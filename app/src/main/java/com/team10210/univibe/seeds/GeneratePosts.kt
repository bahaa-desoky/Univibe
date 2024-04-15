package com.team10210.univibe.seeds

import android.os.Build
import com.team10210.univibe.models.PostModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

/**
 * Used to populate database with posts
 */
fun generatePosts(db: FirebaseFirestore) {
    usernames.shuffle()
    locations.shuffle()
    for (i in 0..18) {
        val newPostRef = db.collection("posts").document()
        val post = PostModel(
            id = newPostRef.id,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi luctus porta lectus, " +
                    "non laoreet ligula facilisis et. Proin lacinia, ipsum gravida dictum bibendum.",
            likes = i,
            date = getCurrentDate(),
            imageUrl = "https://source.unsplash.com/random",
            deleteImagePath = "",
            username = usernames[i],
            userId = UUID.randomUUID().toString(),
            created = Timestamp.now(),
            address = addresss[i],
            location = locations[i],
            event = false,
            university = "University of Waterloo",
            eventDate = "",
            eventTime = ""
        )

        newPostRef.set(post)
            .addOnSuccessListener { println("Post added")}
            .addOnFailureListener { println("Error writing document") }
    }
}

private fun getCurrentDate(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.now()      // date time object
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        return dateTime.format(formatter)
    }
    return ""
}