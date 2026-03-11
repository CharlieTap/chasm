package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.MemoryInstance

sealed interface MemorySuperInstruction : LinkedInstruction {

    data class I32LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F32LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F32LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F64LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F64LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load8SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load8SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load8UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load8US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load16SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load16SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load16UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Load16US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load8SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load8SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load8UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load8US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load16SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load16SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load16UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load16US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load32SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load32SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load32UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Load32US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class MemorySizeS(
        val destinationSlot: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryGrowI(
        val pagesToAdd: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val max: Int,
    ) : MemorySuperInstruction

    data class MemoryGrowS(
        val pagesToAddSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryInstance,
        val max: Int,
    ) : MemorySuperInstruction

    data class MemoryInitIii(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitIis(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitIsi(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitIss(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitSii(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitSis(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitSsi(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryInitSss(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryInstance,
        val data: io.github.charlietap.chasm.runtime.instance.DataInstance,
    ) : MemorySuperInstruction

    data class MemoryCopyIii(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopyIis(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopyIsi(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopyIss(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopySii(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopySis(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopySsi(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryCopySss(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryInstance,
        val dstMemory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillIii(
        val bytesToFill: Int,
        val fillValue: Int,
        val offset: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillIis(
        val bytesToFill: Int,
        val fillValue: Int,
        val offsetSlot: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillIsi(
        val bytesToFill: Int,
        val fillValueSlot: Int,
        val offset: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillIss(
        val bytesToFill: Int,
        val fillValueSlot: Int,
        val offsetSlot: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillSii(
        val bytesToFillSlot: Int,
        val fillValue: Int,
        val offset: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillSis(
        val bytesToFillSlot: Int,
        val fillValue: Int,
        val offsetSlot: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillSsi(
        val bytesToFillSlot: Int,
        val fillValueSlot: Int,
        val offset: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class MemoryFillSss(
        val bytesToFillSlot: Int,
        val fillValueSlot: Int,
        val offsetSlot: Int,
        val memory: MemoryInstance,
    ) : MemorySuperInstruction

    data class I32StoreIi(
        val value: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32StoreIs(
        val value: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64StoreIi(
        val value: Long,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64StoreIs(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F32StoreIi(
        val value: Float,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F32StoreIs(
        val value: Float,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F32StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F32StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F64StoreIi(
        val value: Double,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F64StoreIs(
        val value: Double,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F64StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class F64StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store8Ii(
        val value: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store8Is(
        val value: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store8Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store8Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store16Ii(
        val value: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store16Is(
        val value: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store16Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I32Store16Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store8Ii(
        val value: Long,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store8Is(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store8Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store8Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store16Ii(
        val value: Long,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store16Is(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store16Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store16Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store32Ii(
        val value: Long,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store32Is(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store32Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction

    data class I64Store32Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryInstance,
        val memArg: MemArg,
    ) : MemorySuperInstruction
}
