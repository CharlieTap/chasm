package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGcFieldInfo
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import java.lang.reflect.Field

class ManagedGc(
    private val bridge: ManagedReferenceBridge,
    private val store: Store,
) : HostGc {

    override val allocatedBytes: Long
        get() = store.allocation.allocatedBytes

    context(resources: HostResources)
    override fun collect() {
        Runtime.getRuntime().gc()
    }

    context(resources: HostResources)
    override fun collect(
        additionalRoots: LongArray,
        rootOffset: Int,
        rootCount: Int,
    ) {
        Runtime.getRuntime().gc()
    }

    override fun structType(reference: HostReference): HostGcType {
        return HostGcType(bridge.fromHost(reference)?.let(bridge::type) ?: -1)
    }

    override fun structFieldCount(type: HostGcType): Int {
        return store.heap.structFieldCount(type)
    }

    override fun structFieldInfo(
        type: HostGcType,
        fieldIndex: Int,
    ): HostGcFieldInfo {
        return store.heap.structFieldInfo(type, fieldIndex)
    }

    override fun readStructField(
        reference: HostReference,
        fieldIndex: Int,
    ): Long {

        val struct = checkNotNull(bridge.fromHost(reference))
        val type = HostGcType(bridge.type(struct))
        val field = fields(type)[fieldIndex]

        return when (field.type) {
            Int::class.javaPrimitiveType -> field.getInt(struct).toLong()
            Long::class.javaPrimitiveType -> field.getLong(struct)
            Float::class.javaPrimitiveType -> field.getFloat(struct).toRawBits().toLong()
            Double::class.javaPrimitiveType -> field.getDouble(struct).toRawBits()
            Byte::class.javaPrimitiveType -> field.getByte(struct).toLong() and BYTE_MASK
            Char::class.javaPrimitiveType -> field.getChar(struct).code.toLong()
            else -> bridge.toHost(field.get(struct), store.heap.runtimeStructFieldType(type, fieldIndex).isExtern())
        }
    }

    override fun writeStructField(
        reference: HostReference,
        fieldIndex: Int,
        value: Long,
    ) {

        val struct = checkNotNull(bridge.fromHost(reference))
        val field = fields(HostGcType(bridge.type(struct)))[fieldIndex]
        write(struct, field, value)
    }

    override fun arrayType(reference: HostReference): HostGcType {
        return HostGcType(bridge.fromHost(reference)?.let(bridge::type) ?: -1)
    }

    override fun arrayElementInfo(type: HostGcType): HostGcFieldInfo {
        return store.heap.arrayElementInfo(type)
    }

    override fun arrayLength(reference: HostReference): Int {
        return array(reference).length()
    }

    override fun readArrayElement(
        reference: HostReference,
        index: Int,
    ): Long {
        val array = array(reference)
        return read(array.storage(), index, extern(array))
    }

    override fun writeArrayElement(
        reference: HostReference,
        index: Int,
        value: Long,
    ) {
        write(array(reference).storage(), index, value)
    }

    override fun fillArray(
        reference: HostReference,
        offset: Int,
        length: Int,
        value: Long,
    ) {
        val storage = array(reference).storage()
        for (index in offset until offset + length) write(storage, index, value)
    }

    override fun copyArray(
        source: HostReference,
        sourceOffset: Int,
        destination: HostReference,
        destinationOffset: Int,
        length: Int,
    ) {
        System.arraycopy(array(source).storage(), sourceOffset, array(destination).storage(), destinationOffset, length)
    }

    override fun readArrayElements(
        reference: HostReference,
        sourceOffset: Int,
        destination: LongArray,
        destinationOffset: Int,
        length: Int,
    ): LongArray {

        val array = array(reference)
        val storage = array.storage()
        val extern = extern(array)
        for (index in 0 until length) destination[destinationOffset + index] = read(storage, sourceOffset + index, extern)

        return destination
    }

    override fun writeArrayElements(
        reference: HostReference,
        destinationOffset: Int,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ) {
        val storage = array(reference).storage()
        for (index in 0 until length) write(storage, destinationOffset + index, source[sourceOffset + index])
    }

    override fun runtimeType(
        module: HostModuleInstance,
        typeIndex: Int,
    ): HostGcType {
        return store.heap.runtimeType(module, typeIndex)
    }

    override fun isSubtype(
        actual: HostGcType,
        expected: HostGcType,
    ): Boolean {
        return store.heap.isSubtype(actual, expected)
    }

    context(resources: HostResources)
    override fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        fieldOffset: Int,
    ): HostReference {

        val struct = bridge.allocator.newStruct(bridge.layouts.struct(type))
        val layout = fields(type)
        layout.forEachIndexed { index, field -> write(struct, field, fields[fieldOffset + index]) }

        return bridge.references.rootObject(struct, bridge.toHostResult(struct, false))
    }

    context(resources: HostResources)
    override fun allocateArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): HostReference {

        val storage = storage(type, length)
        for (index in 0 until length) write(storage, index, initialValue)

        return wrap(type, storage)
    }

    context(resources: HostResources)
    override fun allocateArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): HostReference {

        val storage = storage(type, elementCount)
        for (index in 0 until elementCount) write(storage, index, elements[elementOffset + index])

        return wrap(type, storage)
    }

    private fun array(reference: HostReference): ManagedArray {
        return bridge.fromHost(reference) as ManagedArray
    }

    private fun extern(array: ManagedArray): Boolean {
        return store.heap.runtimeArrayFieldType(HostGcType(bridge.type(array))).isExtern()
    }

    private fun fields(type: HostGcType): Array<Field> {
        return bridge.layouts.struct(type).fields
    }

    private fun storage(
        type: HostGcType,
        length: Int,
    ): Any {
        return bridge.allocator.newStorage(bridge.layouts.array(type), length)
    }

    private fun wrap(
        type: HostGcType,
        storage: Any,
    ): HostReference {
        val array = bridge.allocator.newArray(bridge.layouts.array(type), storage)
        return bridge.references.rootObject(array, bridge.toHostResult(array, false))
    }

    private fun write(
        struct: Any,
        field: Field,
        value: Long,
    ) {
        when (field.type) {
            Int::class.javaPrimitiveType -> field.setInt(struct, value.toInt())
            Long::class.javaPrimitiveType -> field.setLong(struct, value)
            Float::class.javaPrimitiveType -> field.setFloat(struct, Float.fromBits(value.toInt()))
            Double::class.javaPrimitiveType -> field.setDouble(struct, Double.fromBits(value))
            Byte::class.javaPrimitiveType -> field.setByte(struct, value.toByte())
            Char::class.javaPrimitiveType -> field.setChar(struct, value.toInt().toChar())
            else -> field.set(struct, bridge.fromHost(value))
        }
    }

    private fun read(
        storage: Any,
        index: Int,
        extern: Boolean,
    ): Long {
        return when (storage) {
            is IntArray -> storage[index].toLong()
            is LongArray -> storage[index]
            is FloatArray -> storage[index].toRawBits().toLong()
            is DoubleArray -> storage[index].toRawBits()
            is ByteArray -> storage[index].toLong() and BYTE_MASK
            is CharArray -> storage[index].code.toLong()
            else -> bridge.toHost((storage as Array<*>)[index], extern)
        }
    }

    private fun write(
        storage: Any,
        index: Int,
        value: Long,
    ) {
        when (storage) {
            is IntArray -> storage[index] = value.toInt()
            is LongArray -> storage[index] = value
            is FloatArray -> storage[index] = Float.fromBits(value.toInt())
            is DoubleArray -> storage[index] = Double.fromBits(value)
            is ByteArray -> storage[index] = value.toByte()
            is CharArray -> storage[index] = value.toInt().toChar()
            else -> {
                @Suppress("UNCHECKED_CAST")
                (storage as Array<Any?>)[index] = bridge.fromHost(value)
            }
        }
    }

    private fun FieldType.isExtern(): Boolean {
        val heap = ((storageType as? StorageType.Value)?.type as? ValueType.Reference)?.referenceType?.heapType
        return heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern
    }

    private companion object {
        const val BYTE_MASK = 0xFFL
    }
}
