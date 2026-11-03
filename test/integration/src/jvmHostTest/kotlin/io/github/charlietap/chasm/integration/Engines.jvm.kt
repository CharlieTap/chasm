package io.github.charlietap.chasm.integration

actual val engines: Set<Engine> = setOf(
    Interpreter(),
    AotEngine(),
)
