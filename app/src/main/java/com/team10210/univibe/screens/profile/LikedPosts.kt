package com.team10210.univibe.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.screens.homepage.PostList

@Composable
fun LikedPostsScreen(
    onPostClick: (String) -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    paddingValues: PaddingValues
) {
    profileViewModel.fetchFavoritePosts()
    PostList(
        posts = profileViewModel.favPostList.value,
        onPostClick = onPostClick,
        homeViewModel = viewModel(),
        paddingValues = paddingValues
    )
}