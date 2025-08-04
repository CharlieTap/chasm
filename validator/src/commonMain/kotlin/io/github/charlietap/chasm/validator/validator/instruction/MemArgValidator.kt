package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.memoryType
import io.github.charlietap.chasm.validator.ext.size
import kotlin.math.pow

internal inline fun MemArgValidator(
    context: ValidationContext,
    arg: MemArg,
): Result<Unit, ModuleValidatorError> = binding {

    val exactAlignment = context.instruction is AtomicMemoryInstruction
    val size = context.instruction.size()
    val alignment = 2f.pow(arg.align.toFloat()).toInt()

    if (exactAlignment) {
        if (alignment != size) {
            Err(InstructionValidatorError.UnnaturalMemoryAlignment).bind<Unit>()
        }
    } else {
        if (alignment > size) {
            Err(InstructionValidatorError.UnnaturalMemoryAlignment).bind<Unit>()
        }
    }

    val memoryType = context.instructionMemoryType().bind()
    if (memoryType.addressType == AddressType.I32 && arg.offset > UInt.MAX_VALUE) {
        Err(InstructionValidatorError.OutOfBounds).bind<Unit>()
    }
}

private fun ValidationContext.instructionMemoryType(): Result<MemoryType, ModuleValidatorError> {
    return when (val instruction = this.instruction) {
        is MemoryInstruction.Load -> memoryType(instruction.memoryIndex)
        is MemoryInstruction.Store -> memoryType(instruction.memoryIndex)
        is AtomicMemoryInstruction.Load -> memoryType(instruction.memoryIndex)
        is AtomicMemoryInstruction.Store -> memoryType(instruction.memoryIndex)
        else -> Err(InstructionValidatorError.UnknownMemory)
    }
}
