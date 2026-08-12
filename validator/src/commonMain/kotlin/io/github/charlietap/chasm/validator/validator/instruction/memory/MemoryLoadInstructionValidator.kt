package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.popMemoryAddressOrThrow
import io.github.charlietap.chasm.validator.ext.pushF32
import io.github.charlietap.chasm.validator.ext.pushF64
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.pushI64
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun MemoryLoadInstructionValidator(
    context: ModuleValidationContext,
    instruction: MemoryInstruction.Load,
): Result<Unit, ModuleValidatorError> {

    MemArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), false).getOrThrowValidation()

    context.popMemoryAddressOrThrow(instruction.memoryIndex)

    when (instruction) {
        is MemoryInstruction.Load.I32 -> context.pushI32()
        is MemoryInstruction.Load.I64 -> context.pushI64()
        is MemoryInstruction.Load.F32 -> context.pushF32()
        is MemoryInstruction.Load.F64 -> context.pushF64()
    }
    return Ok(Unit)
}
