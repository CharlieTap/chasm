package io.github.charlietap.chasm.validator.error

sealed interface FunctionValidatorError : ModuleValidatorError {
    data object UnknownFunction : FunctionValidatorError

    data object UnknownType : FunctionValidatorError
}
