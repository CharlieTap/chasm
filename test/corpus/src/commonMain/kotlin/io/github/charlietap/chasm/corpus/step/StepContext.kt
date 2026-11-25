package io.github.charlietap.chasm.corpus.step

import io.github.charlietap.chasm.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.corpus.failure
import io.github.charlietap.chasm.corpus.instance.RuntimeSetup
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture

internal data class StepContext(
    val fixture: Fixture,
    val location: String,
    val setup: RuntimeSetup,
    val captures: MutableMap<String, ExecutionValue>,
    val timings: CorpusTimingRecorder,
)

internal fun StepContext.failure(
    message: String,
    detail: String? = null,
    expected: String? = null,
    actual: String? = null,
): CorpusResult.Failure {
    return fixture.failure(location, message, detail, expected, actual)
}
