package com.guryon19.photoenhancer.ui.viewmodel

import android.annotation.SuppressLint
import android.net.Uri.parse
import androidx.core.net.toUri

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guryon19.photoenhancer.domain.model.ImageMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * ViewModel for the Image Editor screen that handles image processing logic
 * This class separates the UI from the business logic for better architecture
 */
class EditorViewModel : ViewModel() {

    // State objects for reactive UI updates

    // UI state for the image metrics - stores analysis results
    private val _imageMetrics = mutableStateOf(ImageMetrics())
    // Public immutable state exposed to the UI
    val imageMetrics: State<ImageMetrics> = _imageMetrics

    // UI state for enhanced image - stores the URI of processed image
    private val _enhancedImageUri = mutableStateOf<String?>(null)
    // Public immutable state exposed to the UI
    val enhancedImageUri: State<String?> = _enhancedImageUri

    // Loading state indicators to show progress in the UI
    // True when image analysis is in progress
    private val _isAnalyzing = mutableStateOf(false)
    val isAnalyzing: State<Boolean> = _isAnalyzing

    // True when image enhancement is in progress
    private val _isEnhancing = mutableStateOf(false)
    val isEnhancing: State<Boolean> = _isEnhancing

    /**
     * Analyzes the image at the given URI to extract metrics
     *
     * @param context Android context needed to access content resolver
     * @param imageUri String representation of the image URI to analyze
     */
    fun analyzeImage(context: android.content.Context, imageUri: String) {
        // Launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            // Update loading state to show progress indicator
            _isAnalyzing.value = true

            try {
                // Load the bitmap from URI - moves I/O operation to background
                val bitmap = loadBitmapFromUri(context, imageUri)

                // Perform the analysis in the background thread to avoid UI freezing
                val metrics = withContext(Dispatchers.Default) {
                    // This is a placeholder - will be replaced with actual wavelet transform algorithm
                    calculateImageMetrics(bitmap)
                }

                // Update UI state with the analysis results
                _imageMetrics.value = metrics
            } catch (e: Exception) {
                // Handle errors gracefully
                e.printStackTrace()
                // Could update error state here for error UI display
            } finally {
                // Always reset loading state when done, even if an error occurred
                _isAnalyzing.value = false
            }
        }
    }

    /**
     * Enhances the image using machine learning techniques
     * Currently a placeholder that simulates processing time
     *
     * @param context Android context needed to access content resolver
     * @param imageUri String representation of the image URI to enhance
     */
    fun enhanceImage(context: android.content.Context, imageUri: String) {
        viewModelScope.launch {
            // Update loading state to show progress
            _isEnhancing.value = true

            try {
                // This would be replaced with actual PyTorch enhancement logic
                // For now, just simulate processing time to show loading UI
                withContext(Dispatchers.Default) {
                    // Simulate processing delay of 2 seconds
                    kotlinx.coroutines.delay(2000)
                }

                // For development: just set the same image as "enhanced"
                // In production, this would be the URI to a new, enhanced image
                _enhancedImageUri.value = imageUri
            } catch (e: Exception) {
                // Log any errors during enhancement
                e.printStackTrace()
                // Future enhancement: store error state for UI display
            } finally {
                // Reset loading state when enhancement finishes
                _isEnhancing.value = false
            }
        }
    }

    /**
     * Loads a bitmap from a URI using Android's content resolver
     * Uses IO dispatcher to move this operation off the main thread
     *
     * @param context Android context needed to access content resolver
     * @param uri String representation of the image URI
     * @return Bitmap loaded from the given URI
     */
    private suspend fun loadBitmapFromUri(context: android.content.Context, uri: String): Bitmap = withContext(Dispatchers.IO) {
        // Convert string URI to Uri object using KTX extension function
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri.toUri())
        // Decode the image from the input stream
        BitmapFactory.decodeStream(inputStream)
    }

    /**
     * Calculates image metrics - currently a simplified implementation
     * Will be replaced with wavelet transform algorithm later
     *
     * @param bitmap The bitmap to analyze
     * @return ImageMetrics object containing analysis results
     */
    @SuppressLint("UseKtx")
    private fun calculateImageMetrics(bitmap: Bitmap): ImageMetrics {
        // This is a placeholder for the wavelet transform algorithm
        // For now, just calculate basic brightness metric

        // Accumulators for calculating average brightness
        var totalBrightness = 0f
        var pixelCount = 0

        // Iterate through all pixels in the bitmap
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                // Get the pixel at position (x,y)
                val pixel = bitmap.getPixel(x, y)
                // Extract RGB components (each 0-255)
                val r = (pixel shr 16) and 0xff  // Red component
                val g = (pixel shr 8) and 0xff   // Green component
                val b = pixel and 0xff           // Blue component

                // Simple brightness formula: average of RGB values
                val brightness = (r + g + b) / 3f
                totalBrightness += brightness
                pixelCount++
            }
        }

        // Calculate the average brightness across all pixels
        val avgBrightness = totalBrightness / pixelCount

        // Return metrics object with calculated brightness
        // Other metrics are placeholders for now
        return ImageMetrics(
            sharpness = 0.75f,  // Placeholder - will be calculated with wavelet transform
            noiseLevel = 0.25f, // Placeholder - will be calculated with wavelet transform
            brightness = avgBrightness / 255f, // Scale to 0-1 range
            contrast = 0.5f     // Placeholder - will be calculated with wavelet transform
        )
    }
}