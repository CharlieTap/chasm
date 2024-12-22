package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import kotlin.jvm.JvmInline

sealed interface MemoryInstruction : ExecutionInstruction {

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
    value class MemoryGrow(val memory: MemoryInstance) : MemoryInstruction

    data class MemoryInit(val memoryIndex: Index.MemoryIndex, val dataIndex: Index.DataIndex) : MemoryInstruction

    @JvmInline
    value class DataDrop(val data: DataInstance) : MemoryInstruction

    data class MemoryCopy(val srcMemory: MemoryInstance, val dstMemory: MemoryInstance) : MemoryInstruction

    @JvmInline
    value class MemoryFill(val memory: MemoryInstance) : MemoryInstruction
}
