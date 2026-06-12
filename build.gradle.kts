// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // allow for KSP to be applied
    alias(libs.plugins.com.google.devtools.ksp) apply false
}