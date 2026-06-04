import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val libs = the<LibrariesForLibs>()

plugins.withId("org.jetbrains.kotlin.jvm") {
    extensions.configure<KotlinBaseExtension>("kotlin") {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        }
    }
}

plugins.withId("org.jetbrains.kotlin.multiplatform") {
    extensions.configure<KotlinBaseExtension>("kotlin") {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        }
    }
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        extraWarnings.set(true)

        freeCompilerArgs.addAll(
            "-opt-in=kotlin.ExperimentalUnsignedTypes",
            "-Xwarning-level=NOTHING_TO_INLINE:disabled",
            "-Xwarning-level=UNUSED_ANONYMOUS_PARAMETER:disabled",
            "-Xwarning-level=REDUNDANT_VISIBILITY_MODIFIER:disabled",
            "-Xexpect-actual-classes",
            "-Xcollection-literals",
            "-Xreturn-value-checker=check",
        )
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjdk-release=" + libs.versions.java.bytecode.version.get().toInt(),
            "-Xno-call-assertions",
            "-Xno-param-assertions",
            "-Xno-receiver-assertions",
            "-XIntrinsic-const-evaluation",
        )
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.java.bytecode.version.get().toInt())
}
