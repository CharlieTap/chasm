package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

interface ShapeLayouts {

    fun struct(value: Any): StructLayout?

    fun array(value: Any): ArrayLayout?

    fun struct(type: HostGcType): StructLayout

    fun array(type: HostGcType): ArrayLayout
}

class StructLayout(
    val type: HostGcType,
    val constructor: Constructor<*>,
    val bytes: Int,
    val fields: Array<Field>,
    val mutable: BooleanArray,
    val checks: Array<ReferenceCheck?>,
)

class ArrayLayout(
    val type: HostGcType,
    val constructor: Constructor<*>,
    val storage: Class<*>,
    val shift: Int,
    val mutable: Boolean,
    val check: ReferenceCheck?,
)

class ReflectedShapeLayouts(
    private val registry: ShapeRegistry,
    private val heap: WasmHeap,
    private val checks: ReferenceChecks,
) : ShapeLayouts {

    private val structs = ConcurrentHashMap<Class<*>, StructLayout>()
    private val arrays = ConcurrentHashMap<Class<*>, ArrayLayout>()

    override fun struct(value: Any): StructLayout? {

        structs[value.javaClass]?.let { layout -> return layout }

        val type = if (value is ManagedStruct) registry.type(value) else -1
        if (type < 0) {
            return null
        }

        val layout = struct(HostGcType(type))
        structs.putIfAbsent(value.javaClass, layout)

        return layout
    }

    override fun array(value: Any): ArrayLayout? {

        arrays[value.javaClass]?.let { layout -> return layout }

        val type = if (value is ManagedArray) registry.type(value) else -1
        if (type < 0) {
            return null
        }

        val layout = array(HostGcType(type))
        arrays.putIfAbsent(value.javaClass, layout)

        return layout
    }

    override fun struct(type: HostGcType): StructLayout {

        val shape = shape(type)
        structs[shape]?.let { layout -> return layout }

        require(ManagedStruct::class.java.isAssignableFrom(shape)) { "type ${type.id} is not a struct type" }

        val layout = structLayout(type, shape)

        return structs.putIfAbsent(shape, layout) ?: layout
    }

    override fun array(type: HostGcType): ArrayLayout {

        val shape = shape(type)
        arrays[shape]?.let { layout -> return layout }

        require(ManagedArray::class.java.isAssignableFrom(shape)) { "type ${type.id} is not an array type" }

        val layout = arrayLayout(type, shape)

        return arrays.putIfAbsent(shape, layout) ?: layout
    }

    private fun shape(type: HostGcType): Class<*> {
        return requireNotNull(registry.shape(type)) { "type ${type.id} is not a GC type of this store" }
    }

    private fun structLayout(
        type: HostGcType,
        shape: Class<*>,
    ): StructLayout {

        val owner = registry.definedType(type)
        val count = heap.structFieldCount(type)
        val fieldTypes = Array(count) { index -> heap.runtimeStructFieldType(type, index) }
        val fields = Array(count) { index -> declaredField(shape, "f$index") }
        val bytes = OBJECT_HEADER_BYTES + fields.sumOf { field -> 1 shl fieldShift(field.type) }

        return StructLayout(
            type = type,
            constructor = shape.getDeclaredConstructor(),
            bytes = (bytes + ALIGNMENT - 1) and (ALIGNMENT - 1).inv(),
            fields = fields,
            mutable = BooleanArray(count) { index -> fieldTypes[index].mutability == Mutability.Var },
            checks = Array(count) { index -> check(fieldTypes[index], owner) },
        )
    }

    private fun arrayLayout(
        type: HostGcType,
        shape: Class<*>,
    ): ArrayLayout {

        val elementType = heap.runtimeArrayFieldType(type)
        val storage = declaredField(shape, "data").type

        return ArrayLayout(
            type = type,
            constructor = shape.getDeclaredConstructor(storage),
            storage = storage,
            shift = fieldShift(storage.componentType),
            mutable = elementType.mutability == Mutability.Var,
            check = check(elementType, registry.definedType(type)),
        )
    }

    private fun check(
        fieldType: FieldType,
        owner: DefinedType?,
    ): ReferenceCheck? {

        val value = (fieldType.storageType as? StorageType.Value)?.type
        val reference: ReferenceType = (value as? ValueType.Reference)?.referenceType ?: return null

        return checks.check(reference, owner)
    }

    private fun declaredField(
        shape: Class<*>,
        name: String,
    ): Field {

        val field = generateSequence(shape) { owner -> owner.superclass }
            .firstNotNullOfOrNull { owner -> owner.declaredFields.firstOrNull { candidate -> candidate.name == name } }
            ?: throw NoSuchFieldException(name)

        field.isAccessible = true

        return field
    }

    private fun fieldShift(type: Class<*>): Int {
        return when (type) {
            Byte::class.javaPrimitiveType -> 0
            Char::class.javaPrimitiveType -> 1
            Long::class.javaPrimitiveType, Double::class.javaPrimitiveType -> 3
            else -> 2
        }
    }

    private companion object {
        const val OBJECT_HEADER_BYTES = 8
        const val ALIGNMENT = 8
    }
}
