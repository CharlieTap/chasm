package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.validator.instruction.InstructionBlockValidator

internal fun BlockInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Block,
): Result<Unit, ModuleValidatorError> =
    BlockInstructionValidator(
        context = context,
        instruction = instruction,
        instructionBlockValidator = ::InstructionBlockValidator,
    )

internal fun BlockInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Block,
    instructionBlockValidator: Validator<List<Instruction>>,
): Result<Unit, ModuleValidatorError> = binding {

    val functionType = context.functionType(instruction.blockType).bind()

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

    instructionBlockValidator(context, instruction.instructions).bind()

    context.labels.pop().bind()
}
