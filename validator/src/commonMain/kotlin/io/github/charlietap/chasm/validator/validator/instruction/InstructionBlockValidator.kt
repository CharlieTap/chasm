package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
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
): Result<Unit, ModuleValidatorError> = binding {

    val labelsDepth = context.labels.depth()

    instructions.forEach { instruction ->
        instructionValidator(context, instruction).bind()
    }

    if (context.labels.depth() != labelsDepth) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    val label = context.labels.peek().bind()
    FinishLabel(context, label, pushOutputs = true).bind()
}
