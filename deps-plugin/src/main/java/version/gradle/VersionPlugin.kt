package version.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
    }
}

object Ver {
    const val core_ktx = "1.10.1"
    const val appcompat = "1.6.1"
    const val constraintlayout = "2.1.4"
    const val swiperefreshlayout = "1.1.0"
    const val material = "1.9.0"
    const val annotation = "1.6.0"
    const val room = "2.5.1"
    const val navigation = "2.5.3"
    const val work_version = "2.8.1"
    const val paging = "3.1.1"
    const val junit = "4.13.2"
    const val atsl_junit = "1.1.5"
    const val espresso = "3.5.1"

    const val lifecycle = "2.6.1"
    const val rxjava = "3.1.6"
    const val rxandroid = "3.0.2"
    const val retrofit = "2.9.0"
    const val logging_interceptor = "4.11.0"
    const val glide = "4.11.0"
    const val fastjson = "2.0.28"
    const val gson = "2.10.1"
    const val protobuf_java = "4.0.0-rc-2"
    const val stetho = "1.6.0"
    const val timber = "4.7.1"
    const val dagger = "2.16"

    const val kotlin = "1.8.0"
    const val kotlinx = "1.8.0"
}

object Deps {
    object plugin {
        const val deps_plugin = "deps-plugin"
    }

    const val core_ktx = "androidx.core:core-ktx:${Ver.core_ktx}"
    const val appcompat = "androidx.appcompat:appcompat:${Ver.appcompat}"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Ver.constraintlayout}"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Ver.swiperefreshlayout}"
    const val material = "com.google.android.material:material:${Ver.material}"
    const val annotation = "androidx.annotation:annotation:${Ver.annotation}"

    object work {
        const val runtime = "androidx.work:work-runtime:${Ver.work_version}" // (Java only)
        const val runtime_ktx = "androidx.work:work-runtime-ktx:${Ver.work_version}" // Kotlin + coroutines
        const val rxjava2 = "androidx.work:work-rxjava2:${Ver.work_version}" // optional - RxJava2 support
        const val rxjava3 = "androidx.work:work-rxjava3:${Ver.work_version}" // RxJava 3
        const val gcm = "androidx.work:work-gcm:${Ver.work_version}" // optional - GCMNetworkManager support
        const val multiprocess = "androidx.work:work-multiprocess:${Ver.work_version}" // optional - Multiprocess support
        const val testing = "androidx.work:work-testing:${Ver.work_version}" // optional - Test helpers // androidTestImplementation
    }

    object paging {
        const val runtime = "androidx.paging:paging-runtime:${Ver.paging}"
        const val runtime_ktx = "androidx.paging:paging-runtime-ktx:${Ver.paging}"
        const val rxjava2 = "androidx.paging:paging-rxjava2:${Ver.paging}" // optional - RxJava2 support
        const val rxjava3 = "androidx.paging:paging-rxjava3:${Ver.paging}" // optional - RxJava3 support
        const val guava = "androidx.paging:paging-guava:${Ver.paging}" // optional - Guava ListenableFuture support
        const val compose = "androidx.paging:paging-compose:1.0.0-alpha18" // optional - Jetpack Compose integration
        const val testing = "androidx.paging:paging-common:${Ver.paging}" // alternatively - without Android dependencies for tests // testImplementation
    }

    object room {
        const val runtime = "androidx.room:room-runtime:${Ver.room}" // java?
        const val compiler = "androidx.room:room-compiler:${Ver.room}" // kapt("")
        const val ktx = "androidx.room:room-ktx:${Ver.room}" // optional - Kotlin Extensions and Coroutines support for Room
        const val rxjava2 = "androidx.room:room-rxjava2:${Ver.room}" // optional - RxJava2 support for Room
        const val rxjava3 = "androidx.room:room-rxjava3:${Ver.room}" // optional - RxJava3 support for Room
        const val guava = "androidx.room:room-guava:${Ver.room}" // optional - Guava support for Room, including Optional and ListenableFuture
        const val testing = "androidx.room:room-testing:${Ver.room}" // optional - Test helpers // testImplementation("")
        const val paging = "androidx.room:room-paging:${Ver.room}" // optional - Paging 3 Integration
    }

    object navigation {
        const val ui = "androidx.navigation:navigation-ui:${Ver.navigation}"     // Java
        const val fragment = "androidx.navigation:navigation-fragment:${Ver.navigation}"     // Java
        const val ui_ktx = "androidx.navigation:navigation-ui-ktx:${Ver.navigation}"     // Kotlin
        const val fragment_ktx = "androidx.navigation:navigation-fragment-ktx:${Ver.navigation}"
        const val safe_args = "androidx.navigation:navigation-safe-args-gradle-plugin:${Ver.navigation}"     // Safe Args
        const val dynamic_feature_fragment = "androidx.navigation:navigation-dynamic-features-fragment:${Ver.navigation}" // Feature module Support
        const val testing = "androidx.navigation:navigation-testing:${Ver.navigation}"     // Testing Navigation
        const val compose = "androidx.navigation:navigation-compose:${Ver.navigation}"     // Jetpack Compose Integration
    }

    object lifecycle {
        const val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Ver.lifecycle}"
        const val viewmodel_compose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Ver.lifecycle}"
        const val viewmodel_saved_state = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Ver.lifecycle}"
        const val livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Ver.lifecycle}"
        const val runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Ver.lifecycle}"
        const val compiler_kapt = "androidx.lifecycle:lifecycle-compiler:${Ver.lifecycle}"
    }

    // Test
    const val junit = "junit:junit:${Ver.junit}"
    const val ext_junit = "androidx.test.ext:junit:${Ver.atsl_junit}"
    const val ext_junit_ktx = "androidx.test.ext:junit-ktx:${Ver.atsl_junit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Ver.espresso}"

    object rxjava3 {
        const val rxjava = "io.reactivex.rxjava3:rxjava:${Ver.rxjava}"
        const val rxandroid = "io.reactivex.rxjava3:rxandroid:${Ver.rxandroid}"
    }

    object retrofit2 {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Ver.retrofit}"
        const val converter_gson = "com.squareup.retrofit2:converter-gson:${Ver.retrofit}"
        const val adapter_rxjava3 = "com.squareup.retrofit2:adapter-rxjava3:${Ver.retrofit}"
        const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Ver.logging_interceptor}"
    }

    object dagger {
        const val dagger = "com.google.dagger:dagger:${Ver.dagger}"
        const val android = "com.google.dagger:dagger-android:${Ver.dagger}"
        const val android_support = "com.google.dagger:dagger-android-support:${Ver.dagger}" // if you use the support libraries
        const val compiler = "com.google.dagger:dagger-compiler:${Ver.dagger}" // kapt
        const val android_processor = "com.google.dagger:dagger-android-processor:${Ver.dagger}" // kapt
    }


    // 图片加载
    const val glide = "com.github.bumptech.glide:glide:${Ver.glide}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Ver.glide}"

    // Json Gson
    const val fastjson = "com.alibaba:fastjson:${Ver.fastjson}"
    const val protobuf_java = "com.google.protobuf:protobuf-java:${Ver.protobuf_java}"

    /* -------------gson------------ */
    const val gson = "com.google.code.gson:gson:${Ver.gson}"

    // 测试框架
    const val stetho ="com.facebook.stetho:stetho:${Ver.stetho}"
    const val stetho_okhttps = "com.facebook.stetho:stetho-okhttp3:${Ver.stetho}"

    const val timber = "com.jakewharton.timber:timber:${Ver.timber}"

    object kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Ver.kotlin}"
        const val gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Ver.kotlin}"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Ver.kotlin}"
    }

    object kotlinx {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Ver.kotlinx}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Ver.kotlinx}"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Ver.kotlinx}"
    }
}