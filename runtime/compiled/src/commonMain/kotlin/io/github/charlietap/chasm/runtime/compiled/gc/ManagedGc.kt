package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGcFieldInfo
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.runtime.execution.ARRAY_OVERHEAD
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import java.lang.reflect.Field
import java.util.IdentityHashMap

class ManagedGc(
    private val bridge: ManagedReferenceBridge,
    private val store: Store,
) : HostGc {

    private val structFields = IdentityHashMap<Class<*>, Array<Field>>()
    private val arrayStorage = IdentityHashMap<Class<*>, Class<*>>()

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
        val field = fields(struct.javaClass, type)[fieldIndex]

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
        val field = fields(struct.javaClass, HostGcType(bridge.type(struct)))[fieldIndex]
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

        val shape = bridge.shape(type.id)
        val layout = fields(shape, type)
        chargeAllocation(store, (OBJECT_HEADER_BYTES + layout.sumOf { field -> fieldBytes(field.type) } + 7) and 7.inv())
        val struct = shape.getDeclaredConstructor().newInstance()
        layout.forEachIndexed { index, field -> write(struct, field, fields[fieldOffset + index]) }

        return bridge.references.rootObject(struct, bridge.toHost(struct, false))
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

    private fun array(reference: HostReference): WasmArray {
        return bridge.fromHost(reference) as WasmArray
    }

    private fun extern(array: WasmArray): Boolean {
        return store.heap.runtimeArrayFieldType(HostGcType(bridge.type(array))).isExtern()
    }

    @Synchronized
    private fun fields(
        shape: Class<*>,
        type: HostGcType,
    ): Array<Field> {
        return structFields.getOrPut(shape) {
            Array(store.heap.structFieldCount(type)) { index -> declaredField(shape, "f$index") }
        }
    }

    @Synchronized
    private fun storage(
        type: HostGcType,
        length: Int,
    ): Any {
        val shape = bridge.shape(type.id)
        val storage = arrayStorage.getOrPut(shape) { declaredField(shape, "data").type }
        val component = checkNotNull(storage.componentType)
        val shift = fieldShift(component)
        checkArrayLength(length, shift)
        chargeAllocation(store, (length shl shift) + ARRAY_OVERHEAD)
        return java.lang.reflect.Array.newInstance(component, length)
    }

    private fun wrap(
        type: HostGcType,
        storage: Any,
    ): HostReference {
        val array = bridge.shape(type.id).getDeclaredConstructor(storage.javaClass).newInstance(storage)
        return bridge.references.rootObject(array, bridge.toHost(array, false))
    }

    private fun declaredField(
        shape: Class<*>,
        name: String,
    ): Field {

        var owner: Class<*>? = shape
        while (owner != null) {
            val field = owner.declaredFields.firstOrNull { candidate -> candidate.name == name }
            if (field != null) {
                field.isAccessible = true
                return field
            }
            owner = owner.superclass
        }

        throw NoSuchFieldException(name)
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

    private fun fieldBytes(
        type: Class<*>,
    ): Int {
        return 1 shl fieldShift(type)
    }

    private fun fieldShift(
        type: Class<*>,
    ): Int {
        return when (type) {
            Byte::class.javaPrimitiveType -> 0
            Char::class.javaPrimitiveType -> 1
            Long::class.javaPrimitiveType, Double::class.javaPrimitiveType -> 3
            else -> 2
        }
    }

    private fun FieldType.isExtern(): Boolean {
        val heap = ((storageType as? StorageType.Value)?.type as? ValueType.Reference)?.referenceType?.heapType
        return heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern
    }

    private companion object {
        const val BYTE_MASK = 0xFFL
        const val OBJECT_HEADER_BYTES = 8
    }
}
