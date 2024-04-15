package com.team10210.univibe.screens.homepage

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.models.PostModel
import com.team10210.univibe.screens.BadgeDialog

@Composable
fun HomeScreen(
    onPostClick: (String) -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
    paddingValues: PaddingValues
) {
    var posts by remember { mutableStateOf<List<PostModel>>(emptyList()) }
    val firstPostBadgeDialog = remember { mutableStateOf(false) }

    // key1 = true runs the Launched effect each time
    LaunchedEffect(key1 = true) {
        posts = homeViewModel.fetchPosts(onSuccess = {firstPostBadgeDialog.value = true})
    }

    // Alert dialog for the user's first post
    when {
        firstPostBadgeDialog.value -> {
            BadgeDialog(
                onConfirmation = {
                    firstPostBadgeDialog.value = false
                },
                dialogTitle = "First Post Badge Earned!",
                dialogText = "Congratulations! You earned a badge for posting for the first time. Check out your badges in your profile page",
                iconUrl = "https://cdn-icons-png.freepik.com/512/5638/5638179.png"
            )
        }
    }

    PostList(
        posts = posts,
        onPostClick = onPostClick,
        homeViewModel = homeViewModel,
        paddingValues = paddingValues
    )
}
