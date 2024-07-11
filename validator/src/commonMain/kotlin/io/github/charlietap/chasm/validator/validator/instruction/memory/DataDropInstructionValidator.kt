package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun DataDropInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.DataDrop,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.dataIdx.idx.toInt() !in context.module.dataSegments.indices) {
        Err(InstructionValidatorError.UnknownDataSegment).bind<Unit>()
    }
}
