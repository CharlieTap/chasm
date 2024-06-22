package io.github.charlietap.chasm.validator.validator.function.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander
import io.github.charlietap.chasm.type.expansion.BlockTypeExpanderImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun BlockInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Block,
): Result<Unit, ModuleValidatorError> =
    BlockInstructionValidator(
        context = context,
        instruction = instruction,
        blockExpander = ::BlockTypeExpanderImpl,
    )

internal fun BlockInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Block,
    blockExpander: BlockTypeExpander,
): Result<Unit, ModuleValidatorError> = binding {
    val functionType = blockExpander(context.types, instruction.blockType)
    functionType?.let {
        context.labels.add(functionType.results)
    }
}
