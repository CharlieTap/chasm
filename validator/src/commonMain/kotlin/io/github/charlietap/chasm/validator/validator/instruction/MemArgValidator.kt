package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
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
        is AtomicMemoryInstruction.ReadModifyWrite -> memoryType(instruction.memoryIndex)
        is AtomicMemoryInstruction.CompareExchange -> memoryType(instruction.memoryIndex)
        is AtomicMemoryInstruction.Notify -> memoryType(instruction.memoryIndex)
        is AtomicMemoryInstruction.I32Wait -> memoryType(instruction.memoryIndex)
        is AtomicMemoryInstruction.I64Wait -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Store -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load8x8S -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load8x8U -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load16x4S -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load16x4U -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load32x2S -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load32x2U -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load8Splat -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load16Splat -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load32Splat -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load64Splat -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load32Zero -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load64Zero -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load8Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load16Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load32Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Load64Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Store8Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Store16Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Store32Lane -> memoryType(instruction.memoryIndex)
        is VectorInstruction.V128Store64Lane -> memoryType(instruction.memoryIndex)
        else -> Err(InstructionValidatorError.UnknownMemory)
    }
}
