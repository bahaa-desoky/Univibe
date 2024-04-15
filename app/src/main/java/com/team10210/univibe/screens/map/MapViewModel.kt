package com.team10210.univibe.screens.map

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.team10210.univibe.models.PostModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MapViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val postList: MutableState<List<PostModel>> = mutableStateOf(emptyList())
    private val currentUser = Firebase.auth.currentUser


    fun fetchMapMarkers() {
        val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
        userQuery
            .get()
            .addOnSuccessListener {
                val document = it.documents.first()
                val school = document.get("university") as String
                db.collection("posts")
                    .whereEqualTo("university", school)
                    .get()
                    .addOnSuccessListener { res ->
                        val posts = mutableListOf<PostModel>()
                        for (post in res) {
                            posts.add(post.toObject<PostModel>())
                        }
                        postList.value = posts
                    }
                    .addOnFailureListener { e ->
                        println("Error getting documents: $e")
                    }
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    suspend fun getUserUniversity(): String? {
        return try {
            val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1).get().await()
            val userDocument = userQuery.documents.first()
            val school = userDocument.get("university") as String
            school
        } catch (e: Exception) {
            println("Error getting documents: $e")
            null
        }
    }
}
