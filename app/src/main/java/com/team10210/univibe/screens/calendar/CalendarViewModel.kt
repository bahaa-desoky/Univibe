package com.team10210.univibe.screens.calendar

import androidx.lifecycle.ViewModel
import com.team10210.univibe.models.PostModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CalendarViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = Firebase.auth.currentUser

    fun fetchEventsForDate(date: String, onResult: (List<PostModel>) -> Unit) {
        val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
        userQuery
            .get()
            .addOnSuccessListener {
                val document = it.documents.first()
                val school = document.get("university") as String
                db.collection("posts")
                    .whereEqualTo("university", school)
                    .whereEqualTo("event", true)
                    .whereEqualTo("eventDate", date)
                    .get()
                    .addOnSuccessListener { documents ->
                        val events = documents.toObjects(PostModel::class.java)
                        onResult(events)
                    }
                    .addOnFailureListener { exception ->
                        println("Error getting events: $exception")
                        onResult(emptyList())
                    }
            }
            .addOnFailureListener { exception ->
                println("Error getting events: $exception")
                onResult(emptyList())
            }
    }
}