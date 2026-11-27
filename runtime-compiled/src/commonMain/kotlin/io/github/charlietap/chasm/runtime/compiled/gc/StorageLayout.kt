package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.storage.F32Array
import io.github.charlietap.chasm.runtime.compiled.gc.storage.F64Array
import io.github.charlietap.chasm.runtime.compiled.gc.storage.I16Array
import io.github.charlietap.chasm.runtime.compiled.gc.storage.I32Array
import io.github.charlietap.chasm.runtime.compiled.gc.storage.I64Array
import io.github.charlietap.chasm.runtime.compiled.gc.storage.I8Array
import io.github.charlietap.chasm.runtime.compiled.gc.storage.RefArray
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
sealed interface StorageLayout

internal data class StorageLane(
    val index: Int,
    val spilled: Boolean,
)

internal data class StorageField(
    val type: FieldType,
    val lanes: List<StorageLane>,
)

internal fun interface StructAllocator {

    fun newStruct(rtt: Rtt): CompiledStruct
}

internal class StructStorageLayout(
    val fields: List<StorageField>,
    val bytes: Int,
    private val allocator: StructAllocator,
) : StorageLayout {

    fun newStruct(rtt: Rtt): CompiledStruct {
        return allocator.newStruct(rtt)
    }

    fun readBits(
        struct: CompiledStruct,
        index: Int,
    ): Long {

        val field = numberField(index)
        val low = readInt(struct, field.lanes[0])

        return if (field.lanes.size == 1) {
            low.toLong()
        } else {
            (low.toLong() and LOW_BITS) or (readInt(struct, field.lanes[1]).toLong() shl Int.SIZE_BITS)
        }
    }

    fun readReference(
        struct: CompiledStruct,
        index: Int,
    ): Any? {

        val lane = referenceField(index).lanes[0]

        return if (lane.spilled) struct.readSpilledReference(lane.index) else struct.readReference(lane.index)
    }

    fun writeBits(
        struct: CompiledStruct,
        index: Int,
        value: Long,
    ) {

        val field = numberField(index)

        if (field.lanes.size == 1) {
            writeInt(struct, field.lanes[0], value.toInt() and packedMask(field.type))
        } else {
            writeInt(struct, field.lanes[0], value.toInt())
            writeInt(struct, field.lanes[1], (value ushr Int.SIZE_BITS).toInt())
        }
    }

    fun writeReference(
        struct: CompiledStruct,
        index: Int,
        value: Any?,
    ) {

        val lane = referenceField(index).lanes[0]

        if (lane.spilled) {
            struct.writeSpilledReference(lane.index, value)
        } else {
            struct.writeReference(lane.index, value)
        }
    }

    private fun numberField(index: Int): StorageField {

        val field = fields.getOrNull(index) ?: throw fieldIndexError(index)
        if (holdsReference(field.type)) {
            throw referenceFieldError(index)
        }

        return field
    }

    private fun referenceField(index: Int): StorageField {

        val field = fields.getOrNull(index) ?: throw fieldIndexError(index)
        if (!holdsReference(field.type)) {
            throw numberFieldError(index)
        }

        return field
    }

    private fun readInt(
        struct: CompiledStruct,
        lane: StorageLane,
    ): Int {
        return if (lane.spilled) struct.readSpilledInt(lane.index) else struct.readInt(lane.index)
    }

    private fun writeInt(
        struct: CompiledStruct,
        lane: StorageLane,
        value: Int,
    ) {
        if (lane.spilled) {
            struct.writeSpilledInt(lane.index, value)
        } else {
            struct.writeInt(lane.index, value)
        }
    }

    private fun holdsReference(type: FieldType): Boolean {
        return (type.storageType as? StorageType.Value)?.type is ValueType.Reference
    }

    private fun packedMask(type: FieldType): Int {
        return when ((type.storageType as? StorageType.Packed)?.type) {
            PackedType.I8 -> I8_MASK
            PackedType.I16 -> I16_MASK
            null -> ALL_BITS
        }
    }

    private companion object {
        const val LOW_BITS = 0xFFFFFFFFL
        const val I8_MASK = 0xFF
        const val I16_MASK = 0xFFFF
        const val ALL_BITS = -1
    }
}

internal class ArrayStorageLayout(
    val type: FieldType,
    val element: ArrayElement,
) : StorageLayout {

    fun newStorage(length: Int): Any {
        return when (element) {
            ArrayElement.I8 -> ByteArray(length)
            ArrayElement.I16 -> CharArray(length)
            ArrayElement.I32 -> IntArray(length)
            ArrayElement.I64 -> LongArray(length)
            ArrayElement.F32 -> FloatArray(length)
            ArrayElement.F64 -> DoubleArray(length)
            ArrayElement.REFERENCE -> arrayOfNulls<Any>(length)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun newArray(
        rtt: Rtt,
        storage: Any,
    ): CompiledArray {
        return when (element) {
            ArrayElement.I8 -> I8Array(rtt, storage as ByteArray)
            ArrayElement.I16 -> I16Array(rtt, storage as CharArray)
            ArrayElement.I32 -> I32Array(rtt, storage as IntArray)
            ArrayElement.I64 -> I64Array(rtt, storage as LongArray)
            ArrayElement.F32 -> F32Array(rtt, storage as FloatArray)
            ArrayElement.F64 -> F64Array(rtt, storage as DoubleArray)
            ArrayElement.REFERENCE -> RefArray(rtt, storage as Array<Any?>)
        }
    }
}

internal enum class ArrayElement(
    val storage: Class<*>,
    val shift: Int,
) {
    I8(ByteArray::class.java, 0),
    I16(CharArray::class.java, 1),
    I32(IntArray::class.java, 2),
    I64(LongArray::class.java, 3),
    F32(FloatArray::class.java, 2),
    F64(DoubleArray::class.java, 3),
    REFERENCE(Array<Any?>::class.java, 2),
}
