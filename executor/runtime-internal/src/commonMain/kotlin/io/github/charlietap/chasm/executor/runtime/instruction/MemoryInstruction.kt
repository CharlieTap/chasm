package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import kotlin.jvm.JvmInline

sealed interface MemoryInstruction : ExecutionInstruction {

    data class I32Load(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class F32Load(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class F64Load(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Load8S(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Load8U(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Load16S(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Load16U(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load8S(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load8U(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load16S(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load16U(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load32S(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Load32U(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Store(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Store(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class F32Store(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class F64Store(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Store8(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I32Store16(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Store8(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Store16(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    data class I64Store32(val memory: MemoryInstance, val memArg: MemArg) : MemoryInstruction

    @JvmInline
    value class MemorySize(val memory: MemoryInstance) : MemoryInstruction

    @JvmInline
    value class MemoryGrow(val memory: MemoryInstance) : MemoryInstruction

    data class MemoryInit(
        val memory: MemoryInstance,
        val data: DataInstance,
    ) : MemoryInstruction

    @JvmInline
    value class DataDrop(val data: DataInstance) : MemoryInstruction

    data class MemoryCopy(
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemoryInstruction

    @JvmInline
    value class MemoryFill(val memory: MemoryInstance) : MemoryInstruction
}
