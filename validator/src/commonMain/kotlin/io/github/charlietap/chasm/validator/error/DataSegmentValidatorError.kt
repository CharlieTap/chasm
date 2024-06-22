package io.github.charlietap.chasm.validator.error

sealed interface DataSegmentValidatorError : ModuleValidatorError {
    data object UnknownMemory : DataSegmentValidatorError
}
