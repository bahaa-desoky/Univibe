package com.team10210.univibe.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.screens.profile.BadgeGrid
import com.team10210.univibe.screens.profile.ProfileViewModel



@Composable
fun BadgeListScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    profileViewModel.fetchBadges()
    BadgeGrid(
        badges = profileViewModel.badges.value,
        modifier = Modifier.padding(paddingValues),
        padding = paddingValues
    )
}

