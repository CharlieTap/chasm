package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.validator.instruction.InstructionBlockValidator
import io.github.charlietap.chasm.validator.validator.type.BlockTypeValidator

internal fun IfInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.If,
): Result<Unit, ModuleValidatorError> =
    IfInstructionValidator(
        context = context,
        instruction = instruction,
        blockTypeValidator = ::BlockTypeValidator,
        instructionBlockValidator = ::InstructionBlockValidator,
    )

internal inline fun IfInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.If,
    crossinline blockTypeValidator: Validator<BlockType>,
    crossinline instructionBlockValidator: Validator<List<Instruction>>,
): Result<Unit, ModuleValidatorError> = binding {

    blockTypeValidator(context, instruction.blockType).bind()

    val functionType = context.functionType(instruction.blockType).bind()

    context.popI32().bind()
    context.popValues(functionType.params.types).bind()

    val label = Label(
        instruction = instruction,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        unreachable = false,
    )

    context.labels.push(label)
    context.pushValues(functionType.params.types)

    instructionBlockValidator(context, instruction.thenInstructions).bind()
    context.labels.pop().bind()

    context.popValues(functionType.results.types).bind()

    val elseLabel = Label(
        instruction = instruction,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        unreachable = false,
    )

    context.labels.push(elseLabel)
    context.pushValues(functionType.params.types.asReversed())

    instructionBlockValidator(context, instruction.elseInstructions ?: emptyList()).bind()

    context.labels.pop().bind()
}
