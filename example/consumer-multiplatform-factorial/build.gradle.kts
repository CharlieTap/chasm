import io.github.charlietap.chasm.gradle.CodegenConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.symbol.processing)
    alias(libs.plugins.chasm)

    alias(libs.plugins.conventions.linting)
}

chasm {
    modules {
        create("FactorialService") {
            binary = layout.projectDirectory.file("src/commonMain/resources/factorial.wasm")
            packageName = "com.test.chasm"
            codegenConfig = CodegenConfig(
                generateTypesafeGlobalProperties = true,
            )
        }
    }
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.chasm.jvm)
            implementation(libs.hilt.core)
        }
    }

    dependencies {
        add("kspJvm", libs.hilt.compiler)
    }
}


