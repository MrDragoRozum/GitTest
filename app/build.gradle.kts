plugins {
    alias(libs.plugins.hilt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "ru.rozum.gitTest"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.rozum.gitTest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // View Binding
    buildFeatures {
        viewBinding = true
    }
}

kapt {
    correctErrorTypes = true
}

configurations.all {
    exclude("org.jetbrains", "annotations-java5")
}

dependencies {

    // ViewBindingPropertyDelegate
    implementation(libs.viewbindingpropertydelegate.noreflection)

    // Sandwich
    implementation(libs.sandwich.retrofit)
    implementation(libs.sandwich)

    // androidx.security
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.security.crypto.ktx.v110alpha06)

    // MarkWon
    implementation(libs.core)
    implementation(libs.ext.tasklist)
    implementation(libs.linkify)
    implementation(libs.ext.tables)
    implementation(libs.image)
    implementation(libs.syntax.highlight)
    kapt(libs.prism4j.bundler)

    // Jetpack Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Kotlin coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Jetpack Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit + kotlin-serialization + okHttp
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.converter.scalars)
    implementation(libs.okhttp)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
}