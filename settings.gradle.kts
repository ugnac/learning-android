pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://jitpack.io")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "learning-android"
includeBuild("deps-plugin")
include(":app")
include(":app-aidl-server")
include(":app-aidl-client")
include(":app-camera")
include(":common")
include(":app-hello-jni")
include(":app-audio-echo")
include(":app-workmanager")
include(":app-paging")
include(":app-learning-kotlin")
include(":app-learning-java")
include(":app-viewpager")
