
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

val libs = the<LibrariesForLibs>()

fun KotlinMultiplatformExtension.unixTargets() = setOf(
    macosArm64 {
        binaries {
            test(listOf(RELEASE)) {

            }
        }
    },
    macosX64(),
    iosArm64(),
    iosSimulatorArm64(),
    iosX64(),
    linuxArm64(),
    linuxX64(),
)

fun KotlinMultiplatformExtension.nativeTargets() = setOf(
    mingwX64()
) + unixTargets()

kotlin {

    compilerOptions {
        extraWarnings.set(true)

        freeCompilerArgs.add("-opt-in=kotlin.ExperimentalUnsignedTypes")

        freeCompilerArgs.add("-Xwarning-level=NOTHING_TO_INLINE:disabled")
        freeCompilerArgs.add("-Xwarning-level=UNUSED_ANONYMOUS_PARAMETER:disabled")
        freeCompilerArgs.add("-Xwarning-level=REDUNDANT_VISIBILITY_MODIFIER:disabled")

        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xnon-local-break-continue")
        freeCompilerArgs.add("-Xwhen-guards")
    }

    jvm {
        compilerOptions {
            freeCompilerArgs.add("-Xjdk-release=" + libs.versions.java.bytecode.version.get().toInt())

            freeCompilerArgs.add("-Xno-call-assertions")
            freeCompilerArgs.add("-Xno-param-assertions")
            freeCompilerArgs.add("-Xno-receiver-assertions")
        }
    }
    nativeTargets()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.java.bytecode.version.get().toInt())
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}
