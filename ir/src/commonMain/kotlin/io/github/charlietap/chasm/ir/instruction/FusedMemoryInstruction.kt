package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedMemoryInstruction : Instruction {

    data class I32Load(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F32Load(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F64Load(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load8S(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load8U(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load16S(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load16U(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load8S(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load8U(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load16S(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load16U(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load32S(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load32U(
        val addressOperand: FusedOperand,
        val destination: FusedDestination,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Store(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F32Store(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F64Store(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Store8(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Store16(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store8(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store16(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store32(
        val valueOperand: FusedOperand,
        val addressOperand: FusedOperand,
        val memoryIndex: Index.MemoryIndex,
        val memArg: MemArg,
    ) : FusedMemoryInstruction
}
