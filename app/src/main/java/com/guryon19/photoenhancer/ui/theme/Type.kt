package com.guryon19.photoenhancer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Typography defines the text styles used throughout the app
// This helps maintain consistent text appearance across different screens
val Typography = Typography(
    // Style for body text (main content)
    bodyLarge = TextStyle(
        // FontFamily.Default uses the system's default font
        fontFamily = FontFamily.Default,
        // Normal weight means the text is neither bold nor light
        fontWeight = FontWeight.Normal,
        // Font size in scaled pixels (16sp is a common size for body text)
        fontSize = 16.sp,
        // Line height determines the vertical space between lines
        lineHeight = 24.sp,
        // Letter spacing controls the horizontal space between characters
        letterSpacing = 0.5.sp
    ),

    // Style for large titles (screen headings, section titles)
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        // Larger font size for titles to make them stand out
        fontSize = 22.sp,
        // More line height for titles gives them more breathing room
        lineHeight = 28.sp,
        // Zero letter spacing keeps the title text more compact
        letterSpacing = 0.sp
    ),

    // Style for small labels (like button text, captions)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        // Medium weight makes small text more readable
        fontWeight = FontWeight.Medium,
        // Smaller size for labels and annotations
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    // Note: Material3 Typography includes many other text styles
    // like titleMedium, bodySmall, labelLarge, etc.
    // You can add more styles here as your app design requires
)