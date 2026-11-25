package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.ConcurrentHashMap

@InternalChasmApi
class ExceptionOperations(
    private val store: Store,
    private val checks: ReferenceChecks,
    private val pending: PendingExceptions,
) {

    private val payloads = ConcurrentHashMap<Int, Array<ReferenceCheck?>>()

    fun createException(
        tag: Address.Tag,
        words: LongArray,
        references: Array<Any?>,
    ): WasmException {

        val payload = payload(tag.address)
        val count = payload.size

        require(words.size >= count && references.size >= count) { "the tag has $count parameters" }

        val payloadWords = LongArray(count)
        val payloadReferences = arrayOfNulls<Any>(count)
        payload.forEachIndexed { index, check ->
            if (check == null) {
                payloadWords[index] = words[index]
            } else {
                require(check.accepts(references[index])) { "payload $index does not match the tag" }
                payloadReferences[index] = references[index]
            }
        }

        return createException(store, tag.address, payloadWords, payloadReferences)
    }

    fun exceptionTag(exception: Any?): Address.Tag {
        return Address.Tag(exception(exception).tag)
    }

    fun payloadSize(exception: Any?): Int {
        return payload(exception(exception).tag).size
    }

    fun readPayload(
        exception: Any?,
        index: Int,
    ): Long {

        val managed = exception(exception)
        val payload = payload(managed.tag)

        checkIndex(index, payload.size)
        require(payload[index] == null) { "payload $index is a reference" }

        return managed.words[index]
    }

    fun readPayloadReference(
        exception: Any?,
        index: Int,
    ): Any? {

        val managed = exception(exception)
        val payload = payload(managed.tag)

        checkIndex(index, payload.size)
        requireNotNull(payload[index]) { "payload $index is a number" }

        return managed.references[index]
    }

    fun raise(exception: Any?): Nothing {
        pending.raise(exception(exception))
    }

    private fun exception(value: Any?): WasmException {

        require(value is WasmException && checks.owned(value)) { "not an exception of this store" }

        return value
    }

    private fun payload(tag: Int): Array<ReferenceCheck?> {

        payloads[tag]?.let { payload -> return payload }

        val instance = try {
            store.heap.tag(Address.Tag(tag))
        } catch (_: InvocationException) {
            throw IllegalArgumentException("tag $tag is not a tag of this store")
        }
        val parameters = instance.type.functionType.params.types
        val payload = Array(parameters.size) { index -> (parameters[index] as? ValueType.Reference)?.let { reference -> checks.check(reference.referenceType, null) } }

        return payloads.putIfAbsent(tag, payload) ?: payload
    }
}
