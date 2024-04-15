package com.team10210.univibe.models

import com.google.firebase.Timestamp

data class UserModel(
    val id: String,
    val comments: List<String>,
    val likedPosts: List<String>,
    val userId: String?,
    val created: Timestamp?,
    val badgeIds: List<String>,
    val posts: List<String>,
    val university: String,
    val lastSpinWheelTime: Timestamp? = null,
    val numberOfPosts: Int,
    val numberOfComments: Int,
    val numberOfLikes: Int
)