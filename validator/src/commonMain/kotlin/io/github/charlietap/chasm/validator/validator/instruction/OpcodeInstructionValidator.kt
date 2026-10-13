package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.control.ControlInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.memory.MemoryInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.numeric.NumericInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.variable.VariableInstructionValidator

/**
 * Validates [instruction] by the opcode it was decoded from, falling back to
 * [InstructionValidator] for an opcode without a direct route.
 */
internal fun OpcodeInstructionValidator(
    context: ModuleValidationContext,
    opcode: Int,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> = when (OPCODE_GROUPS[opcode]) {
    VARIABLE_GROUP -> VariableInstructionValidator(context, opcode, instruction)
    NUMERIC_GROUP -> NumericInstructionValidator(context, instruction as NumericInstruction)
    CONTROL_GROUP -> ControlInstructionValidator(context, opcode, instruction)
    MEMORY_GROUP -> MemoryInstructionValidator(context, opcode, instruction)
    else -> InstructionValidator(context, instruction)
}

private const val VARIABLE_GROUP: Byte = 1
private const val NUMERIC_GROUP: Byte = 2
private const val CONTROL_GROUP: Byte = 3
private const val MEMORY_GROUP: Byte = 4

private val OPCODE_GROUPS: ByteArray = ByteArray(256).also { groups ->
    for (opcode in LOCAL_GET..GLOBAL_SET) groups[opcode] = VARIABLE_GROUP
    for (opcode in FIRST_NUMERIC..LAST_NUMERIC) groups[opcode] = NUMERIC_GROUP
    for (opcode in UNREACHABLE..ELSE) groups[opcode] = CONTROL_GROUP
    for (opcode in END..CALL_INDIRECT) groups[opcode] = CONTROL_GROUP
    for (opcode in FIRST_LOAD..MEMORY_GROW) groups[opcode] = MEMORY_GROUP
}
