package io.github.charlietap.chasm.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

@InternalChasmApi
sealed interface ModuleFactoryError {

    class Malformed(val error: ModuleDecoderError) : ModuleFactoryError {
        override fun toString(): String = error.toString()
    }

    class Invalid(val error: ModuleValidatorError) : ModuleFactoryError {
        override fun toString(): String = error.toString()
    }
}
