package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.memoryType
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator

internal fun MemoryCopyInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryCopy,
): Result<Unit, ModuleValidatorError> =
    MemoryCopyInstructionValidator(
        context = context,
        instruction = instruction,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemoryCopyInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.srcIndex).bind()
    memoryIndexValidator(context, instruction.dstIndex).bind()

    val srcMemory = context.memoryType(instruction.srcIndex).bind()
    val dstMemory = context.memoryType(instruction.dstIndex).bind()
    // min(srcAddressType, dstAddressType)
    if (srcMemory.addressType == AddressType.I32 || dstMemory.addressType == AddressType.I32) {
        context.popI32().bind()
    } else {
        context.popI64().bind()
    }
    context.popMemoryAddress(instruction.srcIndex).bind()
    context.popMemoryAddress(instruction.dstIndex).bind()
}
