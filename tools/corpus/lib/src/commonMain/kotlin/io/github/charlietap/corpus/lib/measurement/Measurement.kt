package io.github.charlietap.corpus.lib.measurement

import kotlinx.serialization.Serializable

@Serializable
data class Measurement(
    val fixture: String,
    val version: String,
    val path: String,
    val sha256: String?,
    val engine: String,
    val outcome: Outcome,
    val detail: String?,
    val binaryBytes: Long,
    val tests: Int,
    val steps: Int,
    val instances: Int,
    val cold: Timings,
    val warm: Timings?,
)

@Serializable
enum class Outcome {
    Passed,
    Skipped,
    Failed,
}

@Serializable
data class Timings(
    val totalNanos: Long,
    val decodeNanos: Long,
    val validateNanos: Long,
    val moduleNanos: Long,
    val instantiateNanos: Long,
    val executeNanos: Long,
)
