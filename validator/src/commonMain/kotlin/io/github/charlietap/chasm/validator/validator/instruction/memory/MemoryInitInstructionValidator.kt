package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32

internal fun MemoryInitInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryInit,
): Result<Unit, ModuleValidatorError> = binding {
    if (context.memories.isEmpty()) {
        Err(InstructionValidatorError.UnknownMemory).bind<Unit>()
    }
    if (instruction.dataIdx.idx.toInt() !in context.module.dataSegments.indices) {
        Err(InstructionValidatorError.UnknownDataSegment).bind<Unit>()
    }

    repeat(3) {
        context.popI32().bind()
    }
}
