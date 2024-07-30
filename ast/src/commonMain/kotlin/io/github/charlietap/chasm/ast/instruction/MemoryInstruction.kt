package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface MemoryInstruction : Instruction {

    data class I32Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class F32Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class F64Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Load8S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Load8U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Load16S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Load16U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load8S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load8U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load16S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load16U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load32S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Load32U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class F32Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class F64Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Store8(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I32Store16(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Store8(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Store16(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    data class I64Store32(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class MemorySize(val memoryIndex: Index.MemoryIndex) : MemoryInstruction

    @JvmInline
    value class MemoryGrow(val memoryIndex: Index.MemoryIndex) : MemoryInstruction

    data class MemoryInit(val memoryIndex: Index.MemoryIndex, val dataIndex: Index.DataIndex) : MemoryInstruction

    @JvmInline
    value class DataDrop(val dataIdx: Index.DataIndex) : MemoryInstruction

    data class MemoryCopy(val srcIndex: Index.MemoryIndex, val dstIndex: Index.MemoryIndex) : MemoryInstruction

    @JvmInline
    value class MemoryFill(val memoryIndex: Index.MemoryIndex) : MemoryInstruction
}
