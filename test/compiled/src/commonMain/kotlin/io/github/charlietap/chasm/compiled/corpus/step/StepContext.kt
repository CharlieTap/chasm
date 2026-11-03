package io.github.charlietap.chasm.compiled.corpus.step

import io.github.charlietap.chasm.compiled.corpus.failure
import io.github.charlietap.chasm.compiled.corpus.instance.RuntimeSetup
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture

internal data class StepContext(
    val fixture: Fixture,
    val location: String,
    val setup: RuntimeSetup,
    val captures: MutableMap<String, ExecutionValue>,
)

internal fun StepContext.failure(
    message: String,
    detail: String? = null,
    expected: String? = null,
    actual: String? = null,
): CorpusResult.Failure = fixture.failure(location, message, detail, expected, actual)
