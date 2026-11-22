package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.host.withScope
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.StorageType

internal interface ArrayAccess {

    fun arrayType(array: ArrayReference): HostGcType

    fun arrayLength(array: ArrayReference): Int

    fun readArrayElement(
        array: ArrayReference,
        index: Int,
    ): Long

    fun readArrayReference(
        array: ArrayReference,
        index: Int,
    ): Any?

    fun writeArrayElement(
        array: ArrayReference,
        index: Int,
        value: Long,
    )

    fun writeArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    )

    fun readArrayElements(
        array: ArrayReference,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    )

    fun writeArrayElements(
        array: ArrayReference,
        offset: Int,
        source: LongArray,
        sourceOffset: Int,
        count: Int,
    )

    fun fillArray(
        array: ArrayReference,
        offset: Int,
        count: Int,
        value: Long,
    )

    fun copyArray(
        source: ArrayReference,
        sourceOffset: Int,
        destination: ArrayReference,
        destinationOffset: Int,
        count: Int,
    )

    fun allocateNumericArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference

    fun allocateReferenceArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference

    fun allocateNumericArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference

    fun allocateReferenceArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference
}

internal class InterpreterArrays(
    private val codec: ReferenceCodec,
    private val heldReferences: HeldReferences,
    private val allocator: GcAllocator,
    private val heap: WasmHeap,
) : ArrayAccess {

    override fun arrayType(array: ArrayReference): HostGcType {
        return HostGcType(codec.array(array).runtimeType)
    }

    override fun arrayLength(array: ArrayReference): Int {

        val held = codec.array(array)

        return withAccessorFailures {
            heap.arrayLengthTrusted(held.raw)
        }
    }

    override fun readArrayElement(
        array: ArrayReference,
        index: Int,
    ): Long {

        val held = codec.array(array)
        val field = numeric(held, writable = false)

        return withAccessorFailures {
            checkIndex(index, heap.arrayLengthTrusted(held.raw))
            packedValue(field, heap.getArrayElementTrusted(held.raw, index))
        }
    }

    override fun readArrayReference(
        array: ArrayReference,
        index: Int,
    ): Any? {

        val held = codec.array(array)
        requireNotNull(referenceType(elementField(held))) { "the array holds numbers" }

        return withAccessorFailures {
            checkIndex(index, heap.arrayLengthTrusted(held.raw))
            codec.decode(heap.getArrayElementTrusted(held.raw, index))
        }
    }

    override fun writeArrayElement(
        array: ArrayReference,
        index: Int,
        value: Long,
    ) {

        val held = codec.array(array)
        numeric(held, writable = true)

        withAccessorFailures {
            checkIndex(index, heap.arrayLengthTrusted(held.raw))
            heap.setArrayElementTrusted(held.raw, index, value)
        }
    }

    override fun writeArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    ) {

        val held = codec.array(array)
        val field = elementField(held)
        requireNotNull(referenceType(field)) { "the array holds numbers" }
        requireWritable(field, writable = true)
        val reference = codec.arrayElementType(HostGcType(held.runtimeType))

        withAccessorFailures {
            checkIndex(index, heap.arrayLengthTrusted(held.raw))
            heap.withScope {
                heap.rootScoped(held.raw)
                heap.setArrayElementTrusted(held.raw, index, codec.encode(reference, value))
            }
        }
    }

    override fun readArrayElements(
        array: ArrayReference,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    ) {

        val held = codec.array(array)
        val field = numeric(held, writable = false)

        withAccessorFailures {
            checkRange(offset, count, heap.arrayLengthTrusted(held.raw))
            checkRange(destinationOffset, count, destination.size)
            heap.readArrayElements(held.raw, offset, destination, destinationOffset, count)
            if (field.storageType is StorageType.Packed) {
                for (index in destinationOffset until destinationOffset + count) {
                    destination[index] = packedValue(field, destination[index])
                }
            }
        }
    }

    override fun writeArrayElements(
        array: ArrayReference,
        offset: Int,
        source: LongArray,
        sourceOffset: Int,
        count: Int,
    ) {

        val held = codec.array(array)
        numeric(held, writable = true)

        withAccessorFailures {
            checkRange(offset, count, heap.arrayLengthTrusted(held.raw))
            checkRange(sourceOffset, count, source.size)
            heap.writeArrayElements(held.raw, offset, source, sourceOffset, count)
        }
    }

    override fun fillArray(
        array: ArrayReference,
        offset: Int,
        count: Int,
        value: Long,
    ) {

        val held = codec.array(array)
        numeric(held, writable = true)

        withAccessorFailures {
            checkRange(offset, count, heap.arrayLengthTrusted(held.raw))
            heap.fillArrayChecked(held.raw, offset, count, value)
        }
    }

    override fun copyArray(
        source: ArrayReference,
        sourceOffset: Int,
        destination: ArrayReference,
        destinationOffset: Int,
        count: Int,
    ) {

        val sourceHeld = codec.array(source)
        val destinationHeld = codec.array(destination)
        val sourceField = elementField(sourceHeld)
        val destinationField = elementField(destinationHeld)
        require(destinationField.mutability == Mutability.Var) { "the destination array is immutable" }
        require(sourceField.storageType == destinationField.storageType) { "the arrays hold different element types" }

        withAccessorFailures {
            checkRange(sourceOffset, count, heap.arrayLengthTrusted(sourceHeld.raw))
            checkRange(destinationOffset, count, heap.arrayLengthTrusted(destinationHeld.raw))
            heap.copyArrayChecked(sourceHeld.raw, sourceOffset, destinationHeld.raw, destinationOffset, count)
        }
    }

    override fun allocateNumericArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference {

        requireNumericArrayType(type)
        require(length >= 0) { "negative length $length" }

        return withAccessorFailures {
            heldReferences.array(allocator.filledArray(RTT(type.id), length, initialValue), type.id)
        }
    }

    override fun allocateReferenceArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference {

        val reference = referenceElementType(type)
        require(length >= 0) { "negative length $length" }

        return withAccessorFailures {
            heap.withScope {
                val initial = codec.encode(reference, initialReference)
                heldReferences.array(allocator.filledArray(RTT(type.id), length, initial), type.id)
            }
        }
    }

    override fun allocateNumericArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {

        requireNumericArrayType(type)
        checkRange(elementOffset, elementCount, elements.size)

        return withAccessorFailures {
            heldReferences.array(allocator.array(RTT(type.id), elements, elementOffset, elementCount), type.id)
        }
    }

    override fun allocateReferenceArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {

        val reference = referenceElementType(type)
        checkRange(elementOffset, elementCount, elements.size)

        return withAccessorFailures {
            heap.withScope {
                val words = LongArray(elementCount) { index -> codec.encode(reference, elements[elementOffset + index]) }
                heldReferences.array(allocator.array(RTT(type.id), words, 0, elementCount), type.id)
            }
        }
    }

    private fun requireNumericArrayType(type: HostGcType) {
        require(heap.isArrayType(type)) { "$type is not an array type" }
        require(referenceType(heap.runtimeArrayFieldType(type)) == null) { "the array type holds references" }
    }

    private fun referenceElementType(type: HostGcType): ResolvedReferenceType {

        require(heap.isArrayType(type)) { "$type is not an array type" }

        return codec.arrayElementType(type)
    }

    private fun elementField(held: HeldArray): FieldType {
        return heap.runtimeArrayFieldType(HostGcType(held.runtimeType))
    }

    private fun numeric(
        held: HeldArray,
        writable: Boolean,
    ): FieldType {

        val field = elementField(held)
        require(referenceType(field) == null) { "the array holds references" }
        requireWritable(field, writable)

        return field
    }

    private fun requireWritable(
        field: FieldType,
        writable: Boolean,
    ) {
        require(!writable || field.mutability == Mutability.Var) { "the array is immutable" }
    }
}
