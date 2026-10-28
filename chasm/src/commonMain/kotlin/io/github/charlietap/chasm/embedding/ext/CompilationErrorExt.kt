package io.github.charlietap.chasm.embedding.ext

import io.github.charlietap.chasm.compiler.frontend.CompilationError
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun CompilationError.toModuleTrapError(): ModuleTrapError = when (this) {
    CompilationError.UnsupportedThreads -> InstantiationError.UnsupportedThreadsModule
    CompilationError.UnsupportedSimd -> InstantiationError.UnsupportedSIMDModule
}
