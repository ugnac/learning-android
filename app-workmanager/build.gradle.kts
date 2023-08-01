import version.gradle.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("deps-plugin")
}

android {
    namespace = "com.learn.workmanager"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.learn.workmanager"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(mapOf("path" to ":common")))

    implementation(Deps.core_ktx)
    implementation(Deps.appcompat)
    implementation(Deps.material)
    implementation(Deps.constraintlayout)
    implementation(Deps.work.runtime_ktx)

    testImplementation(Deps.junit)
    androidTestImplementation(Deps.ext_junit)
    androidTestImplementation(Deps.espresso)
}