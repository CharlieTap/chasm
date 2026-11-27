package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
sealed interface CompilationError {
    data object UnsupportedThreads : CompilationError

    data object UnsupportedSimd : CompilationError
}
