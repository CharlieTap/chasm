package io.github.charlietap.corpus.lib.report

import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlinx.serialization.Serializable

@Serializable
data class CorpusFixtureResult(
    val version: String,
    val fixture: String,
    val path: String,
    val sha256: String?,
    val phase: String,
    val outcome: String,
    val detail: String?,
    val binarySizeBytes: Long,
    val testCount: Int,
    val stepCount: Int,
    val moduleBuildCount: Int,
    val totalNanos: Long,
    val decodeNanos: Long,
    val validateNanos: Long,
    val instantiateNanos: Long,
    val executeNanos: Long,
) {
    companion object {
        fun from(
            fixture: Fixture,
            phase: CorpusPhase,
            execution: CorpusExecution,
        ): CorpusFixtureResult {
            val (outcome, detail) = when (val result = execution.result) {
                CorpusResult.Success -> "passed" to null
                is CorpusResult.Skipped -> "skipped" to result.reason
                is CorpusResult.Failure -> "failed" to listOfNotNull(
                    result.context,
                    result.message,
                    result.detail,
                ).joinToString(": ")
            }

            return CorpusFixtureResult(
                version = fixture.version ?: "1.0",
                fixture = fixture.name,
                path = fixture.path,
                sha256 = fixture.sha256,
                phase = phase.name.lowercase(),
                outcome = outcome,
                detail = detail,
                binarySizeBytes = execution.binarySizeBytes,
                testCount = fixture.tests.size,
                stepCount = fixture.tests.sumOf { test -> test.steps.size },
                moduleBuildCount = execution.moduleBuildCount,
                totalNanos = execution.timings.totalNanos,
                decodeNanos = execution.timings.decodeNanos,
                validateNanos = execution.timings.validateNanos,
                instantiateNanos = execution.timings.instantiateNanos,
                executeNanos = execution.timings.executeNanos,
            )
        }
    }
}
