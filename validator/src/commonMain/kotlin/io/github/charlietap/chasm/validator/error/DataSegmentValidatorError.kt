package io.github.charlietap.chasm.validator.error

internal sealed interface DataSegmentValidatorError : ModuleValidatorError {
    data object UnknownMemory : DataSegmentValidatorError
}
