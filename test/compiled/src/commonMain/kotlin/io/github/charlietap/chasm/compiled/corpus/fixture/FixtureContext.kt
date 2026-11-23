package io.github.charlietap.chasm.compiled.corpus.fixture

import io.github.charlietap.chasm.compiled.Compiler
import io.github.charlietap.chasm.compiled.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.fixture.Fixture

internal data class FixtureContext(
    val fixture: Fixture,
    val phase: CorpusPhase,
    val moduleConfig: ModuleConfig,
    val runtimeConfig: RuntimeConfig,
    val storeFactory: (Module) -> Store,
    val timings: CorpusTimingRecorder,
    val compiler: Compiler,
)
