package com.team10210.univibe.screens.homepage

import androidx.lifecycle.ViewModel
import com.team10210.univibe.exceptions.DataFetchException
import com.team10210.univibe.models.CommentModel
import com.team10210.univibe.models.PostModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class HomeViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = Firebase.auth.currentUser
    private val storage = com.google.firebase.ktx.Firebase.storage

    fun isPostFromCurrentUser(postUserId: String?): Boolean {
        val currentUserId = currentUser?.uid
        return currentUserId == postUserId
    }

    fun deletePost(postId: String, imagePath: String) {
        runBlocking {
            val comments = db.collection("comments").whereEqualTo("postId", postId).get().await()
            for (comment in comments) {
                comment.reference.delete()
            }
            db.collection("posts").document(postId)
                .delete()
                .addOnSuccessListener {
                    deleteImage(imagePath)
                    println("deleted")
                }
                .addOnFailureListener{
                    throw DataFetchException("Unable to delete post")
                }
        }
    }

    private fun deleteImage(imagePath: String) {
        storage.reference.child(imagePath).delete()
            .addOnSuccessListener {
                println("Post deleted")
            }
            .addOnFailureListener {
                throw DataFetchException("Unable to delete image")
            }
    }
    suspend fun fetchPosts(onSuccess: () -> Unit): List<PostModel> {
        return try {
            val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1).get().await()
            val userDocument = userQuery.documents.first()
            val likedPosts = userDocument.get("likedPosts") as List<String>
            val school = userDocument.get("university") as String
            val badgeIds = userDocument.get("badgeIds") as MutableList<String>
            val numberOfPosts = userDocument.get("numberOfPosts") as Long

            // Check if its the user's first time posting (numberOfPosts is exactly 1)
            // If so, give an alert and award the badge
            if (numberOfPosts.toInt() == 1 && "post" !in badgeIds) {
                badgeIds += "post"
                db.collection("users").document(userDocument.id)
                    .update("badgeIds", badgeIds)
                    .addOnSuccessListener {
                        onSuccess()
                    }
            }

            // Get all the posts
            val documents = db.collection("posts")
                .whereEqualTo("university", school)
                .orderBy("created", Query.Direction.DESCENDING).get().await()
            val posts = mutableListOf<PostModel>()
            for (post in documents) {
                val postObject = post.toObject<PostModel>()
                if (postObject.id in likedPosts) {
                    postObject.isLiked = true
                }
                posts.add(postObject)
            }
            posts
        } catch (e: Exception) {
            println("Error getting posts document: $e")
            mutableListOf<PostModel>()
        }
    }

    suspend fun fetchPost(postId: String, onSuccess: () -> Unit): PostModel? {
        return try {
            val document = db.collection("posts").document(postId).get().await()
            val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1).get().await()
            val userDocument = userQuery.documents.first()
            val likedPosts = userDocument.get("likedPosts") as List<String>
            val post = document.toObject<PostModel>()
            val badgeIds = userDocument.get("badgeIds") as MutableList<String>
            val numberOfComments = userDocument.get("numberOfComments") as Long

            // Check if its the user's first time commenting (numberOfComments is exactly 1)
            // If so, give an alert and award the badge
            if (numberOfComments.toInt() == 1 && "comment" !in badgeIds) {
                badgeIds += "comment"
                db.collection("users").document(userDocument.id)
                    .update("badgeIds", badgeIds)
                    .addOnSuccessListener {
                        onSuccess()
                    }
            }

            if (post != null) {
                if (post.id in likedPosts) {
                    post.isLiked = true
                }
            }
            post
        } catch (e: Exception) {
            println("Error getting document: $e")
            null
        }
    }

    suspend fun fetchAllComments(postId: String): List<CommentModel>? {
        return try {
            val res = db.collection("comments").orderBy("created", Query.Direction.DESCENDING).get().await()
            val comments = mutableListOf<CommentModel>()
            for (comment in res) {
                val commentObject = comment.toObject<CommentModel>()
                if (commentObject.postId == postId) {
                    comments.add(commentObject)
                }
            }
            comments
        } catch (e: Exception) {
            println("Error getting documents: $e")
            null
        }
    }

    fun updateLikes(postId: String, likesCount: Int, isLike: Boolean) {
        val postRef = db.collection("posts").document(postId)
        val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
        postRef.update("likes", likesCount)
            .addOnSuccessListener { res -> println("Success: $res") }
            .addOnFailureListener { e -> println("Error: $e")}
        userQuery.get().addOnSuccessListener {
            val documentId = it.documents.first().id
            // add or remove the post from the user's liked posts
            val likedPosts = if (isLike) FieldValue.arrayUnion(postId) else FieldValue.arrayRemove(postId)
            db.collection("users").document(documentId)
                .update("likedPosts", likedPosts)
                .addOnSuccessListener { res -> println("Success: $res") }
                .addOnFailureListener { e -> println("Error: $e") }
        }
    }
}