package io.github.charlietap.corpus.lib.fixture

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Fixture(
    val name: String,
    val path: String,
    val version: String? = null,
    val features: List<String> = emptyList(),
    val imports: List<FixtureImport> = emptyList(),
    val exports: List<FixtureExport> = emptyList(),
    val tests: List<FixtureTest> = emptyList(),
    val sha256: String? = null,
    val tags: List<String> = emptyList(),
)

@Serializable
data class FixtureImport(
    val module: String,
    val name: String,
    val kind: String,
    val type: String? = null,
    val stub: FixtureImportStub? = null,
)

@Serializable
data class FixtureImportStub(
    val params: List<FixtureValue> = emptyList(),
    val returns: List<FixtureValue> = emptyList(),
    val trap: String? = null,
    val value: FixtureValue? = null,
)

@Serializable
data class FixtureExport(
    val name: String,
    val kind: String,
    val type: String? = null,
)

@Serializable
data class FixtureTest(
    val description: String? = null,
    @SerialName("shared_instance")
    val sharedInstance: Boolean = false,
    val host: FixtureHost? = null,
    val steps: List<JsonObject> = emptyList(),
)

@Serializable
data class FixtureHost(
    @SerialName("wasi_preview_1")
    val wasiPreview1: FixtureWasiPreview1Host? = null,
)

@Serializable
data class FixtureWasiPreview1Host(
    @SerialName("command_args")
    val commandArgs: List<String> = emptyList(),
    @SerialName("system_env")
    val systemEnv: Map<String, String> = emptyMap(),
    @SerialName("preopened_directories")
    val preopenedDirectories: Map<String, String> = emptyMap(),
    val stdin: FixtureBytes? = null,
)

@Serializable
data class FixtureBytes(
    val encoding: String,
    val value: String,
)

@Serializable
data class FixtureValue(
    val type: String,
    val value: String? = null,
    val capture: String? = null,
    @SerialName("var")
    val variable: String? = null,
    val export: String? = null,
)
