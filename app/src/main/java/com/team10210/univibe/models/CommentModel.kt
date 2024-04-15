package com.team10210.univibe.models

import com.google.firebase.Timestamp

data class CommentModel(
    val id: String,
    val content: String,
    val postId: String,
    val authorId: String,
    val username: String,
    val created: Timestamp?,
)
{
    constructor() : this("", "", "", "", "", null)
}