@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.symbol.processing)
    id("linting-conventions")
}

android {

    namespace = libs.versions.application.namespace.get()
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {

        applicationId = libs.versions.application.id.get()
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionName = libs.versions.version.name.get()

        testInstrumentationRunner =  "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled =  true
            isShrinkResources =  true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
        compose = true
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {

    implementation(libs.chasm.jvm)
    implementation(libs.hilt.core)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose.ui)

    runtimeOnly(libs.kotlinx.coroutines.android)

    ksp(libs.bundles.hilt.compilers)
}
