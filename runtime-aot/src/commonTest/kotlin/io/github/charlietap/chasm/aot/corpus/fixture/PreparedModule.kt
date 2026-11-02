package io.github.charlietap.chasm.aot.corpus.fixture

import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.embedding.shapes.Module

internal data class PreparedModule(
    val decoded: Module,
    val compiled: CompiledModule,
)
