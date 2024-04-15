package com.team10210.univibe.screens.createcomment

import androidx.lifecycle.ViewModel
import com.team10210.univibe.models.CommentModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class CreateCommentViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = Firebase.auth.currentUser

    /**
     * Add comment to Firestore database
     */
    fun addCommentToDatabase(
        content: String,
        postId: String,
        onSuccess: () -> Unit
    ) {
        runBlocking {
            val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1).get().await()
            val userDocument = userQuery.documents.first()
            val newCommentRef = db.collection("comments").document()
            val comment = CommentModel(
                id = newCommentRef.id,
                content = content,
                postId = postId,
                username = Firebase.auth.currentUser?.displayName as String,
                authorId = Firebase.auth.currentUser?.uid as String,
                created = Timestamp.now()
            )
            newCommentRef.set(comment)
                .addOnSuccessListener {
                    println("Comment added")
                    db.collection("users").document(userDocument.id)
                        .update("numberOfComments", FieldValue.increment(1))
                        .addOnSuccessListener {
                            println("User numberOfComments updated")
                            onSuccess()
                        }
                }
                .addOnFailureListener { println("Error writing document") }
        }
    }
}
