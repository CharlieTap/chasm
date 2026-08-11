package io.github.charlietap.corpus.lib

data class CorpusExecution(
    val result: CorpusResult,
    val timings: CorpusTimings,
    val binarySizeBytes: Long,
    val moduleBuildCount: Int,
)

data class CorpusTimings(
    val totalNanos: Long,
    val decodeNanos: Long,
    val validateNanos: Long,
    val instantiateNanos: Long,
    val executeNanos: Long,
)
