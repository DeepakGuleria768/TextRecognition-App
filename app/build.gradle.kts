plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.ocr"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ocr"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

     // ML kit text recognition

    //What it is-->: This line adds Google's ML Kit Text Recognition library (version 16.0.0) to your Android application.
    // This library contains pre-trained machine learning models specifically designed to identify and extract text from images.

    //Why you use it-->: You use it to enable your app to automatically "read" text from photos or camera feeds. This is useful for features like:
    //Scanning documents or receipts to extract information.
    //Digitizing handwritten notes or whiteboards.
    //Implementing live translation by reading text from signs.
    //Creating apps that can scan business cards to automatically save contact details.
    implementation(libs.mlKit.text)

    // Camera X

    //What--> it does: This is the core engine of CameraX. You know how Android has a low-level camera API called "Camera2"?
    // It's powerful, but it's also a nightmare to use directly – super complex, lots of boilerplate code, and easy to make mistakes.
    // This camera-camera2 module is like a wrapper around that complicated Camera2 API. It takes all that complexity and simplifies it for you.
    //Why--> use it: Because you don't want to spend weeks learning the nitty-gritty of the Camera2 API just to take a picture or record a video.
    // This module handles all the heavy lifting of interacting with the device's camera hardware.
    // It's like having a skilled mechanic who knows exactly how to tune your car's engine, so you just have to turn the key.
    implementation(libs.cameraCamera)
    //What it does-->: This module is all about managing the camera's state based on your app's lifecycle. Think of your app going through different phases: it's active,
    // it's paused, it's resumed, it's destroyed. The camera needs to be started, stopped, or released correctly at each of these phases.
    // If you don't manage it right, your app could crash, or the camera might stay active even when your app is in the background, draining the battery.
    //Why use it-->: Imagine you're shooting a video. If someone calls you, your app pauses. This module automatically tells the camera to stop recording gracefully.
    // When the call ends and you go back to your app, it automatically resumes the camera. It handles all these start/stop/release operations for you,
    // so you don't have to manually write onResume(), onPause(), onDestroy() methods just for the camera. It ties the camera's life to your app's life,
    // making sure everything runs smoothly and efficiently without you pulling your hair out. It's like having a personal assistant who knows exactly
    // when to turn on and turn off the lights in your room based on whether you're awake or asleep.
    implementation(libs.cameraLifeCycle)
    //What it does:--> This is the visual part. When you open a camera app, you see a live preview of what the camera is seeing, right?
    // This camera-view module provides a PreviewView widget (or something similar, depending on the CameraX version)
    // that you can directly drop into your app's layout. It handles all the displaying of the camera feed on the screen.
    //Why use it:--> Because you don't want to mess around with low-level OpenGL or SurfaceTexture to show the camera's output.
    // This module gives you a ready-made component that displays the camera preview. You just put it in your XML layout,
    // and CameraX handles rendering the live video feed inside it. It's like getting a pre-built TV screen that you just plug in,
    // and it starts showing the camera's broadcast.
    implementation(libs.cameraView)
    //What it does-->: This is where the cool, advanced stuff comes in.
    // Modern smartphone cameras have all sorts of fancy features: Portrait mode (bokeh effect),
    // Night mode (for low light), HDR (High Dynamic Range), Beauty filters, etc.
    // These are often powered by manufacturer-specific algorithms.
    // The camera-extensions module provides a standardized way to access some of these advanced features across different devices,
    // provided the device manufacturer has implemented them using CameraX extensions.
    //Why use it-->: If you want your app to have a "Portrait mode" or a "Night mode" without having to write incredibly complex image processing algorithms yourself,
    // this module is your friend. It tries to leverage the built-in capabilities of the device's camera hardware and software to offer these enhanced photographic
    // experiences. It's like having a magic button that automatically makes your photos look professional, using the special tricks your phone already knows.
    implementation(libs.cameraExtentions)

    implementation(libs.navigation)


}