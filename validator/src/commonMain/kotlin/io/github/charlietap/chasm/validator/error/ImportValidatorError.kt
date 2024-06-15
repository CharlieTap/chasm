package io.github.charlietap.chasm.validator.error

sealed interface ImportValidatorError : ModuleValidatorError {
    data object UnknownType : ImportValidatorError
}
