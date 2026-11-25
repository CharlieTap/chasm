package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture

internal fun Fixture.failure(
    context: String,
    message: String,
    detail: String? = null,
    expected: String? = null,
    actual: String? = null,
): CorpusResult.Failure {
    return CorpusResult.Failure(
        fixture = name,
        context = context,
        message = message,
        detail = detail,
        expected = expected,
        actual = actual,
        rerun = "./gradlew corpus -PwasmCorpus.targets=$name",
    )
}
