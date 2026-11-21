package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostException
import io.github.charlietap.chasm.host.HostExceptions
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ValueType

class ManagedExceptions(
    private val bridge: ManagedReferenceBridge,
    private val store: Store,
) : HostExceptions {

    override val hasPending: Boolean
        get() = store.heap.hasPending

    context(resources: HostResources)
    override fun create(
        tag: HostTag,
        payload: HostStack,
        payloadOffset: Int,
    ): HostException {

        val parameters = parameters(tag.rawAddress)
        val words = LongArray(parameters.size)
        val references = arrayOfNulls<Any>(parameters.size)
        parameters.forEachIndexed { index, parameter ->
            if (parameter is ValueType.Reference) {
                references[index] = bridge.fromHost(payload[payloadOffset + index])
            } else {
                words[index] = payload[payloadOffset + index]
            }
        }

        val exception = WasmException(tag.rawAddress, words, references)
        return HostException(bridge.references.rootScoped(bridge.toHost(exception, false)))
    }

    override fun tag(exception: HostException): HostTag {
        return HostTag(exception(exception).tag)
    }

    override fun payloadSize(exception: HostException): Int {
        return parameters(exception(exception).tag).size
    }

    override fun readPayload(
        exception: HostException,
        index: Int,
    ): Long {

        val value = exception(exception)
        val parameter = parameters(value.tag)[index]

        return if (parameter is ValueType.Reference) {
            bridge.toHost(value.references[index], parameter.isExtern())
        } else {
            value.words[index]
        }
    }

    override fun readPayload(
        exception: HostException,
        sourceOffset: Int,
        destination: LongArray,
        destinationOffset: Int,
        length: Int,
    ): LongArray {
        for (index in 0 until length) destination[destinationOffset + index] = readPayload(exception, sourceOffset + index)
        return destination
    }

    override fun takePending(): HostException {
        val reference = store.heap.takePendingExceptionReference()
        bridge.references.rootScoped(reference)
        bridge.releaseRaised()
        return HostException(reference)
    }

    override fun raise(exception: HostException): Nothing {
        bridge.holdRaised(exception.rawReference)
        store.heap.setPendingException(exception.rawReference)
        throw HostRaisedWasmException.instance
    }

    override fun raisePending(): Nothing {
        throw HostRaisedWasmException.instance
    }

    private fun exception(exception: HostException): WasmException {
        return bridge.fromHost(exception.rawReference) as WasmException
    }

    private fun parameters(tag: Int): List<ValueType> {
        return store.heap.tag(Address.Tag(tag)).type.functionType.params.types
    }

    private fun ValueType.Reference.isExtern(): Boolean {
        val heap = referenceType.heapType
        return heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern
    }
}
