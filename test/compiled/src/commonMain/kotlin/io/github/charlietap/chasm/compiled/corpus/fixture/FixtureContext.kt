package io.github.charlietap.chasm.compiled.corpus.fixture

import io.github.charlietap.chasm.compiled.Compiler
import io.github.charlietap.chasm.compiled.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.fixture.Fixture

internal data class FixtureContext(
    val fixture: Fixture,
    val phase: CorpusPhase,
    val config: Config,
    val timings: CorpusTimingRecorder,
    val compiler: Compiler,
)
