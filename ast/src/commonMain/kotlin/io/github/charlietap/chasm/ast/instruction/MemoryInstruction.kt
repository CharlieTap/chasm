package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface MemoryInstruction : Instruction {

    sealed interface Load : MemoryInstruction {

        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg

        data class I32Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class F32Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class F64Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I32Load8S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I32Load8U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I32Load16S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I32Load16U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load8S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load8U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load16S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load16U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load32S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load

        data class I64Load32U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Load
    }

    sealed interface Store : MemoryInstruction {

        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg

        data class I32Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class I64Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class F32Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class F64Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class I32Store8(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class I32Store16(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class I64Store8(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class I64Store16(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store

        data class I64Store32(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : Store
    }

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
