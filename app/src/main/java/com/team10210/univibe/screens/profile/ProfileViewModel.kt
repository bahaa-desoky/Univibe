package com.team10210.univibe.screens.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.team10210.univibe.models.Badge
import com.team10210.univibe.models.CommentModel
import com.team10210.univibe.models.PostModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Calendar
import java.util.concurrent.TimeUnit


// List of available badges
val BADGES = listOf(
    Badge(
        id = "welcome",
        criteria = "User created an account",
        description = "Welcome to Univibe!",
        iconUrl = "https://tr.rbxcdn.com/0743acc8128012ba833e6d406812a54e/420/420/Image/Png",
        name = "Welcome Badge"
    ),
    Badge(
        id = "post",
        criteria = "User Created a post",
        description = "Created a Post!",
        iconUrl = "https://cdn-icons-png.freepik.com/512/5638/5638179.png",
        name = "First Post Badge"
    ),
    Badge(
        id = "comment",
        criteria = "User created a comment",
        description = "Added a Comment!",
        iconUrl = "https://cdn-icons-png.flaticon.com/512/1069/1069870.png",
        name = "First Comment Badge"
    ),
    Badge(
        id = "wheel",
        criteria = "User spun the wheel",
        description = "Spin the Wheel!",
        iconUrl = "https://cdn-icons-png.flaticon.com/512/5288/5288862.png",
        name = "Spin the Wheel Badge"
    )
)

class ProfileViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = Firebase.auth.currentUser
    val favPostList: MutableState<List<PostModel>> = mutableStateOf(emptyList())
    val myPostList: MutableState<List<PostModel>> = mutableStateOf(emptyList())
    val commentList: MutableState<List<CommentModel>> = mutableStateOf(emptyList())

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
    fun fetchFavoritePosts() {
        val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
        userQuery.get().addOnSuccessListener {
            val document = it.documents.first()
            val likedPosts = document.get("likedPosts") as MutableList<*>
            // Need to check if likedPosts is not empty, otherwise it will crash
            if (likedPosts.isNotEmpty()) {
                db.collection("posts")
                    .whereIn(FieldPath.documentId(), likedPosts)
                    .get()
                    .addOnSuccessListener { res ->
                        val posts = mutableListOf<PostModel>()
                        for (post in res) {
                            val postObject = post.toObject<PostModel>()
                            postObject.isLiked = postObject.id in likedPosts
                            posts.add(postObject)
                        }
                        favPostList.value = posts
                    }
                    .addOnFailureListener { e ->
                        println("Error getting documents: $e")
                    }
            }
        }
    }

    val spinAvailable: MutableState<Boolean> = mutableStateOf(false)
    val daysLeft: MutableState<Long> = mutableStateOf(100)
    private fun canSpin(lastSpinTime: Timestamp?): Boolean {
        if (lastSpinTime == null) {
            return true
        }
        val timestampDate = lastSpinTime.toDate()
        val calendar = Calendar.getInstance()
        calendar.time = timestampDate

        val currentCalendar = Calendar.getInstance()
        val diff = currentCalendar.timeInMillis - calendar.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        daysLeft.value = days
        return days >= 7
    }
    private val lastSpin: MutableState<Timestamp?> = mutableStateOf(null)
    fun fetchLastSpin() {
        val currentUserUid = currentUser?.uid
        if (currentUserUid == null) {
            Log.d("ProfileViewModel", "User UID is null.")
            return
        }
        Log.d("ProfileViewModel", "Fetching last spin for user UID: $currentUserUid")
        db.collection("users").whereEqualTo("userId", currentUserUid).limit(1).get().addOnSuccessListener { querySnapshot ->
            // Check if the query returned any documents
            if (!querySnapshot.isEmpty) {
                // Get the first document from the snapshot
                val document = querySnapshot.documents.first()
                val lastSpinTime = document.get("lastSpinWheelTime") as? Timestamp
                lastSpin.value = lastSpinTime
                spinAvailable.value = canSpin(lastSpinTime)
                Log.d("ProfileViewModel", "Last spin time fetched: ${lastSpin.value?.toDate()} and can spin is ${canSpin(lastSpin.value)}")
            } else {
                Log.d("ProfileViewModel", "Document does not exist.")
            }
        }.addOnFailureListener { exception ->
            Log.d("ProfileViewModel", "Error fetching last spin time: $exception")
        }
    }


    fun updateLastSpin() {
        val currentUserUid = currentUser?.uid ?: return
        val currentTime = Timestamp.now()
        val db = FirebaseFirestore.getInstance()
        val userQuery = db.collection("users").whereEqualTo("userId", currentUserUid).limit(1)
        userQuery.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.documents.isNotEmpty()) {
                val userDocument = querySnapshot.documents.first()
                userDocument.reference.update("lastSpinWheelTime", currentTime)
                    .addOnSuccessListener {
                        Log.d("UpdateSuccess", "Last spin time updated successfully for user $currentUserUid oh wait it is")
                        // Success handling code here
                    }
                    .addOnFailureListener { e ->
                        Log.e("UpdateFailure", "Failed to update last spin wheel time for user $currentUserUid", e)
                        // Failure handling code here
                    }
            } else {
                Log.e("UpdateFailure", "No user found with userId $currentUserUid")
                // Handling the case where the user doesn't exist
            }
        }.addOnFailureListener { e ->
            Log.e("QueryFailure", "Failed to fetch user for userId $currentUserUid", e)
            // Failure handling
        }
    }

    val badges = mutableStateOf<List<Badge>>(emptyList())

    fun fetchBadges() {
        val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
        userQuery.get().addOnSuccessListener {
            val document = it.documents.first()
            val badgeIds = document.get("badgeIds") as List<String>
            val userBadges = mutableListOf<Badge>()
            for (badge in BADGES) {
                if (badge.id in badgeIds) {
                    userBadges.add(badge)
                }
            }
            badges.value = userBadges
        }
    }

    fun fetchMyComments() {
        db.collection("comments")
            .orderBy("created", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { res ->
                val comments = mutableListOf<CommentModel>()
                for (comment in res) {
                    val commentObject = comment.toObject<CommentModel>()
                    if (commentObject.authorId == currentUser?.uid) {
                        comments.add(commentObject)
                    }
                }
                commentList.value = comments
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun fetchMyPosts() {
        db.collection("posts")
            .orderBy("created", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { res ->
                val posts = mutableListOf<PostModel>()
                for (post in res) {
                    val postObject = post.toObject<PostModel>()
                    if (postObject.userId == currentUser?.uid) {
                        posts.add(postObject)
                    }
                }
                myPostList.value = posts
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

}