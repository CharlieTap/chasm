package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import kotlinx.serialization.json.Json
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLauncher

class HostEnvironments(
    private val machineJson: Provider<String>,
) {

    fun jvm(
        name: String,
        launcher: Provider<JavaLauncher>,
    ): Provider<String> {

        val runtime = launcher.map { javaLauncher ->
            CorpusRuntime(
                name = javaLauncher.metadata.vendor,
                version = javaLauncher.metadata.javaRuntimeVersion,
                detail = null,
            )
        }

        return machineJson.zip(runtime) { machine, corpusRuntime ->
            environment(name, machine, corpusRuntime)
        }
    }

    fun native(
        name: String,
        kotlinVersion: String,
    ): Provider<String> {

        val runtime = CorpusRuntime(
            name = KOTLIN_NATIVE,
            version = kotlinVersion,
            detail = null,
        )

        return machineJson.map { machine ->
            environment(name, machine, runtime)
        }
    }

    private fun environment(
        name: String,
        machine: String,
        runtime: CorpusRuntime,
    ): String {

        val environment = CorpusEnvironment(
            name = name,
            machine = Json.decodeFromString(CorpusMachine.serializer(), machine),
            runtime = runtime,
            conditions = emptyMap(),
        )

        return Json.encodeToString(CorpusEnvironment.serializer(), environment)
    }

    private companion object {
        const val KOTLIN_NATIVE = "Kotlin/Native"
    }
}
