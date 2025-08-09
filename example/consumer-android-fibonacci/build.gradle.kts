import io.github.charlietap.chasm.gradle.CodegenConfig

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.symbol.processing)
    alias(libs.plugins.chasm)

    alias(libs.plugins.conventions.linting)
}

chasm {
    modules {
        create("FibonacciService") {
            binary = layout.projectDirectory.file("src/main/assets/fibonacci.wasm")
            packageName = "com.test.chasm"
            codegenConfig = CodegenConfig(
                generateTypesafeGlobalProperties = true,
            )
        }
    }
}

android {
    compileSdk = libs.versions.compile.sdk.get().toInt()
    namespace = libs.versions.application.namespace.get()

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get())
    }

    kotlinOptions {
        jvmTarget = libs.versions.java.bytecode.version.get()
    }
}

dependencies {
    implementation(libs.hilt.android)

    ksp(libs.bundles.hilt.compilers)
}


