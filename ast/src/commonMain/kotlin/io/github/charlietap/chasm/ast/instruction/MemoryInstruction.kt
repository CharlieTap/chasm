package io.github.charlietap.chasm.ast.instruction

import kotlin.jvm.JvmInline

sealed interface MemoryInstruction : Instruction {
    @JvmInline
    value class I32Load(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class F32Load(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class F64Load(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Load8S(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Load8U(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Load16S(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Load16U(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load8S(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load8U(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load16S(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load16U(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load32S(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Load32U(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Store(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Store(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class F32Store(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class F64Store(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Store8(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I32Store16(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Store8(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Store16(val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class I64Store32(val memArg: MemArg) : MemoryInstruction

    data object MemorySize : MemoryInstruction

    data object MemoryGrow : MemoryInstruction

    @JvmInline
    value class MemoryInit(val dataIdx: Index.DataIndex) : MemoryInstruction

    @JvmInline
    value class DataDrop(val dataIdx: Index.DataIndex) : MemoryInstruction

    data object MemoryCopy : MemoryInstruction

    data object MemoryFill : MemoryInstruction
}
