import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

val libs = the<LibrariesForLibs>()
val conventions = extensions.create<KotlinConventionsExtension>("kotlinConventions")

conventions.jvmBytecodeVersion.convention(
    libs.versions.java.library.bytecode.version.map(String::toInt),
)

fun JavaCompile.targetBytecodeVersion(version: Provider<Int>) {
    val targetVersion = version.get().toString()
    sourceCompatibility = targetVersion
    targetCompatibility = targetVersion
}

fun KotlinJvmTarget.targetBytecodeVersion(version: Provider<Int>) {
    compilerOptions {
        jvmTarget.set(version.map { target -> JvmTarget.fromTarget(target.toString()) })
    }
    compilations.configureEach {
        compileJavaTaskProvider?.configure {
            targetBytecodeVersion(version)
        }
    }
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    extensions.configure<KotlinJvmExtension>("kotlin") {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        }
        compilerOptions {
            jvmTarget.set(conventions.jvmBytecodeVersion.map { target -> JvmTarget.fromTarget(target.toString()) })
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        targetBytecodeVersion(conventions.jvmBytecodeVersion)
    }
}

plugins.withId("org.jetbrains.kotlin.multiplatform") {
    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        }
        targets.withType<KotlinJvmTarget>().configureEach {
            targetBytecodeVersion(conventions.jvmBytecodeVersion)
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
            "-Xno-call-assertions",
            "-Xno-param-assertions",
            "-Xno-receiver-assertions",
            "-XIntrinsic-const-evaluation",
        )
    }
}
