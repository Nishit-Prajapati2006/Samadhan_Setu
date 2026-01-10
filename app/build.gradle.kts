import java.util.Properties // Import the correct Properties class

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

// Create a new Properties object and load the local.properties file
val properties = Properties() // Use java.util.Properties
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
} // The 'if' block was not properly closed. It ends here.

android {
    namespace = "com.example.samadhansetu"
    // Update compileSdk to a stable version like 34 unless you need Android 15 features
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.samadhansetu"
        minSdk = 26
        // It's best practice to match targetSdk with compileSdk
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // Get property and provide a default empty string value
//        buildConfigField("String", "GEMINI_API_KEY", properties.getProperty("GEMINI_API_KEY", "\"\""))
        buildConfigField("String", "GEMINI_API_KEY", "\"${properties.getProperty("GEMINI_API_KEY", "DEFAULT_KEY")}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    // Note: cardview-v7 is an old support library. Consider replacing with androidx.cardview:cardview:1.0.0
    implementation(libs.cardview.v7)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google AI (for Gemini)
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")

    // Kotlin Coroutines for asynchronous tasks (needed for Gemini)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Lifecycle KTX for lifecycleScope (also for Coroutines)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // This is a Room compiler but you don't have the main Room dependency.
    // I am removing it to prevent potential issues. Add it back if you use Room DB.
    // implementation(libs.androidx.room.compiler)
}