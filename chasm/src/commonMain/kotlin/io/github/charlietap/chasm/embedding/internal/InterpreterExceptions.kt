package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.host.withScope
import io.github.charlietap.chasm.type.ValueType

internal interface ExceptionAccess {

    fun createException(
        tag: Tag,
        words: LongArray,
        references: Array<Any?>,
    ): ExceptionReference

    fun exceptionTag(exception: ExceptionReference): Tag

    fun payloadSize(exception: ExceptionReference): Int

    fun readPayload(
        exception: ExceptionReference,
        index: Int,
    ): Long

    fun readPayloadReference(
        exception: ExceptionReference,
        index: Int,
    ): Any?

    fun raise(exception: ExceptionReference): Nothing

    fun raisePending(): Nothing

    fun takePending(): ExceptionReference

    fun hasPending(): Boolean
}

internal class InterpreterExceptions(
    private val handles: EngineHandles,
    private val codec: ReferenceCodec,
    private val heldReferences: HeldReferences,
    private val allocator: GcAllocator,
    private val heap: WasmHeap,
) : ExceptionAccess {

    override fun createException(
        tag: Tag,
        words: LongArray,
        references: Array<Any?>,
    ): ExceptionReference {

        val address = handles.tagAddress(tag)
        val parameters = withAccessorFailures { heap.tag(address) }.type.functionType.params.types
        val count = parameters.size
        val hasReferences = parameters.any { parameter -> parameter is ValueType.Reference }
        require(words.size >= count) { "the tag has $count parameters" }
        require(!hasReferences || references.size >= count) { "the tag has $count parameters" }

        return withAccessorFailures {
            heap.withScope {
                val payload = LongArray(count) { index ->
                    val reference = referenceType(parameters[index])
                    if (reference == null) {
                        words[index]
                    } else {
                        codec.encode(codec.resolve(reference), references[index])
                    }
                }
                heldReferences.exception(allocator.exception(address, payload))
            }
        }
    }

    override fun exceptionTag(exception: ExceptionReference): Tag {

        val held = codec.exception(exception)

        return handles.tag(withAccessorFailures { heap.exceptionTagAddress(held.raw) })
    }

    override fun payloadSize(exception: ExceptionReference): Int {
        return parameters(codec.exception(exception).raw).size
    }

    override fun readPayload(
        exception: ExceptionReference,
        index: Int,
    ): Long {

        val raw = codec.exception(exception).raw
        val parameters = parameters(raw)
        checkIndex(index, parameters.size)
        require(referenceType(parameters[index]) == null) { "payload $index is a reference" }

        return heap.getExceptionFieldTrusted(raw, index)
    }

    override fun readPayloadReference(
        exception: ExceptionReference,
        index: Int,
    ): Any? {

        val raw = codec.exception(exception).raw
        val parameters = parameters(raw)
        checkIndex(index, parameters.size)
        requireNotNull(referenceType(parameters[index])) { "payload $index is a number" }

        return codec.decode(heap.getExceptionFieldTrusted(raw, index))
    }

    override fun raise(exception: ExceptionReference): Nothing {

        heap.setPendingException(codec.exception(exception).raw)

        throw HostRaisedWasmException.instance
    }

    override fun raisePending(): Nothing {

        check(heap.hasPending) { "no exception is pending" }

        throw HostRaisedWasmException.instance
    }

    override fun takePending(): ExceptionReference {

        check(heap.hasPending) { "no exception is pending" }

        return heldReferences.exception(heap.takePendingExceptionReference())
    }

    override fun hasPending(): Boolean {
        return heap.hasPending
    }

    private fun parameters(raw: Long): List<ValueType> {
        return withAccessorFailures {
            heap.tag(heap.exceptionTagAddress(raw)).type.functionType.params.types
        }
    }
}
