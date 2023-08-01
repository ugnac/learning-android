import version.gradle.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // kotlin data class 序列化需要此 plugin
    id("kotlin-kapt") // data binding 需要此 plugin
    id("deps-plugin")
}

android {
    namespace = "com.learn.aidl.server"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.learn.aidl.server"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        aidl = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(mapOf("path" to ":common")))

    implementation(Deps.core_ktx)
    implementation(Deps.appcompat)
    implementation(Deps.material)
    implementation(Deps.constraintlayout)

    testImplementation(Deps.junit)
    androidTestImplementation(Deps.ext_junit)
    androidTestImplementation(Deps.espresso)
}