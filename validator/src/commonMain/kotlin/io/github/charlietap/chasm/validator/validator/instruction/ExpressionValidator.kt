package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.ExpressionScope
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

internal fun ExpressionValidator(
    context: ValidationContext,
    expression: Expression,
    scope: Scope<Expression>,
    constInstructionValidator: Validator<Instruction>,
): Result<Unit, ModuleValidatorError> = binding {

    val scopedContext = scope(context, expression).bind()

    expression.instructions.map { instruction ->
        constInstructionValidator(scopedContext, instruction).bind()
    }

    val resultType = scopedContext.expressionResultType().bind()
    scopedContext.popValues(resultType.types).bind()

    if (scopedContext.operands.depth() != 0) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }
}
