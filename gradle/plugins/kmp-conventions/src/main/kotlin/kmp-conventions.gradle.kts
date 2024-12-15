
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

val libs = the<LibrariesForLibs>()

fun KotlinMultiplatformExtension.unixTargets() = setOf(
    macosArm64(),
    macosX64(),
    iosArm64(),
    iosSimulatorArm64(),
    iosX64(),
    linuxArm64(),
    linuxX64(),
)

kotlin {

    jvm()
    unixTargets()

    compilerOptions {
        extraWarnings.set(true)

        freeCompilerArgs.add("-opt-in=kotlin.ExperimentalUnsignedTypes")

        freeCompilerArgs.add("-Xsuppress-warning=NOTHING_TO_INLINE")
        freeCompilerArgs.add("-Xsuppress-warning=UNUSED_ANONYMOUS_PARAMETER")
        freeCompilerArgs.add("-Xsuppress-warning=REDUNDANT_VISIBILITY_MODIFIER")

        freeCompilerArgs.add("-Xnon-local-break-continue")
        freeCompilerArgs.add("-Xwhen-guards")
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }
}
