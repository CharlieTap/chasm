import com.android.build.api.artifact.SingleArtifact
import io.github.charlietap.chasm.gradle.task.DeviceInstrumentationCheckTask
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import java.time.Duration

plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
    alias(libs.plugins.conventions.sweet)
}

sweet {
    scriptRunner = "io.github.charlietap.chasm.compiled.script.PlatformScriptRunner"
    testPackageName = "io.github.charlietap.chasm.compiled.testsuite"
}

val sweetResources = tasks.register<Sync>("sweetResources") {
    dependsOn("prepareTestSuite")
    from(sweet.intermediatesDirectory) {
        into("sweet")
    }
    into(layout.buildDirectory.dir("generated/sweetResources"))
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiled"
        withHostTest {}
        withDeviceTestBuilder {}.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            instrumentationRunnerArguments["notClass"] = "io.github.charlietap.chasm.compiled.JitTieringTest"
        }
        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("consumer-rules.pro")
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.InternalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
                api(projects.runtime.core)

                implementation(projects.embeddingRuntime)
                implementation(projects.memory)
                implementation(projects.moduleDeclarations)
                implementation(projects.runtime.compiledFormat)
                implementation(projects.runtime.host)
                implementation(projects.runtime.operations)

                implementation(projects.executor.callCore)
                implementation(projects.executor.instantiator)

                implementation(projects.libs.parallel)
                implementation(projects.libs.parallelCoroutines)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.chasm)
                implementation(projects.compiler.backendAnalysis)
                implementation(projects.compiler.backendJvm)
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.module)
                implementation(projects.runtime.address)
                implementation(projects.test.compiled)
                implementation(projects.validator)

                implementation(libs.asm.util)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.sweet.lib)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.chasmJvmCompiler)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.runtime)
                implementation(projects.test.fixture.typeSystem)
            }
        }

        getByName("androidDeviceTest") {
            kotlin.srcDir(tasks.named<GenerateTestsTask>("generateCoreTests").flatMap { task -> task.outputDirectory })
            resources.srcDir(sweetResources)
            languageSettings.optIn("io.github.charlietap.chasm.ExperimentalChasmApi")
            dependencies {
                implementation(projects.chasm)
                implementation(projects.chasmDexCompiler)
                implementation(projects.compiler.backendDex)
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.libs.parallel)
                implementation(projects.libs.parallelCoroutines)
                implementation(projects.module)
                implementation(projects.runtime.address)
                implementation(projects.test.compiled)
                implementation(projects.typeSystem)
                implementation(projects.validator)

                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.runner)
                implementation(libs.kotlin.junit)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.result)
                implementation(libs.sweet.lib)
            }
        }
    }
}

androidComponents {
    onVariants { variant ->
        variant.deviceTests.values.forEach { deviceTest ->
            tasks.register<DeviceInstrumentationCheckTask>("checkJitTiering") {
                group = "verification"
                description = "Checks on a Samsung S24 that ART JIT-compiles the generated code"
                adbExecutable = sdkComponents.adb
                timeout = Duration.ofMinutes(10)
                testApkDirectory = deviceTest.artifacts.get(SingleArtifact.APK)
                testApplicationId = deviceTest.applicationId
                instrumentationRunner = deviceTest.instrumentationRunner
                testClass = "io.github.charlietap.chasm.compiled.JitTieringTest"
                deviceModel = "SM-S721B"
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Xss32m")
    val defaults = if (name == "testAndroidHostTest") emptyMap() else mapOf("chasm.test.aot.memory" to "BYTE_BUFFER")
    listOf("chasm.test.aot.split", "chasm.test.aot.memory").forEach { property ->
        (providers.systemProperty(property).orNull ?: defaults[property])?.let { value ->
            systemProperty(property, value)
            inputs.property(property, value)
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-compiled"
    description = "Runs Wasm modules compiled to JVM classes or DEX on Chasm's runtime"
}
