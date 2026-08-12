package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.validator.instruction.control.FinishLabel

internal fun InstructionBlockValidator(
    context: ModuleValidationContext,
    instructions: List<Instruction>,
): Result<Unit, ModuleValidatorError> =
    InstructionBlockValidator(
        context = context,
        instructions = instructions,
        instructionValidator = ::InstructionValidator,
    )

internal inline fun InstructionBlockValidator(
    context: ModuleValidationContext,
    instructions: List<Instruction>,
    crossinline instructionValidator: ModuleValidator<Instruction>,
): Result<Unit, ModuleValidatorError> {

    val labelsDepth = context.labels.depth()

    var index = 0
    while (index < instructions.size) {
        val result = try {
            instructionValidator(context, instructions[index])
        } catch (exception: ModuleValidationException) {
            return Err(exception.error)
        }
        result.getOrElse { error ->
            return Err(error)
        }
        index++
    }

    if (context.labels.depth() != labelsDepth) {
        return Err(TypeValidatorError.TypeMismatch)
    }

    val label = context.labels.peek().getOrElse { error ->
        return Err(error)
    }
    FinishLabel(context, label, pushOutputs = true).getOrElse { error ->
        return Err(error)
    }
    return Ok(Unit)
}
