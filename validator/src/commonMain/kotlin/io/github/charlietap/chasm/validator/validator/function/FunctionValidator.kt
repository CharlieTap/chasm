package io.github.charlietap.chasm.validator.validator.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.function.instruction.ExpressionValidator

internal fun FunctionValidator(
    context: ValidationContext,
    function: Function,
): Result<Unit, ModuleValidatorError> =
    FunctionValidator(
        context = context,
        function = function,
        expressionValidator = ::ExpressionValidator,
    )

internal fun FunctionValidator(
    context: ValidationContext,
    function: Function,
    expressionValidator: Validator<Expression>,
): Result<Unit, ModuleValidatorError> = binding {
    expressionValidator(context, function.body).bind()
}
