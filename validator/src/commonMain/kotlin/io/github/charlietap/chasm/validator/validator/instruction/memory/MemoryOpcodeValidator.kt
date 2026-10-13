package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.FIRST_LOAD
import io.github.charlietap.chasm.validator.validator.instruction.FIRST_STORE
import io.github.charlietap.chasm.validator.validator.instruction.LAST_LOAD
import io.github.charlietap.chasm.validator.validator.instruction.LAST_STORE
import io.github.charlietap.chasm.validator.validator.instruction.MEMORY_GROW
import io.github.charlietap.chasm.validator.validator.instruction.MEMORY_SIZE

internal fun MemoryInstructionValidator(
    context: ModuleValidationContext,
    opcode: Int,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> = when (opcode) {
    in FIRST_LOAD..LAST_LOAD -> MemoryLoadInstructionValidator(context, instruction as MemoryInstruction.Load)
    in FIRST_STORE..LAST_STORE -> MemoryStoreInstructionValidator(context, instruction as MemoryInstruction.Store)
    MEMORY_SIZE -> MemorySizeInstructionValidator(context, instruction as MemoryInstruction.MemorySize)
    MEMORY_GROW -> MemoryGrowInstructionValidator(context, instruction as MemoryInstruction.MemoryGrow)
    else -> MemoryInstructionValidator(context, instruction as MemoryInstruction)
}
