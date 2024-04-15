package com.team10210.univibe.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team10210.univibe.R
import com.team10210.univibe.navigation.UnivibeNavRoutes

val routes = UnivibeNavRoutes()

sealed class TopNavItem(var route: String, @DrawableRes var icon: Int, var title: String) {
    data object Home : TopNavItem(routes.Home, R.drawable.home_icon, "Home")
    data object Map : TopNavItem(routes.Map, R.drawable.map_icon, "Map")
    data object Calendar : TopNavItem(routes.Calendar, R.drawable.calendar_icon, "Calendar")
    data object Settings : TopNavItem(routes.Spinwheel, R.drawable.game_icon, "Spinwheel")
    data object Profile : TopNavItem(routes.Profile, R.drawable.game_icon, "Profile")
    data object LikedPosts : TopNavItem(routes.LikedPosts, R.drawable.game_icon, "Liked Posts")
    data object MyComments : TopNavItem(routes.MyComments, R.drawable.game_icon, "My Comments")
    data object MyPosts : TopNavItem(routes.MyPosts, R.drawable.game_icon, "My Posts")
    data object MyBadges : TopNavItem(routes.BadgeList, R.drawable.game_icon, "My Badges")

}

val topNavItems = listOf(
    TopNavItem.Home,
    TopNavItem.Map,
    TopNavItem.Calendar,
    TopNavItem.Settings,
    TopNavItem.Profile,
    TopNavItem.LikedPosts,
    TopNavItem.MyComments,
    TopNavItem.MyPosts,
    TopNavItem.MyBadges
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = (
        backStackEntry?.destination?.route ?: routes.Home
    )
    val currentDestination = backStackEntry?.destination
    val topBarDestination = topNavItems.any { it.route == currentDestination?.route }
    val currentRoute = currentDestination?.route
    if (topBarDestination) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = currentScreen.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        // Navigate to the respective screen
                        if (currentRoute != TopNavItem.Profile.route) {
                            navController.navigate(TopNavItem.Profile.route) {
                                // This will try to navigate to an existing instance of the screen in the stack
                                launchSingleTop = true
                                // Restore state when navigating to a previously visited screen
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "User Profile", modifier = Modifier.size(48.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors()
        )
    }
}

@Preview
@Composable
fun MainTopBarPreview() {
    val navController = rememberNavController()
    MainTopBar(navController = navController)
}
