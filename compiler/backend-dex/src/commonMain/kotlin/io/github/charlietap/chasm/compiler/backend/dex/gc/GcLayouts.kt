package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.abi.EQ_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType

internal class GcField(
    val owner: String,
    val name: String,
    val descriptor: String,
)

internal class StructShape(
    val className: String,
    val parent: StructShape?,
    val final: Boolean,
    val fields: List<GcField>,
)

internal class ArrayShape(
    val className: String,
    val parent: ArrayShape?,
    val final: Boolean,
    val data: GcField,
)

internal class GcLayouts(
    private val types: List<DefinedType>,
) {
    private val keys = TypeKeys(types)
    private val structsByKey = LinkedHashMap<String, StructShape>()
    private val arraysByKey = LinkedHashMap<String, ArrayShape>()
    private val structsByIndex = arrayOfNulls<StructShape>(types.size)
    private val arraysByIndex = arrayOfNulls<ArrayShape>(types.size)
    private val descriptorsByIndex = arrayOfNulls<String>(types.size)
    private val unsupportedByIndex = arrayOfNulls<String>(types.size)
    private val layableByKey = HashMap<String, Boolean>()

    val structs: Collection<StructShape> get() = structsByKey.values
    val arrays: Collection<ArrayShape> get() = arraysByKey.values

    init {
        types.forEachIndexed { index, type ->
            try {
                when (type.subType().compositeType) {
                    is CompositeType.Struct -> structsByIndex[index] = structShape(type)
                    is CompositeType.Array -> arraysByIndex[index] = arrayShape(type)
                    is CompositeType.Function -> Unit
                }
                descriptorsByIndex[index] = definedDescriptor(type)
            } catch (unsupported: Unsupported) {
                unsupportedByIndex[index] = unsupported.message.orEmpty()
            }
        }
    }

    fun struct(index: Int): StructShape {
        requireSupported(index)
        return structsByIndex[index] ?: error("type $index is not a struct type")
    }

    fun array(index: Int): ArrayShape {
        requireSupported(index)
        return arraysByIndex[index] ?: error("type $index is not an array type")
    }

    val typeCount: Int
        get() = types.size

    fun className(index: Int): String? {

        if (unsupportedByIndex[index] != null) {
            return null
        }

        return when (types[index].subType().compositeType) {
            is CompositeType.Struct -> struct(index).className
            is CompositeType.Array -> array(index).className
            is CompositeType.Function -> null
        }
    }

    fun typeDescriptor(index: Int): String {
        requireSupported(index)
        return checkNotNull(descriptorsByIndex[index])
    }

    private fun requireSupported(index: Int) {
        val reason = unsupportedByIndex[index]
        if (reason != null) {
            throw Unsupported(reason)
        }
    }

    fun isFunction(index: Int): Boolean {
        return types[index].subType().compositeType is CompositeType.Function
    }

    fun descriptor(type: ValueType): String {
        return valueDescriptor(type, null)
    }

    fun descriptor(type: HeapType): String {
        return heapDescriptor(type, null)
    }

    private fun valueDescriptor(
        type: ValueType,
        owner: DefinedType?,
    ): String {
        return when (type) {
            is ValueType.Reference -> heapDescriptor(type.referenceType.heapType, owner)
            is ValueType.Number -> numberDescriptor(type.numberType)
            is ValueType.Vector -> throw Unsupported("v128 values cannot be managed objects")
            is ValueType.Bottom -> OBJECT_TYPE
        }
    }

    private fun heapDescriptor(
        type: HeapType,
        owner: DefinedType?,
    ): String {
        return when (type) {
            AbstractHeapType.Eq -> EQ_REFERENCE
            AbstractHeapType.Struct -> MANAGED_STRUCT
            AbstractHeapType.Array -> MANAGED_ARRAY
            AbstractHeapType.I31 -> I31_REFERENCE
            AbstractHeapType.Func, AbstractHeapType.NoFunc -> MANAGED_FUNCTION
            is AbstractHeapType -> OBJECT_TYPE
            else -> definedDescriptor(resolve(type, owner))
        }
    }

    private fun definedDescriptor(type: DefinedType): String {

        val key = keys.key(type)
        val layable = layable(type)

        return when (type.subType().compositeType) {
            is CompositeType.Struct -> if (layable) structClass(key) else MANAGED_STRUCT
            is CompositeType.Array -> if (layable) arrayClass(key) else MANAGED_ARRAY
            is CompositeType.Function -> MANAGED_FUNCTION
        }
    }

    private fun layable(type: DefinedType): Boolean {

        val key = keys.key(type)
        layableByKey[key]?.let { layable -> return layable }

        val ownStorageLayable = when (val composite = type.subType().compositeType) {
            is CompositeType.Struct -> composite.structType.fields.none { field -> isVector(field.storageType) }
            is CompositeType.Array -> !isVector(composite.arrayType.fieldType.storageType)
            is CompositeType.Function -> true
        }
        val layable = ownStorageLayable && supertype(type)?.let(::layable) != false
        layableByKey[key] = layable

        return layable
    }

    private fun isVector(storage: StorageType): Boolean {
        return storage is StorageType.Value && storage.type is ValueType.Vector
    }

    private fun structShape(type: DefinedType): StructShape {

        val key = keys.key(type)
        structsByKey[key]?.let { shape -> return shape }

        val subType = type.subType()
        val struct = subType.compositeType as CompositeType.Struct
        val parent = supertype(type)?.let(::structShape)
        val className = structClass(key)
        val fields = struct.structType.fields.mapIndexed { index, field ->
            parent?.fields?.getOrNull(index) ?: GcField(className, "f$index", storageDescriptor(field.storageType, type))
        }
        val shape = StructShape(className, parent, subType is SubType.Final, fields)
        structsByKey[key] = shape

        return shape
    }

    private fun arrayShape(type: DefinedType): ArrayShape {

        val key = keys.key(type)
        arraysByKey[key]?.let { shape -> return shape }

        val subType = type.subType()
        val array = subType.compositeType as CompositeType.Array
        val parent = supertype(type)?.let(::arrayShape)
        val className = arrayClass(key)
        val data = parent?.data ?: GcField(className, "data", "[" + storageDescriptor(array.arrayType.fieldType.storageType, type))
        val shape = ArrayShape(className, parent, subType is SubType.Final, data)
        arraysByKey[key] = shape

        return shape
    }

    private fun supertype(type: DefinedType): DefinedType? {

        val supertype = type.subType().superTypes.singleOrNull() ?: return null

        return resolve(supertype, type)
    }

    private fun storageDescriptor(
        storage: StorageType,
        owner: DefinedType,
    ): String {
        return when (storage) {
            is StorageType.Packed -> if (storage.type == PackedType.I8) "B" else "C"
            is StorageType.Value -> valueDescriptor(storage.type, owner)
        }
    }

    private fun resolve(
        type: HeapType,
        owner: DefinedType?,
    ): DefinedType {
        return when (type) {
            is ConcreteHeapType.TypeIndex -> types[type.index]
            is ConcreteHeapType.Defined -> type.definedType
            is ConcreteHeapType.RecursiveTypeIndex -> DefinedType(checkNotNull(owner).recursiveType, type.index, owner.typeIndex - owner.recursiveTypeIndex + type.index)
            is AbstractHeapType -> error("$type does not name a defined type")
        }
    }

    private fun structClass(key: String): String {
        return "Lchasm/gc/S$key;"
    }

    private fun arrayClass(key: String): String {
        return "Lchasm/gc/A$key;"
    }

    private fun numberDescriptor(type: NumberType): String {
        return when (type) {
            NumberType.I32 -> "I"
            NumberType.I64 -> "J"
            NumberType.F32 -> "F"
            NumberType.F64 -> "D"
        }
    }

    private fun DefinedType.subType(): SubType {
        return recursiveType.subTypes[recursiveTypeIndex]
    }
}
