package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import kotlin.jvm.JvmField

@InternalChasmApi
abstract class CompiledStruct(
    @JvmField val rtt: Rtt,
) : ManagedStruct() {

    final override fun engine(): Engine {
        return rtt.engine
    }

    final override fun readField(index: Int): Long {
        return storageLayout().readBits(this, index)
    }

    final override fun readFields(
        offset: Int,
        buffer: LongArray,
        bufferOffset: Int,
        count: Int,
    ) {

        val storageLayout = storageLayout()

        checkRange(offset, count, storageLayout.fields.size)
        checkRange(bufferOffset, count, buffer.size)

        if (count < MIN_BULK_READ_FIELDS) {
            for (field in 0 until count) {
                buffer[bufferOffset + field] = storageLayout.readBits(this, offset + field)
            }
        } else {
            storageLayout.readBitsInBulk(this, offset, buffer, bufferOffset, count)
        }
    }

    final override fun readFieldReference(index: Int): Any? {
        return storageLayout().readReference(this, index)
    }

    final override fun writeField(
        index: Int,
        value: Long,
    ) {
        rtt.engine.writeStructField(this, index, value)
    }

    final override fun writeFieldReference(
        index: Int,
        value: Any?,
    ) {
        rtt.engine.writeStructReference(this, index, value)
    }

    private fun storageLayout(): StructStorageLayout {
        return rtt.layout as StructStorageLayout
    }

    open fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {}

    open fun readInt(lane: Int): Int {
        throw laneError(lane)
    }

    open fun readReference(lane: Int): Any? {
        throw laneError(lane)
    }

    open fun writeInt(
        lane: Int,
        value: Int,
    ) {
        throw laneError(lane)
    }

    open fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        throw laneError(lane)
    }

    open fun readSpilledInt(slot: Int): Int {
        throw laneError(slot)
    }

    open fun readSpilledReference(slot: Int): Any? {
        throw laneError(slot)
    }

    open fun writeSpilledInt(
        slot: Int,
        value: Int,
    ) {
        throw laneError(slot)
    }

    open fun writeSpilledReference(
        slot: Int,
        value: Any?,
    ) {
        throw laneError(slot)
    }
}

internal fun storeLane(
    destination: Int,
    value: Int,
    buffer: LongArray,
    shift: Int,
    from: Int,
    until: Int,
) {

    val index = (destination shr DESTINATION_FIELD_SHIFT) + shift
    if (index < from || index >= until) {
        return
    }

    when (destination and DESTINATION_PART_MASK) {
        WHOLE_FIELD -> buffer[index] = value.toLong()
        LOW_HALF -> buffer[index] = value.toLong() and LOW_BITS
        else -> buffer[index] = buffer[index] or (value.toLong() shl Int.SIZE_BITS)
    }
}

internal const val DESTINATION_FIELD_SHIFT = 2
internal const val DESTINATION_PART_MASK = 3
internal const val WHOLE_FIELD = 0
internal const val LOW_HALF = 1
internal const val HIGH_HALF = 2
internal const val UNUSED_LANE = -1
internal const val LOW_BITS = 0xFFFFFFFFL
