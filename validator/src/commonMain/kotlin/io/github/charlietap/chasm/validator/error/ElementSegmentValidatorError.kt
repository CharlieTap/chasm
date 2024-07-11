package io.github.charlietap.chasm.validator.error

sealed interface ElementSegmentValidatorError : ModuleValidatorError {
    data object UnknownSegment : ElementSegmentValidatorError

    data object UnknownTable : ElementSegmentValidatorError
}
