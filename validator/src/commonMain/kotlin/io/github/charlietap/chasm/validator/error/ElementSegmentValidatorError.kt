package io.github.charlietap.chasm.validator.error

sealed interface ElementSegmentValidatorError : ModuleValidatorError {
    data object UnknownTable : ElementSegmentValidatorError
}
