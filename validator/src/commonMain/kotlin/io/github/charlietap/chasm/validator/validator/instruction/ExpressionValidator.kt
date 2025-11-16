package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.ExpressionScope
import io.github.charlietap.chasm.validator.context.scope.NewScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.expressionResultType
import io.github.charlietap.chasm.validator.ext.popValues

internal fun ExpressionValidator(
    context: ValidationContext,
    expression: Expression,
): Result<Unit, ModuleValidatorError> =
    ExpressionValidator(
        context = context,
        expression = expression,
        scope = ::ExpressionScope,
        constInstructionValidator = ::ConstInstructionValidator,
    )

internal inline fun ExpressionValidator(
    context: ValidationContext,
    expression: Expression,
    crossinline scope: NewScope<Expression>,
    crossinline constInstructionValidator: Validator<Instruction>,
): Result<Unit, ModuleValidatorError> = binding {
    scope(context, expression) { scopedContext ->
        expression.instructions.forEach { instruction ->
            constInstructionValidator(scopedContext, instruction).bind()
        }

        val resultType = scopedContext.expressionResultType().bind()
        scopedContext.popValues(resultType.types).bind()

        if (scopedContext.operands.depth() != 0) {
            Err(TypeValidatorError.TypeMismatch)
        } else {
            Ok(Unit)
        }
    }.bind()
}
