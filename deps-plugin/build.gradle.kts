import org.gradle.kotlin.dsl.`kotlin-dsl`
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
}

gradlePlugin {
    plugins {
        create("deps-plugin") {
            id = "deps-plugin" //添加插件
            version = "1.0"
            implementationClass = "version.gradle.VersionPlugin" //在根目录创建类 VersionPlugin 继承 Plugin<Project>
        }
    }
}