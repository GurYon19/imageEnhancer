package com.guryon19.photoenhancer.domain.model

data class ImageMetrics(
    val sharpness: Float = 0f,
    val noiseLevel: Float = 0f,
    val brightness: Float = 0f,
    val contrast: Float = 0f
)