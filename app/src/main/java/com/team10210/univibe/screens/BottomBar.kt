package com.team10210.univibe.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team10210.univibe.R

sealed class BottomNavItem(var route: String, @DrawableRes var icon: Int, var title: String) {
    data object Home : BottomNavItem(routes.Home, R.drawable.home_icon, "Home")
    data object Map : BottomNavItem(routes.Map, R.drawable.map_icon, "Map")
    data object Calendar : BottomNavItem(routes.Calendar, R.drawable.calendar_icon, "Calendar")
    data object Settings : BottomNavItem(routes.Spinwheel, R.drawable.game_icon, "Spinwheel")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Map,
    BottomNavItem.Calendar,
    BottomNavItem.Settings
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = bottomNavItems.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar (
            containerColor = MaterialTheme.colorScheme.background, // Custom background color
            contentColor = MaterialTheme.colorScheme.onSurface // Custom icon and text color
        ) {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(28.dp) // Set the icon size
                        )
                    },
                    label = { Text(text = item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        // Navigate to the respective screen
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // This will try to navigate to an existing instance of the screen in the stack
                                launchSingleTop = true
                                // Restore state when navigating to a previously visited screen
                                restoreState = true
                                // Avoid creating a new instance if already in the back stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    }

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    // Create a mock NavController for preview purposes
    val navController = rememberNavController()

    // Material Theme wrapper to apply default Material styles
    MaterialTheme {
        BottomNavigationBar(navController = navController)
    }
}