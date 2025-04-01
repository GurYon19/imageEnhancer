package com.guryon19.photoenhancer.ui.screens.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

// This annotation tells the compiler that we're using experimental Material3 API features
@OptIn(ExperimentalMaterial3Api::class)
@Composable // This marks the function as a Compose UI component
fun CameraScreen(
    // This function will be called when an image is captured, with the image URI as parameter
    onImageCaptured: (String) -> Unit,
    // This function will be called when the back button is clicked
    onBackClick: () -> Unit,
    // Optional modifier for flexible UI customization
    modifier: Modifier = Modifier
) {
    // Get the current Android context for system operations
    val context = LocalContext.current
    // Get the current lifecycle owner for camera operations
    val lifecycleOwner = LocalLifecycleOwner.current

    // State variable to track if we have camera permission
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Create a permission request launcher that updates our state when permission is granted or denied
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            // Show a message if permission is denied
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    // This effect runs once when the screen is displayed (because Unit never changes)
    // It automatically requests camera permission
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Create an executor for running camera operations on the main thread
    val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }

    // Create an image capture use case that will be used to take photos
    val imageCapture = remember { ImageCapture.Builder().build() }

    // The screen's main container
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar with a back button
            TopAppBar(
                title = { Text("Camera") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

            // Show different content based on whether we have camera permission
            if (hasCameraPermission) {
                // Camera preview takes most of the screen space
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Takes up available space
                    contentAlignment = Alignment.Center
                ) {
                    // Custom camera preview component
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        imageCapture = imageCapture,
                        lifecycleOwner = lifecycleOwner
                    )
                }

                // Camera control buttons at the bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            // Call the captureImage function when button is clicked
                            captureImage(
                                context = context,
                                imageCapture = imageCapture,
                                executor = cameraExecutor,
                                onImageCaptured = onImageCaptured
                            )
                        }
                    ) {
                        Text("Capture")
                    }
                }
            } else {
                // If we don't have camera permission, show this message instead
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Camera permission is required to use this feature")
                }
            }
        }
    }
}

// This composable function displays the camera preview
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    val context = LocalContext.current

    // AndroidView is a Compose function that lets us embed traditional Android Views
    // In this case, we're using it to include the camera preview
    AndroidView(
        modifier = modifier,
        // This factory lambda creates and configures our Android View (PreviewView)
        factory = { ctx ->
            // Create the preview view that will display the camera feed
            val previewView = PreviewView(ctx)
            // Get a future that will provide the camera when it's available
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            // Add a listener that will be called when the camera is available
            cameraProviderFuture.addListener({
                // Get the camera provider from the future
                val cameraProvider = cameraProviderFuture.get()

                // Set up the preview use case
                val preview = Preview.Builder().build().also {
                    // Connect the preview to our preview view's surface
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Set up to use the back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind any previous use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to the camera: preview for viewing, imageCapture for taking photos
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    // Handle any errors
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx)) // Run on main thread

            // Return the preview view as the result of the factory function
            previewView
        }
    )
}

// This function captures an image using the camera
private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (String) -> Unit
) {
    // Create a file with a timestamp name to store the photo
    val photoFile = File(
        context.cacheDir, // Store in the app's cache directory
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    // Set up options for saving the image
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // Take the picture
    imageCapture.takePicture(
        outputOptions,
        executor, // Use main thread executor
        // This anonymous object handles the result of taking a picture
        object : ImageCapture.OnImageSavedCallback {
            // Called if there's an error taking the picture
            override fun onError(e: ImageCaptureException) {
                Toast.makeText(
                    context,
                    "Photo capture failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Called when the image is successfully saved
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Create a URI from the saved file
                val savedUri = Uri.fromFile(photoFile)
                // Call the callback function with the image URI as a string
                onImageCaptured(savedUri.toString())
            }
        }
    )
}