package io.github.charlietap.chasm.jit.internal

internal sealed interface CompileResult {

    data class Success(
        val compilation: JitCompilation,
    ) : CompileResult

    data class Error(
        val error: CompileError,
    ) : CompileResult
}
