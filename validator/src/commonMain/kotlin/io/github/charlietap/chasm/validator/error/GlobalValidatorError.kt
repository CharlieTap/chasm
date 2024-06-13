package io.github.charlietap.chasm.validator.error

sealed interface GlobalValidatorError : ModuleValidatorError {
    data object ExpressionMustBeConstant : GlobalValidatorError
}
