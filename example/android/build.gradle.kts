plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.symbol.processing)


    alias(libs.plugins.conventions.linting)
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
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get())
    }

    kotlinOptions {
        jvmTarget = libs.versions.java.bytecode.version.get()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(projects.consumerAndroidFibonacci)
    implementation(projects.consumerJvmTest)
    implementation(projects.consumerMultiplatformFactorial)
    implementation(projects.producer)

    implementation(libs.chasm.jvm)
    implementation(libs.hilt.android)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose.ui)

    runtimeOnly(libs.kotlinx.coroutines.android)

    ksp(libs.bundles.hilt.compilers)
}
