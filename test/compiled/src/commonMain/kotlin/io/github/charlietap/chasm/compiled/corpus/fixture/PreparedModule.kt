package io.github.charlietap.chasm.compiled.corpus.fixture

import io.github.charlietap.chasm.compiled.Instantiable
import io.github.charlietap.chasm.embedding.shapes.Module

internal data class PreparedModule(
    val decoded: Module,
    val compiled: Instantiable,
)
