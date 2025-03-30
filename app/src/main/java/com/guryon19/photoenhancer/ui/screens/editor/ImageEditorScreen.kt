package com.guryon19.photoenhancer.ui.screens.editor

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.guryon19.photoenhancer.ui.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditorScreen(
    imageUri: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val imageMetrics by viewModel.imageMetrics
    val isAnalyzing by viewModel.isAnalyzing
    val isEnhancing by viewModel.isEnhancing
    val enhancedImageUri by viewModel.enhancedImageUri

    // Trigger analysis when the screen is first displayed
    LaunchedEffect(imageUri) {
        viewModel.analyzeImage(context, imageUri)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar
            TopAppBar(
                title = { Text("Image Editor") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Save image */ }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save"
                        )
                    }
                }
            )

            // Image display - show enhanced image if available, otherwise show original
            AsyncImage(
                model = enhancedImageUri ?: imageUri,
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            // Analysis results
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Image Analysis",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isAnalyzing) {
                        // Show loading indicator while analyzing
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        // Show metrics
                        Text("Sharpness: ${(imageMetrics.sharpness * 100).toInt()}%")
                        Text("Noise Level: ${(imageMetrics.noiseLevel * 100).toInt()}%")
                        Text("Brightness: ${(imageMetrics.brightness * 100).toInt()}%")
                        Text("Contrast: ${(imageMetrics.contrast * 100).toInt()}%")
                    }
                }
            }

            // Enhancement actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.enhanceImage(context, imageUri) },
                    enabled = !isEnhancing && !isAnalyzing
                ) {
                    if (isEnhancing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Enhance Image")
                    }
                }

                Button(
                    onClick = { /* Show comparison */ },
                    enabled = enhancedImageUri != null
                ) {
                    Text("Compare Results")
                }
            }
        }
    }
}