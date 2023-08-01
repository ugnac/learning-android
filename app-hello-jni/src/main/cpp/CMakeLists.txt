# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.
cmake_minimum_required(VERSION 3.18.1)

project("app-hello-jni")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_library(
        hello-jni # Sets the name of the library.
        SHARED # Sets the library as a shared library.
        hello-jni.cpp) # Provides a relative path to your source file(s).

add_library(plasma SHARED plasma.c)

find_library(
        log-lib # Defines the name of the path variable that stores the location of the NDK library.
        log) # Specifies the name of the NDK library that CMake needs to locate.

# Include libraries needed for hello-jni lib
target_link_libraries(
        plasma
        android
        jnigraphics
        ${log-lib}  # Links the log library to the target library.
        hello-jni
)