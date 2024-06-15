package io.github.charlietap.chasm.validator.validator.function.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ExpressionValidator(
    context: ValidationContext,
    expression: Expression,
): Result<Unit, ModuleValidatorError> =
    ExpressionValidator(
        context = context,
        expression = expression,
        instructionValidator = ::InstructionValidator,
    )

internal fun ExpressionValidator(
    context: ValidationContext,
    expression: Expression,
    instructionValidator: Validator<Instruction>,
): Result<Unit, ModuleValidatorError> = binding {
    expression.instructions.map { instruction ->
        instructionValidator(context, instruction).bind()
    }
}
