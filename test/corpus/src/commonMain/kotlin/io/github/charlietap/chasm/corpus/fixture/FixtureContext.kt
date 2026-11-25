package io.github.charlietap.chasm.corpus.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.corpus.engine.Engine
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.fixture.Fixture

internal class FixtureContext(
    val fixture: Fixture,
    val phase: CorpusPhase,
    val engine: Engine,
    val runtimeConfig: RuntimeConfig,
    val timings: CorpusTimingRecorder,
)
