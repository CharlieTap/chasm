package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.HostGcType
import java.util.Arrays

@InternalChasmApi
class ArrayOperations(
    private val layouts: ShapeLayouts,
    private val allocator: ManagedAllocator,
) {

    fun arrayType(array: Any?): HostGcType {
        return layout(array).type
    }

    fun arrayLength(array: Any?): Int {

        layout(array)

        return (array as ManagedArray).length()
    }

    fun readArrayElement(
        array: Any?,
        index: Int,
    ): Long {

        val managed = numeric(array, writable = false)

        checkIndex(index, managed.length())

        return readElement(managed.storage(), index)
    }

    fun readArrayReference(
        array: Any?,
        index: Int,
    ): Any? {

        val storage = referenceStorage(array)

        checkIndex(index, storage.size)

        return storage[index]
    }

    fun writeArrayElement(
        array: Any?,
        index: Int,
        value: Long,
    ) {

        val managed = numeric(array, writable = true)

        checkIndex(index, managed.length())

        writeElements(managed.storage(), index, 1, value)
    }

    fun writeArrayReference(
        array: Any?,
        index: Int,
        value: Any?,
    ) {

        val layout = layout(array)
        val check = requireNotNull(layout.check) { "the array holds numbers" }
        val storage = referenceStorage(array)

        require(layout.mutable) { "the array is immutable" }
        checkIndex(index, storage.size)
        require(check.accepts(value)) { "the value does not match the element type" }

        storage[index] = value
    }

    fun unsafeWriteArrayReference(
        array: Any?,
        index: Int,
        value: Any?,
    ) {
        referenceStorage(array)[index] = value
    }

    fun readArrayElements(
        array: Any?,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    ) {

        val managed = numeric(array, writable = false)

        checkRange(offset, count, managed.length())
        checkRange(destinationOffset, count, destination.size)

        readElements(managed.storage(), offset, destination, destinationOffset, count)
    }

    fun writeArrayElements(
        array: Any?,
        offset: Int,
        source: LongArray,
        sourceOffset: Int,
        count: Int,
    ) {

        val managed = numeric(array, writable = true)

        checkRange(offset, count, managed.length())
        checkRange(sourceOffset, count, source.size)

        copyElements(source, sourceOffset, managed.storage(), offset, count)
    }

    fun fillArray(
        array: Any?,
        offset: Int,
        count: Int,
        value: Long,
    ) {

        val managed = numeric(array, writable = true)

        checkRange(offset, count, managed.length())

        writeElements(managed.storage(), offset, count, value)
    }

    fun copyArray(
        source: Any?,
        sourceOffset: Int,
        destination: Any?,
        destinationOffset: Int,
        count: Int,
    ) {

        val sourceLayout = layout(source)
        val destinationLayout = layout(destination)
        val sourceStorage = (source as ManagedArray).storage()
        val destinationStorage = (destination as ManagedArray).storage()

        require(destinationLayout.mutable) { "the destination array is immutable" }
        require(compatible(sourceLayout, destinationLayout)) { "the arrays hold different element types" }
        checkRange(sourceOffset, count, source.length())
        checkRange(destinationOffset, count, destination.length())

        val sourceCheck = sourceLayout.check
        val destinationCheck = destinationLayout.check
        if (sourceCheck != null && destinationCheck != null && !destinationCheck.includes(sourceCheck)) {
            val elements = sourceStorage as Array<*>
            for (index in sourceOffset until sourceOffset + count) {
                require(destinationCheck.accepts(elements[index])) { "element $index does not match the destination element type" }
            }
        }

        System.arraycopy(sourceStorage, sourceOffset, destinationStorage, destinationOffset, count)
    }

    fun <T : Any> borrow(
        array: Any?,
        storage: Class<T>,
    ): T {

        layout(array)

        val backing = (array as ManagedArray).storage()

        require(storage.isInstance(backing)) { "the array is not backed by ${storage.simpleName}" }

        return storage.cast(backing)
    }

    fun allocateArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ManagedArray {

        val layout = numericLayout(type)
        val storage = allocator.newStorage(layout, length)

        writeElements(storage, 0, length, initialValue)

        return allocator.newArray(layout, storage)
    }

    fun allocateArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ManagedArray {

        val layout = referenceLayout(type)

        require(checkNotNull(layout.check).accepts(initialReference)) { "the value does not match the element type" }

        val storage = allocator.newStorage(layout, length)
        Arrays.fill(storage as Array<*>, initialReference)

        return allocator.newArray(layout, storage)
    }

    fun allocateArray(
        type: HostGcType,
        elements: LongArray,
        offset: Int,
        count: Int,
    ): ManagedArray {

        val layout = numericLayout(type)

        checkRange(offset, count, elements.size)

        val storage = allocator.newStorage(layout, count)
        copyElements(elements, offset, storage, 0, count)

        return allocator.newArray(layout, storage)
    }

    fun allocateArray(
        type: HostGcType,
        elements: Array<Any?>,
        offset: Int,
        count: Int,
    ): ManagedArray {

        val layout = referenceLayout(type)
        val check = checkNotNull(layout.check)

        checkRange(offset, count, elements.size)
        for (index in offset until offset + count) {
            require(check.accepts(elements[index])) { "element $index does not match the element type" }
        }

        val storage = allocator.newStorage(layout, count)
        System.arraycopy(elements, offset, storage, 0, count)

        return allocator.newArray(layout, storage)
    }

    fun unsafeAllocateArray(
        type: HostGcType,
        storage: Any,
    ): ManagedArray {

        val layout = layouts.array(type)
        val check = layout.check

        require(layout.storageLayout.element.storage == storage.javaClass) { "${storage.javaClass.simpleName} cannot back an array of this type" }
        if (check != null) {
            require(check.admitsUncheckedWrites) { "only arrays of nullable anyref or externref adopt host storage" }
            (storage as Array<*>).forEachIndexed { index, element ->
                require(check.accepts(element)) { "element $index does not match the element type" }
            }
        }

        allocator.adoptStorage(layout, storage)

        return allocator.newArray(layout, storage)
    }

    private fun layout(array: Any?): ArrayLayout {
        return requireNotNull(array?.let(layouts::array)) { "not an array of this store" }
    }

    private fun numeric(
        array: Any?,
        writable: Boolean,
    ): ManagedArray {

        val layout = layout(array)

        require(layout.check == null) { "the array holds references" }
        require(!writable || layout.mutable) { "the array is immutable" }

        return array as ManagedArray
    }

    private fun referenceStorage(array: Any?): Array<Any?> {

        requireNotNull(layout(array).check) { "the array holds numbers" }

        @Suppress("UNCHECKED_CAST")
        return (array as ManagedArray).storage() as Array<Any?>
    }

    private fun compatible(
        source: ArrayLayout,
        destination: ArrayLayout,
    ): Boolean {

        val sourceCheck = source.check
        val destinationCheck = destination.check

        return if (destinationCheck == null) {
            sourceCheck == null && source.storageLayout.element == destination.storageLayout.element
        } else {
            sourceCheck != null && destinationCheck.includesHeap(sourceCheck)
        }
    }

    private fun numericLayout(type: HostGcType): ArrayLayout {

        val layout = layouts.array(type)

        require(layout.check == null) { "the array type holds references" }

        return layout
    }

    private fun referenceLayout(type: HostGcType): ArrayLayout {

        val layout = layouts.array(type)

        requireNotNull(layout.check) { "the array type holds numbers" }

        return layout
    }

    private fun readElement(
        storage: Any,
        index: Int,
    ): Long {
        return when (storage) {
            is IntArray -> storage[index].toLong()
            is LongArray -> storage[index]
            is FloatArray -> storage[index].toRawBits().toLong()
            is DoubleArray -> storage[index].toRawBits()
            is ByteArray -> storage[index].toLong() and BYTE_MASK
            else -> (storage as CharArray)[index].code.toLong()
        }
    }

    private fun readElements(
        storage: Any,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    ) {
        when (storage) {
            is LongArray -> System.arraycopy(storage, offset, destination, destinationOffset, count)
            is IntArray -> {
                for (index in 0 until count) {
                    destination[destinationOffset + index] = storage[offset + index].toLong()
                }
            }
            is FloatArray -> {
                for (index in 0 until count) {
                    destination[destinationOffset + index] = storage[offset + index].toRawBits().toLong()
                }
            }
            is DoubleArray -> {
                for (index in 0 until count) {
                    destination[destinationOffset + index] = storage[offset + index].toRawBits()
                }
            }
            is ByteArray -> {
                for (index in 0 until count) {
                    destination[destinationOffset + index] = storage[offset + index].toLong() and BYTE_MASK
                }
            }
            else -> {
                val chars = storage as CharArray
                for (index in 0 until count) {
                    destination[destinationOffset + index] = chars[offset + index].code.toLong()
                }
            }
        }
    }

    private fun copyElements(
        source: LongArray,
        sourceOffset: Int,
        storage: Any,
        offset: Int,
        count: Int,
    ) {
        when (storage) {
            is LongArray -> System.arraycopy(source, sourceOffset, storage, offset, count)
            is IntArray -> {
                for (index in 0 until count) {
                    storage[offset + index] = source[sourceOffset + index].toInt()
                }
            }
            is FloatArray -> {
                for (index in 0 until count) {
                    storage[offset + index] = Float.fromBits(source[sourceOffset + index].toInt())
                }
            }
            is DoubleArray -> {
                for (index in 0 until count) {
                    storage[offset + index] = Double.fromBits(source[sourceOffset + index])
                }
            }
            is ByteArray -> {
                for (index in 0 until count) {
                    storage[offset + index] = source[sourceOffset + index].toByte()
                }
            }
            else -> {
                val chars = storage as CharArray
                for (index in 0 until count) {
                    chars[offset + index] = source[sourceOffset + index].toInt().toChar()
                }
            }
        }
    }

    private fun writeElements(
        storage: Any,
        offset: Int,
        count: Int,
        value: Long,
    ) {
        when (storage) {
            is IntArray -> storage.fill(value.toInt(), offset, offset + count)
            is LongArray -> storage.fill(value, offset, offset + count)
            is FloatArray -> storage.fill(Float.fromBits(value.toInt()), offset, offset + count)
            is DoubleArray -> storage.fill(Double.fromBits(value), offset, offset + count)
            is ByteArray -> storage.fill(value.toByte(), offset, offset + count)
            else -> (storage as CharArray).fill(value.toInt().toChar(), offset, offset + count)
        }
    }

    private companion object {
        const val BYTE_MASK = 0xFFL
    }
}
