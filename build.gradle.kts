// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    //noinspection GradleDependency
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false // Note: using 1.9.0 to match Compose compiler version
    id("com.google.dagger.hilt.android") version "2.48.1" apply false

}