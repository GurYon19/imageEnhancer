package com.guryon19.photoenhancer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guryon19.photoenhancer.ui.screens.camera.CameraScreen
import com.guryon19.photoenhancer.ui.screens.editor.ImageEditorScreen
import com.guryon19.photoenhancer.ui.screens.home.HomeScreen
import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Editor : Screen("editor/{imageUri}") {
        fun createRoute(imageUri: String): String {
            // Encode the URI to make it URL-safe
            val encodedUri = Uri.encode(imageUri)
            return "editor/$encodedUri"
        }
    }
}

@Composable
fun PhotoEnhancerNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCameraClick = { navController.navigate(Screen.Camera.route) }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { imageUri ->
                    navController.navigate(Screen.Editor.createRoute(imageUri))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
            val decodedUri = Uri.decode(imageUri)
            ImageEditorScreen(
                imageUri = decodedUri,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}