package com.team10210.univibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.team10210.univibe.navigation.UnivibeNavHost
import com.team10210.univibe.ui.theme.UnivibeTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            //val db = FirebaseFirestore.getInstance()
            //generatePosts(db)
            UnivibeApp()
        }
    }

    @Composable
    fun UnivibeApp() {
        UnivibeTheme {
            // Navigation controller for app
            val navController = rememberNavController()

            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                UnivibeNavHost(navController = navController)
            }
        }
    }
}