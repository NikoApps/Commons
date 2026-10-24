plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.nikoapps.commons"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    lint {
        // Accessibility issues fail the build, as in the apps.
        error += listOf("ContentDescription", "ClickableViewAccessibility", "KeyboardInaccessibleWidget", "LabelFor")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    // BackHandler for the setup frame.
    implementation(libs.androidx.activity.compose)
    // Turns photos upright from their EXIF orientation before cropping.
    implementation(libs.androidx.exifinterface)
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    // Same spellout rules as android.icu, so number words can be tested on the JVM.
    testImplementation(libs.icu4j)
}
