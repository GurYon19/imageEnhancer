package com.guryon19.photoenhancer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.guryon19.photoenhancer.ui.navigation.PhotoEnhancerNavHost
import com.guryon19.photoenhancer.ui.theme.PhotoEnhancerTheme

// Temporarily remove @AndroidEntryPoint to debug without Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoEnhancerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    PhotoEnhancerNavHost(navController = navController)
                }
            }
        }
    }
}