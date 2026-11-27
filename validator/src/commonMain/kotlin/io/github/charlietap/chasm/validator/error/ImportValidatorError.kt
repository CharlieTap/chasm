package io.github.charlietap.chasm.validator.error

internal sealed interface ImportValidatorError : ModuleValidatorError {
    data object UnknownType : ImportValidatorError
}
