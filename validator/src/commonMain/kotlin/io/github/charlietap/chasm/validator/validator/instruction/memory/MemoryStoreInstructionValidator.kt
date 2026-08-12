package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.popF32OrThrow
import io.github.charlietap.chasm.validator.ext.popF64OrThrow
import io.github.charlietap.chasm.validator.ext.popI32OrThrow
import io.github.charlietap.chasm.validator.ext.popI64OrThrow
import io.github.charlietap.chasm.validator.ext.popMemoryAddressOrThrow
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun MemoryStoreInstructionValidator(
    context: ModuleValidationContext,
    instruction: MemoryInstruction.Store,
): Result<Unit, ModuleValidatorError> {

    MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()

    when (instruction) {
        is MemoryInstruction.Store.I32 -> context.popI32OrThrow()
        is MemoryInstruction.Store.I64 -> context.popI64OrThrow()
        is MemoryInstruction.Store.F32 -> context.popF32OrThrow()
        is MemoryInstruction.Store.F64 -> context.popF64OrThrow()
    }
    context.popMemoryAddressOrThrow(instruction.memoryIndex)
    return Ok(Unit)
}
