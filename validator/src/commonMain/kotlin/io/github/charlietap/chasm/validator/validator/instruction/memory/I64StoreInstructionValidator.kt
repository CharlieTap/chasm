package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64

internal fun I64StoreInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.I64Store,
): Result<Unit, ModuleValidatorError> = binding {

    if (context.memories.isEmpty()) {
        Err(InstructionValidatorError.UnknownMemory).bind<Unit>()
    }

    when (instruction.memArg.align) {
        in 0u..3u -> Unit
        else -> Err(InstructionValidatorError.UnnaturalMemoryAlignment).bind<Unit>()
    }

    context.popI64().bind()
    context.popI32().bind()
}
