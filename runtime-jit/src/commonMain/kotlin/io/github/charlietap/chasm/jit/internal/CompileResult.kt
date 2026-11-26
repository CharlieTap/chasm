package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.jit.internal.CompileError.Stage

internal sealed interface CompileResult {

    data class Success(
        val compilation: JitCompilation,
    ) : CompileResult

    data class Error(
        val error: CompileError,
    ) : CompileResult
}

internal fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): CompileResult.Error = CompileResult.Error(CompileError(message, stage, functions))
