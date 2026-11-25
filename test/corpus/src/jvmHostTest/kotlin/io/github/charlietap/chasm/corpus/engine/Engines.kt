package io.github.charlietap.chasm.corpus.engine

internal val engines: Set<Engine> = setOf(
    Interpreter(),
    ClassFiles(),
)
