package io.github.charlietap.chasm.validator.error

internal sealed interface GlobalValidatorError : ModuleValidatorError {
    data object ExpressionMustBeConstant : GlobalValidatorError
}
