package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal typealias MemoryArgumentValidator = (
    ModuleValidationContext,
    MemArg,
    Index.MemoryIndex,
    Int,
    Boolean,
) -> Result<Unit, ModuleValidatorError>

internal fun MemArgValidator(
    context: ModuleValidationContext,
    arg: MemArg,
    memoryIndex: Index.MemoryIndex,
    size: Int,
    exactAlignment: Boolean,
): Result<Unit, ModuleValidatorError> {
    val naturalAlignment = size.countTrailingZeroBits().toUInt()

    if (exactAlignment) {
        if (arg.align != naturalAlignment) {
            return Err(InstructionValidatorError.UnnaturalMemoryAlignment)
        }
    } else {
        if (arg.align > naturalAlignment) {
            return Err(InstructionValidatorError.UnnaturalMemoryAlignment)
        }
    }

    val memoryType = context.memories.getOrNull(memoryIndex.idx.toInt())
        ?: return Err(InstructionValidatorError.UnknownMemory)
    if (memoryType.addressType == AddressType.I32 && arg.offset > UInt.MAX_VALUE) {
        return Err(InstructionValidatorError.OutOfBounds)
    }
    return Ok(Unit)
}
