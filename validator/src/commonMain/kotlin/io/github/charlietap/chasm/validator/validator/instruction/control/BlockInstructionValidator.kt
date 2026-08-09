package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.LabelKind
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.validator.type.BlockTypeValidator

internal fun BlockInstructionValidator(
    context: ModuleValidationContext,
    instruction: ControlInstruction.Block,
): Result<Unit, ModuleValidatorError> =
    BlockInstructionValidator(
        context = context,
        instruction = instruction,
        blockTypeValidator = ::BlockTypeValidator,
    )

internal inline fun BlockInstructionValidator(
    context: ModuleValidationContext,
    instruction: ControlInstruction.Block,
    crossinline blockTypeValidator: ModuleValidator<BlockType>,
): Result<Unit, ModuleValidatorError> = binding {

    blockTypeValidator(context, instruction.blockType).bind()

    val functionType = context.functionType(instruction.blockType).bind()

    context.popValues(functionType.params.types).bind()

    val label = Label(
        kind = LabelKind.Block,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        localChangesDepth = context.localChanges.size,
        unreachable = false,
    )

    context.labels.push(label)
    context.pushValues(functionType.params.types)
}
