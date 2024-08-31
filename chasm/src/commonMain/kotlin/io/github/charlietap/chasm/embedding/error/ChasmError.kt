package io.github.charlietap.chasm.embedding.error

import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import kotlin.jvm.JvmInline

sealed interface ChasmError {
    @JvmInline
    value class DecodeError(val error: ModuleDecoderError) : ChasmError

    @JvmInline
    value class ValidationError(val error: ModuleValidatorError) : ChasmError

    @JvmInline
    value class ExecutionError(val error: ModuleTrapError) : ChasmError
}
