import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.android.benchmark)

    alias(libs.plugins.conventions.linting)
}

android {

    namespace = "com.tap.chasmbenchmark"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()

        testInstrumentationRunner =  "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }

    testBuildType = "release"
    buildTypes {
        release {
            isMinifyEnabled =  true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.android.library.bytecode.version.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.android.library.bytecode.version.get())
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.library.bytecode.version.get()))
    }
}

dependencies {

    androidTestImplementation(libs.chasm.kmp)

    androidTestImplementation(libs.androidx.benchmark)
    androidTestImplementation(libs.androidx.test.junit)
}
