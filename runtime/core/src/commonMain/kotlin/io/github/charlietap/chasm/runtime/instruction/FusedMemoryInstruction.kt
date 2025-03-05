package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import kotlin.jvm.JvmInline

sealed interface FusedMemoryInstruction : LinkedInstruction {

    data class I32Load(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F32Load(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F64Load(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load8S(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load8U(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load16S(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Load16U(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load8S(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load8U(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load16S(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load16U(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load32S(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Load32U(
        val address: LoadOp,
        val destination: StoreOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Store(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F32Store(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class F64Store(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Store8(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I32Store16(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store8(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store16(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    data class I64Store32(
        val value: LoadOp,
        val address: LoadOp,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : FusedMemoryInstruction

    @JvmInline
    value class MemorySize(val memory: MemoryInstance) : FusedMemoryInstruction

    data class MemoryGrow(
        val memory: MemoryInstance,
        val max: Int,
    ) : FusedMemoryInstruction

    data class MemoryInit(
        val memory: MemoryInstance,
        val data: DataInstance,
    ) : FusedMemoryInstruction

    @JvmInline
    value class DataDrop(val data: DataInstance) : FusedMemoryInstruction

    data class MemoryCopy(
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : FusedMemoryInstruction

    @JvmInline
    value class MemoryFill(val memory: MemoryInstance) : FusedMemoryInstruction
}
