package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.jit.internal.CompileError.Stage

/** Compiles [module] to the platform's own code: DEX on Android, JVM classes elsewhere. */
internal expect suspend fun compile(
    module: Module,
    config: CompileConfig,
): CompileResult

internal fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): CompileResult.Error = CompileResult.Error(CompileError(message, stage, functions))
