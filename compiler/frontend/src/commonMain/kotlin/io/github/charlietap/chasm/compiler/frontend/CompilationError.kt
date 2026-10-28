package io.github.charlietap.chasm.compiler.frontend

sealed interface CompilationError {
    data object UnsupportedThreads : CompilationError

    data object UnsupportedSimd : CompilationError
}
