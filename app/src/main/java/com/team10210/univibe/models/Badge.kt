package com.team10210.univibe.models

data class Badge(
        val id: String = "", // Unique identifier for the badge
        val name: String = "", // The display name of the badge
        val description: String = "", // A description of what the badge is for
        val iconUrl: String = "", // A URL to an image for the badge icon
        val criteria: String = "" // A description of the criteria needed to earn the badge
){

}