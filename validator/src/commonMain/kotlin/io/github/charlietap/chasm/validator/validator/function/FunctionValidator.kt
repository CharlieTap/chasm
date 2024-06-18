package io.github.charlietap.chasm.validator.validator.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.ext.functionType
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

    context.locals.clear()
    context.labels.clear()

    val type = context.module.types[function.typeIndex.idx.toInt()]
    val functionType = type.recursiveType.definedType().functionType()

    val params = functionType?.params?.types ?: emptyList()
    context.locals += params + function.locals.map { local ->
        local.type
    }

    expressionValidator(context, function.body).bind()
}
