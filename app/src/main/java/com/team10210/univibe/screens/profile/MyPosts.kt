package com.team10210.univibe.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.screens.homepage.PostList

@Composable
fun MyPostsScreen(
    onPostClick: (String) -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    paddingValues: PaddingValues
) {
    profileViewModel.fetchMyPosts()
    PostList(
        posts = profileViewModel.myPostList.value,
        onPostClick = onPostClick,
        homeViewModel = viewModel(),
        paddingValues = paddingValues
    )
}