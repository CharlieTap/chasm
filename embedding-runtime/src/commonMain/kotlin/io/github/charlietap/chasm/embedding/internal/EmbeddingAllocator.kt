package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.type.RTT

internal interface GcAllocator {

    fun struct(
        type: RTT,
        fields: LongArray,
    ): Long

    fun filledArray(
        type: RTT,
        length: Int,
        value: Long,
    ): Long

    fun array(
        type: RTT,
        elements: LongArray,
        offset: Int,
        count: Int,
    ): Long

    fun exception(
        tag: Address.Tag,
        payload: LongArray,
    ): Long
}

internal class EmbeddingAllocator(
    private val store: StoreRuntime,
) : GcAllocator {

    override fun struct(
        type: RTT,
        fields: LongArray,
    ): Long {

        val runtime = store.liveStore()
        runtime.heap.prepareStructAllocation(runtime, type)

        return runtime.heap.allocateStruct(type, fields)
    }

    override fun filledArray(
        type: RTT,
        length: Int,
        value: Long,
    ): Long {

        val runtime = store.liveStore()
        runtime.heap.prepareArrayAllocation(runtime, type, length)

        return runtime.heap.allocateArrayFilled(type, length, value)
    }

    override fun array(
        type: RTT,
        elements: LongArray,
        offset: Int,
        count: Int,
    ): Long {

        val runtime = store.liveStore()
        runtime.heap.prepareArrayAllocation(runtime, type, count)

        return runtime.heap.allocateArrayFromElements(type, elements, offset, count)
    }

    override fun exception(
        tag: Address.Tag,
        payload: LongArray,
    ): Long {

        val runtime = store.liveStore()
        runtime.heap.prepareExceptionAllocation(runtime, tag)

        return runtime.heap.allocateException(tag, payload)
    }
}
