package io.github.charlietap.corpus.lib.report

import kotlinx.serialization.Serializable

@Serializable
data class CorpusReport(
    val schemaVersion: Int,
    val environment: CorpusEnvironment,
    val configuration: CorpusConfiguration,
    val fixtures: List<CorpusFixtureResult>,
)

@Serializable
data class CorpusEnvironment(
    val machine: CorpusMachine,
    val gradle: CorpusGradleRuntime,
    val testJvm: CorpusJavaRuntime,
)

@Serializable
data class CorpusMachine(
    val operatingSystem: String,
    val operatingSystemVersion: String,
    val architecture: String,
    val model: String?,
    val processor: String?,
    val availableProcessors: Int,
    val totalMemoryBytes: Long?,
)

@Serializable
data class CorpusGradleRuntime(
    val version: String,
    val jvm: CorpusJavaRuntime,
)

@Serializable
data class CorpusJavaRuntime(
    val languageVersion: String,
    val runtimeVersion: String,
    val jvmVersion: String,
    val vendor: String,
)

@Serializable
data class CorpusConfiguration(
    val repositoryUrl: String,
    val revision: String,
    val phase: String,
    val versions: List<String>,
    val languages: List<String>,
    val requiredFeatures: List<String>,
    val excludedFeatures: List<String>,
    val tags: List<String>,
    val excludedTags: List<String>,
    val size: String?,
    val duration: String?,
    val targets: List<String>,
    val excludedTargets: List<String>,
)
