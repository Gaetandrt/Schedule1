plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version libs.versions.kspVersion.get() apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
}