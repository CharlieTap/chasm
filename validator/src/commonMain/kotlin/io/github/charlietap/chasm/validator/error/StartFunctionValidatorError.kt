package io.github.charlietap.chasm.validator.error

internal sealed interface StartFunctionValidatorError : ModuleValidatorError {
    data object UnknownFunction : StartFunctionValidatorError

    data object InvalidStartFunction : StartFunctionValidatorError
}
