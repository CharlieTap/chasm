package io.github.charlietap.chasm.validator.error

sealed interface TableValidatorError : ModuleValidatorError {
    data object IncorrectLimits : TableValidatorError

    data object UnknownTable : TableValidatorError

    data object UnknownSegmentIndex : TableValidatorError
}
