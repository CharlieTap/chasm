package io.github.charlietap.corpus.lib.report

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Timings
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class CorpusReport(
    val schemaVersion: Int,
    val environment: CorpusEnvironment,
    val configuration: CorpusConfiguration,
    val summary: List<EngineSummary>,
    val fixtures: List<Measurement>,
)

@Serializable
data class CorpusEnvironment(
    val name: String,
    val machine: CorpusMachine,
    val runtime: CorpusRuntime,
    val conditions: Map<String, String>,
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
data class CorpusRuntime(
    val name: String,
    val version: String,
    val detail: String?,
)

@Serializable
data class CorpusConfiguration(
    val repositoryUrl: String,
    val revision: String,
    val phase: String,
    val engines: List<String>,
    val warmups: Int,
    val rounds: Int,
    @EncodeDefault
    val pin: Boolean = false,
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

@Serializable
data class EngineSummary(
    val engine: String,
    val passed: Int,
    val skipped: Int,
    val failed: Int,
    val missing: List<String>,
    val cold: Timings,
    val warm: Timings?,
)
