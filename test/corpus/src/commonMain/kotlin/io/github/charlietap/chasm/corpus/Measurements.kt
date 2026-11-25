package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.measurement.Timings

internal fun measurement(
    fixture: Fixture,
    engine: String,
    bytes: ByteArray,
    instances: Int,
    result: CorpusResult,
    cold: Timings,
    warm: Timings?,
): Measurement {

    val outcome = when (result) {
        CorpusResult.Success -> Outcome.Passed
        is CorpusResult.Skipped -> Outcome.Skipped
        is CorpusResult.Failure -> Outcome.Failed
    }
    val detail = when (result) {
        CorpusResult.Success -> null
        is CorpusResult.Skipped -> result.reason
        is CorpusResult.Failure -> listOfNotNull(result.context, result.message, result.detail).joinToString(": ")
    }

    return Measurement(
        fixture = fixture.name,
        version = fixture.version ?: DEFAULT_VERSION,
        path = fixture.path,
        sha256 = fixture.sha256,
        engine = engine,
        outcome = outcome,
        detail = detail,
        binaryBytes = bytes.size.toLong(),
        tests = fixture.tests.size,
        steps = fixture.tests.sumOf { test -> test.steps.size },
        instances = instances,
        cold = cold,
        warm = warm,
    )
}

private const val DEFAULT_VERSION = "1.0"
