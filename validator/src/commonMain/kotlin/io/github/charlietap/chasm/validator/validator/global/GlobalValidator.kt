package io.github.charlietap.chasm.validator.validator.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.GlobalScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.ExpressionValidator
import io.github.charlietap.chasm.validator.validator.type.ValueTypeValidator

internal fun GlobalValidator(
    context: ValidationContext,
    global: Global,
): Result<Unit, ModuleValidatorError> =
    GlobalValidator(
        context = context,
        global = global,
        scope = ::GlobalScope,
        expressionValidator = ::ExpressionValidator,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun GlobalValidator(
    context: ValidationContext,
    global: Global,
    crossinline scope: Scope<Global>,
    crossinline expressionValidator: Validator<Expression>,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    val scopedContext = scope(context, global).bind()
    expressionValidator(scopedContext, global.initExpression).bind()
    valueTypeValidator(scopedContext, global.type.valueType).bind()
}
