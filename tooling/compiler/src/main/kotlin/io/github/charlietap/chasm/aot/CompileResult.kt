package io.github.charlietap.chasm.aot

sealed interface CompileResult {

    data class Success(
        val artifact: CompiledModuleArtifact,
    ) : CompileResult

    data class Error(
        val error: CompileError,
    ) : CompileResult
}

fun CompileResult.expect(
    message: String,
): CompiledModuleArtifact = when (this) {
    is CompileResult.Success -> artifact
    is CompileResult.Error -> throw IllegalStateException("$message: $error")
}
