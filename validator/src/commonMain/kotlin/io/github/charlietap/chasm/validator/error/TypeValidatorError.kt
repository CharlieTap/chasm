package io.github.charlietap.chasm.validator.error

sealed interface TypeValidatorError : ModuleValidatorError {
    data object IncorrectLimits : TypeValidatorError

    data object TypeMismatch : TypeValidatorError
}
