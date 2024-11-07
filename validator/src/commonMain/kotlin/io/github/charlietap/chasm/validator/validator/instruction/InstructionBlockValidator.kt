package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.type.LocalType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues

internal fun InstructionBlockValidator(
    context: ValidationContext,
    instructions: List<Instruction>,
): Result<Unit, ModuleValidatorError> =
    InstructionBlockValidator(
        context = context,
        instructions = instructions,
        instructionValidator = ::InstructionValidator,
    )

internal fun InstructionBlockValidator(
    context: ValidationContext,
    instructions: List<Instruction>,
    instructionValidator: Validator<Instruction>,
): Result<Unit, ModuleValidatorError> = binding {

    val locals = context.locals.map(LocalType::status)

    instructions.map { instruction ->
        instructionValidator(context, instruction).bind()
    }

    val label = context.labels.peek().bind()

    context.popValues(label.outputs.types).bind()

    if (context.operands.depth() != label.operandsDepth) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    locals.forEachIndexed { idx, status ->
        context.locals[idx] = context.locals[idx].copy(
            status = status,
        )
    }

    context.pushValues(label.outputs.types)
}
