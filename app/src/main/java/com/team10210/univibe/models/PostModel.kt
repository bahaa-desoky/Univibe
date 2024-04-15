package com.team10210.univibe.models

import com.google.firebase.Timestamp

data class PostModel(
    val id: String,
    val description: String,
    val likes: Int,
    val date: String,
    val imageUrl: String,
    val deleteImagePath: String,
    val username: String?,
    val userId: String?,
    val created: Timestamp?,
    val address: String,
    val location: Geocode?,
    val university: String,

    // Event post fields
    val event: Boolean,
    val eventDate: String,
    val eventTime: String,
    var isLiked: Boolean = false
)
    {
    constructor() : this("", "", 0, "", "", "", null,
        null, null, "", null, "", false, "", "")
}

data class Geocode(
    val latitude: Double,
    val longitude: Double
) {
    constructor() : this(0.0, 0.0)
}