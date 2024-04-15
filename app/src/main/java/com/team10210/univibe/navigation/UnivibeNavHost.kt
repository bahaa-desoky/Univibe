package com.team10210.univibe.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.team10210.univibe.screens.BadgeListScreen
import com.team10210.univibe.screens.BottomNavigationBar
import com.team10210.univibe.screens.MainTopBar
import com.team10210.univibe.screens.calendar.CalendarScreen
import com.team10210.univibe.screens.createcomment.CreateCommentScreen
import com.team10210.univibe.screens.createpost.CreatePostScreen
import com.team10210.univibe.screens.homepage.HomeScreen
import com.team10210.univibe.screens.homepage.PostDetailScreen
import com.team10210.univibe.screens.login.LoginScreen
import com.team10210.univibe.screens.map.MapScreen
import com.team10210.univibe.screens.onboarding.OnboardingScreen
import com.team10210.univibe.screens.profile.LikedPostsScreen
import com.team10210.univibe.screens.profile.MyCommentsScreen
import com.team10210.univibe.screens.profile.MyPostsScreen
import com.team10210.univibe.screens.profile.Profile
import com.team10210.univibe.screens.register.RegisterScreen
import com.team10210.univibe.screens.spinwheel.SpinWheelScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.random.Random

@Composable
fun UnivibeNavHost(
    navController: NavHostController
) {
    val routes = UnivibeNavRoutes()
    var startDestination = routes.Auth
    val currentUser = Firebase.auth.currentUser
    val userCreationTime = Firebase.auth.currentUser?.metadata?.creationTimestamp
    val userLastLogIn = Firebase.auth.currentUser?.metadata?.lastSignInTimestamp
    if (currentUser != null) {
        startDestination = routes.Main
    }

    // Add scaffold app bar here
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Log in / Sign up navigation
        navigation(
            startDestination = routes.LogIn,
            route = routes.Auth
        ) {
            composable(
                route = routes.LogIn
            ) {
                LoginScreen(
                    onLogin = {
                        // Also need to check for auth validations here
                        navController.navigate(routes.Main)  {
                            // If logged in, we want to pop everything from auth NavGraph
                            popUpTo(route = routes.Auth) {
                                inclusive = true
                            }
                        }
                    },
                    onSignUpButtonClicked = {
                        navController.navigate(routes.SignUp)
                    }
                )
            }
            composable(
                route = routes.SignUp
            ) {
                RegisterScreen(
                    onSignUp = { navController.navigate(routes.Onboarding) }
                )
            }
            composable(
                route = routes.Onboarding
            ) {
                OnboardingScreen(
                    onHomepageButtonClicked = {
                        navController.navigate(routes.Main) {
                            popUpTo(route = routes.Auth) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
        composable(routes.Main) {
            val navControllerHome = rememberNavController()
            val backStackEntry by navControllerHome.currentBackStackEntryAsState()
            val currentScreen =
                backStackEntry?.destination?.route ?: routes.LogIn

            ScaffoldWithBottomBar(navController = navControllerHome, currentScreen = currentScreen)
            { paddingValues ->
                HomeNavGraph(
                    homeNavController = navControllerHome,
                    rootNavController = navController,
                    paddingValues = paddingValues,
                    currentScreen = currentScreen
                )
            }
        }
    }
}
@Composable
fun HomeNavGraph(
    homeNavController: NavHostController,
    rootNavController: NavController,
    paddingValues: PaddingValues,
    currentScreen: String
) {
    val routes = UnivibeNavRoutes()
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = homeNavController,
        route = routes.Main,
        startDestination = routes.Home
    ) {

        composable(route = routes.Home) {
            HomeScreen(
                onPostClick = { postId ->
                    homeNavController.navigate(routes.Details + postId)
                },
                paddingValues = paddingValues
            )
        }
        composable(route = routes.Map) {
            MapScreen(
                paddingValues = paddingValues,
                onMarkerClick = { postId ->
                    homeNavController.navigate(routes.Details + postId)
                }
            )
        }
        composable(route = routes.Calendar) {
            CalendarScreen()
        }
        composable(route = routes.Spinwheel) {
            SpinWheelScreen()
        }
        composable(route = routes.BadgeList) {
            BadgeListScreen(paddingValues = paddingValues)
        }
        composable(
            route = routes.Details + "{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            val postId = it.arguments?.getString("postId")
            PostDetailScreen(
                postId = postId as String,
                onAddCommentClick = { homeNavController.navigate(routes.CreateComment + postId) },
                goBackHome = {
                    homeNavController.navigate(routes.Home) {
                        popUpTo(route = routes.Main) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = routes.CreatePost) {
            CreatePostScreen {
                homeNavController.navigate(routes.Home) {
                    popUpTo(route = routes.Main) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = routes.CreateComment + "{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            val postId = it.arguments?.getString("postId")
            CreateCommentScreen(
                postId = postId as String,
                goBackToPost = { homeNavController.navigate(routes.Details + postId) {
                    popUpTo(route = routes.Details + postId) {
                        inclusive = true
                    }
                }}
            )
        }
        composable(route = routes.Profile) {
            Profile(
                onLogout = {
                    rootNavController.navigate(routes.Auth) {
                        popUpTo(route = routes.Main) {
                            inclusive = true
                        }
                    }
               },
                onLikedPostsClicked = { homeNavController.navigate(routes.LikedPosts) },
                onCommentsClicked = { homeNavController.navigate(routes.MyComments) },
                onBadgeButtonClick = {homeNavController.navigate(routes.BadgeList)},
                onMyPostsClicked = { homeNavController.navigate(routes.MyPosts) },
                paddingValues = paddingValues
            )
        }
        composable(route = routes.LikedPosts) {
            LikedPostsScreen(
                onPostClick = {postId -> homeNavController.navigate(routes.Details + postId) },
                paddingValues = paddingValues
            )
        }
        composable(route = routes.MyComments) {
            MyCommentsScreen(
                onCommentClick = {postId -> homeNavController.navigate(routes.Details + postId) },
                paddingValues = paddingValues
            )
        }
        composable(route = routes.MyPosts) {
            MyPostsScreen(
                onPostClick = {postId -> homeNavController.navigate(routes.Details + postId) },
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun ScaffoldWithBottomBar(
    navController: NavHostController,
    currentScreen: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val routes = UnivibeNavRoutes()
    Scaffold(
        topBar = { MainTopBar(navController = navController) },
        bottomBar = { BottomNavigationBar(navController) },
        content = content,
        floatingActionButton = {
            if (currentScreen.toString() == routes.Home) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(), // Adjust padding as needed
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Random Post FAB (to the left)
                    FloatingActionButton(
                        modifier = Modifier.padding(start = 48.dp),
                        onClick = { fetchRandomPostId { randomPostId ->
                            randomPostId?.let {
                                navController.navigate(routes.Details + randomPostId)
                            }
                        } },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Random Post")
                    }

                    // Create Post FAB (to the right)
                    FloatingActionButton(
                        modifier = Modifier.padding(end = 16.dp),
                        onClick = { navController.navigate(routes.CreatePost) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Create Post")
                    }
                }
            }
        }
    )
}

fun fetchRandomPostId(onResult: (String?) -> Unit) {
    val currentUser = Firebase.auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val userQuery = db.collection("users").whereEqualTo("userId", currentUser?.uid).limit(1)
    userQuery
        .get()
        .addOnSuccessListener {
            val document = it.documents.first()
            val school = document.get("university") as String
            val allPosts = Firebase.firestore.collection("posts")
            allPosts
                .whereEqualTo("university", school)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        onResult(null)
                    } else {
                        val posts = snapshot.documents
                        val randomIndex = Random.nextInt(posts.size)
                        val randomID = posts[randomIndex].id
                        onResult(randomID)
                    }
                }
                .addOnFailureListener { e ->
                    println("Error getting random post: $e")
                    onResult(null)
                }
        }
        .addOnFailureListener { e ->
            println("Error getting random post: $e")
            onResult(null)
        }
}