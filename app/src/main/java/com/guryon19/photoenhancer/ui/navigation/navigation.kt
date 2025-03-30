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

/**
 * Defines all the possible screen destinations in the app.
 * Each screen is represented by a route string that uniquely identifies it.
 */
sealed class Screen(val route: String) {
    // Home screen - the app's starting point
    object Home : Screen("home")

    // Camera screen - for capturing photos
    object Camera : Screen("camera")

    // Editor screen - receives an image URI as a parameter
    object Editor : Screen("editor/{imageUri}") {
        /**
         * Creates a navigation route with the image URI parameter.
         * Encodes the URI to ensure special characters don't break navigation.
         */
        fun createRoute(imageUri: String): String {
            // Encode the URI to make it URL-safe
            val encodedUri = Uri.encode(imageUri)
            return "editor/$encodedUri"
        }
    }
}

/**
 * The main navigation component that sets up the app's navigation structure.
 * It defines the NavHost and all the possible screen destinations.
 *
 * @param navController The controller that manages app navigation
 */
@Composable
fun PhotoEnhancerNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route  // App starts at the Home screen
    ) {
        // Home screen destination
        composable(Screen.Home.route) {
            HomeScreen(
                // When camera button is clicked, navigate to Camera screen
                onCameraClick = { navController.navigate(Screen.Camera.route) }
            )
        }

        // Camera screen destination
        composable(Screen.Camera.route) {
            CameraScreen(
                // When an image is captured, navigate to Editor with the image URI
                onImageCaptured = { imageUri ->
                    navController.navigate(Screen.Editor.createRoute(imageUri))
                },
                // When back button is pressed, go back to previous screen
                onBackClick = { navController.navigateUp() }
            )
        }

        // Editor screen destination with URI parameter
        composable(
            route = Screen.Editor.route,
            // Define argument type for the imageUri parameter
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            // Extract the URI parameter from navigation arguments
            val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
            // Decode the URI back to its original form
            val decodedUri = Uri.decode(imageUri)

            ImageEditorScreen(
                imageUri = decodedUri,
                // When back button is pressed, go back to previous screen
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}