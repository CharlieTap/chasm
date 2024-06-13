package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun I64Load8UInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.I64Load8U,
): Result<Unit, ModuleValidatorError> = binding {

    if (context.memories.isEmpty()) {
        Err(InstructionValidatorError.UnknownMemory).bind<Unit>()
    }

    when (instruction.memArg.align) {
        0u -> Ok(Unit)
        else -> Err(InstructionValidatorError.UnnaturalMemoryAlignment).bind<Unit>()
    }
}
