package com.team10210.univibe.screens.createpost

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import com.team10210.univibe.exceptions.DataFetchException
import com.team10210.univibe.models.Geocode
import com.team10210.univibe.models.PostModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.UUID

class CreatePostViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val currentUser = Firebase.auth.currentUser

    /**
     * Save image to Firebase storage and get its url link
     */
    fun saveImageToDatabaseAndGetUrl(imageUri: Uri?, onComplete: (String?, String) -> Unit) {
        val storageRef = storage.reference
        val imagePath = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(imagePath)
        val uploadTask = imageUri?.let { imageRef.putFile(it) }

        uploadTask?.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
            val progress =
                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
        }?.addOnSuccessListener {
            // This listener is triggered when the file is uploaded successfully.
            // Using the below code, get the download url of the file
            imageRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    val imageUrl = uri.toString()
                    onComplete(imageUrl, imagePath)
                }
                .addOnFailureListener { e ->
                    onComplete(null, imagePath)
                }
        }?.addOnFailureListener { e ->
            onComplete(null, imagePath)
        }
    }

    /**
     * Add post to Firestore database
     */
    fun addPostToDatabase(
        context: Context,
        description: String,
        address: String,
        imageUrl: String,
        imagePath: String,
        isEventChecked: Boolean,
        eventDate: String,
        eventTime: String,
        onSuccess: () -> Unit
    ) {
        runBlocking {
            val coords = getAddressCoordinates(context, address)
            if (coords != null) {
                val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
                userQuery
                    .get()
                    .addOnSuccessListener {
                        val document = it.documents.first()
                        val school = document.get("university") as String
                        val newPostRef = db.collection("posts").document()
                        val post = PostModel(
                            id = newPostRef.id,
                            description = description,
                            likes = 0,
                            date = getCurrentDate(),
                            imageUrl = imageUrl,
                            deleteImagePath = imagePath,
                            username = Firebase.auth.currentUser?.displayName,
                            userId = Firebase.auth.currentUser?.uid,
                            created = Timestamp.now(),
                            address = address,
                            location = Geocode(coords.latitude, coords.longitude),
                            university = school,
                            event = isEventChecked,
                            eventDate = eventDate,
                            eventTime = eventTime
                        )
                        newPostRef.set(post)
                            .addOnSuccessListener {
                                println("Post added")
                                // Increment the number of posts created
                                db.collection("users").document(document.id)
                                    .update("numberOfPosts", FieldValue.increment(1))
                                    .addOnSuccessListener {
                                        println("User numberOfPosts updated")
                                        onSuccess()
                                    }
                            }
                            .addOnFailureListener { throw DataFetchException("Error creating post") }
                    }
                    .addOnFailureListener { throw DataFetchException("Error creating post")
                    }
            } else {
                throw DataFetchException("Address is invalid")
            }
        }
    }

    /**
     * Get the coordinates of the address provided
     */
    private suspend fun getAddressCoordinates(context: Context, address: String): LatLng? {
        val geoCoder = Geocoder(context, Locale.getDefault())
        var coords: LatLng? = null
        val addr = geoCoder.getFromLocationName(address, 1)
        if (addr != null) {
            coords = if (addr.isNotEmpty()) {
                val location = addr[0]
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        }
        return coords
    }

    /**
     * Delete image from database
     */
    fun deleteImage(imagePath: String) {
        storage.reference.child(imagePath).delete()
            .addOnSuccessListener {
                println("Post deleted")
            }
            .addOnFailureListener {
                println("Failed to delete post")
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
}
