package io.github.charlietap.chasm.aot.corpus.fixture

import io.github.charlietap.chasm.aot.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.fixture.Fixture

internal data class FixtureContext(
    val fixture: Fixture,
    val phase: CorpusPhase,
    val config: Config,
    val timings: CorpusTimingRecorder,
)
