package io.github.charlietap.chasm.validator.validator.function.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun F32StoreInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.F32Store,
): Result<Unit, ModuleValidatorError> {
    return when (instruction.memArg.align) {
        in 0u..2u -> Ok(Unit)
        else -> Err(InstructionValidatorError.UnnaturalMemoryAlignment)
    }
}
