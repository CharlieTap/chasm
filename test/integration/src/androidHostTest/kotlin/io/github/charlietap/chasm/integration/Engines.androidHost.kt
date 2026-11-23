package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.aot.CompileTarget

actual val engines: Set<Engine> = setOf(
    Interpreter(),
    Aot(CompileTarget.ANDROID),
)
