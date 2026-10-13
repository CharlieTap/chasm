package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.GLOBAL_GET
import io.github.charlietap.chasm.validator.validator.instruction.GLOBAL_SET
import io.github.charlietap.chasm.validator.validator.instruction.LOCAL_GET
import io.github.charlietap.chasm.validator.validator.instruction.LOCAL_SET
import io.github.charlietap.chasm.validator.validator.instruction.LOCAL_TEE

internal fun VariableInstructionValidator(
    context: ModuleValidationContext,
    opcode: Int,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> = when (opcode) {
    LOCAL_GET -> LocalGetInstructionValidator(context, instruction as VariableInstruction.LocalGet)
    LOCAL_SET -> LocalSetInstructionValidator(context, instruction as VariableInstruction.LocalSet)
    LOCAL_TEE -> LocalTeeInstructionValidator(context, instruction as VariableInstruction.LocalTee)
    GLOBAL_GET -> GlobalGetInstructionValidator(context, instruction as VariableInstruction.GlobalGet)
    GLOBAL_SET -> GlobalSetInstructionValidator(context, instruction as VariableInstruction.GlobalSet)
    else -> VariableInstructionValidator(context, instruction as VariableInstruction)
}
