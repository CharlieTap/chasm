package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

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
    private val callConfig: HostCallConfig,
) : GcAllocator {

    override fun struct(
        type: RTT,
        fields: LongArray,
    ): Long {

        val runtime = store.liveStore()
        val threshold = collectionThreshold(runtime)

        if (threshold != null) {
            runtime.heap.prepareStructAllocation(runtime, type, threshold)
        }

        return runtime.heap.allocateStruct(type, fields)
    }

    override fun filledArray(
        type: RTT,
        length: Int,
        value: Long,
    ): Long {

        val runtime = store.liveStore()
        val threshold = collectionThreshold(runtime)

        if (threshold != null) {
            runtime.heap.prepareArrayAllocation(runtime, type, length, threshold)
        }

        return runtime.heap.allocateArrayFilled(type, length, value)
    }

    override fun array(
        type: RTT,
        elements: LongArray,
        offset: Int,
        count: Int,
    ): Long {

        val runtime = store.liveStore()
        val threshold = collectionThreshold(runtime)

        if (threshold != null) {
            runtime.heap.prepareArrayAllocation(runtime, type, count, threshold)
        }

        return runtime.heap.allocateArrayFromElements(type, elements, offset, count)
    }

    override fun exception(
        tag: Address.Tag,
        payload: LongArray,
    ): Long {

        val runtime = store.liveStore()
        val threshold = collectionThreshold(runtime)

        if (threshold != null) {
            runtime.heap.prepareExceptionAllocation(runtime, tag, threshold)
        }

        return runtime.heap.allocateException(tag, payload)
    }

    @Suppress("DEPRECATION")
    private fun collectionThreshold(runtime: RuntimeStore): Long? {

        val config = callConfig.current
        if (runtime.invocationDepth == 0 || config == null) {
            return (runtime.config.gcThreshold ?: DEFAULT_THRESHOLD).bytes
        }

        if (config.gcStrategy != GCStrategy.TRADITIONAL) {
            return null
        }

        return config.gcThreshold.bytes
    }

    private companion object {
        val DEFAULT_THRESHOLD = GCThreshold.MB(8)
    }
}
