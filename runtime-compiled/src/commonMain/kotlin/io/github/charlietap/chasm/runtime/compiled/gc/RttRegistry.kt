package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.ConcurrentHashMap

internal class RttRegistry(
    private val managedStore: ManagedStore,
) {

    private val groups = HashMap<String, MutableList<List<Rtt>>>()
    private val rtts = ConcurrentHashMap<Int, Rtt>()

    @Synchronized
    fun register(
        types: List<DefinedType>,
        keys: List<String>,
        runtimeTypes: RuntimeTypeMap,
        structLayout: (Int) -> StructStorageLayout?,
    ): List<Rtt> {

        val registered = ArrayList<Rtt>(types.size)
        var groupStart = 0

        while (groupStart < types.size) {

            val size = types[groupStart].recursiveType.subTypes.size
            val candidates = groups.getOrPut(keys[groupStart]) { ArrayList() }
            val group = candidates.firstOrNull { candidate -> sameTypes(candidate, runtimeTypes, groupStart, size) }
                ?: createGroup(types, keys, runtimeTypes, groupStart, size, structLayout).also(candidates::add)

            registered.addAll(group)
            groupStart += size
        }

        return registered
    }

    fun rtt(type: Int): Rtt {
        return requireNotNull(rtts[type]) { "type $type is not a type of this store" }
    }

    @Synchronized
    fun clear() {
        groups.clear()
        rtts.clear()
    }

    private fun sameTypes(
        group: List<Rtt>,
        runtimeTypes: RuntimeTypeMap,
        groupStart: Int,
        size: Int,
    ): Boolean {
        return group.size == size && group.indices.all { offset -> group[offset].id == runtimeTypes[groupStart + offset].value }
    }

    private fun createGroup(
        types: List<DefinedType>,
        keys: List<String>,
        runtimeTypes: RuntimeTypeMap,
        groupStart: Int,
        size: Int,
        structLayout: (Int) -> StructStorageLayout?,
    ): List<Rtt> {
        return List(size) { offset ->
            val index = groupStart + offset
            rtts.getOrPut(runtimeTypes[index].value) { createRtt(types, keys, runtimeTypes, index, groupStart, structLayout) }
        }
    }

    private fun createRtt(
        types: List<DefinedType>,
        keys: List<String>,
        runtimeTypes: RuntimeTypeMap,
        index: Int,
        groupStart: Int,
        structLayout: (Int) -> StructStorageLayout?,
    ): Rtt {

        val type = types[index]
        val subType = type.recursiveType.subTypes[type.recursiveTypeIndex]
        val supertype = subType.superTypes.firstOrNull()?.let { heapType ->
            rtts.getValue(runtimeTypes[typeIndex(heapType, type, groupStart)].value)
        }
        val layout = when (val composite = subType.compositeType) {
            is CompositeType.Struct -> structLayout(index)
            is CompositeType.Array -> arrayLayout(composite.arrayType.fieldType)
            is CompositeType.Function -> null
        }

        return Rtt(runtimeTypes[index].value, keys[index].intern(), managedStore.engine, supertype, layout)
    }

    private fun typeIndex(
        heapType: HeapType,
        owner: DefinedType,
        groupStart: Int,
    ): Int {
        return when (heapType) {
            is ConcreteHeapType.RecursiveTypeIndex -> groupStart + heapType.index
            is ConcreteHeapType.TypeIndex -> heapType.index
            is ConcreteHeapType.Defined -> if (heapType.definedType.recursiveType === owner.recursiveType) {
                groupStart + heapType.definedType.recursiveTypeIndex
            } else {
                heapType.definedType.typeIndex
            }
            is AbstractHeapType -> error("defined type has an abstract supertype")
        }
    }

    private fun arrayLayout(fieldType: FieldType): ArrayStorageLayout? {
        return arrayElement(fieldType)?.let { element -> ArrayStorageLayout(fieldType, element) }
    }

    private fun arrayElement(fieldType: FieldType): ArrayElement? {
        return when (val storage = fieldType.storageType) {
            is StorageType.Packed -> when (storage.type) {
                PackedType.I8 -> ArrayElement.I8
                PackedType.I16 -> ArrayElement.I16
            }
            is StorageType.Value -> when (val value = storage.type) {
                is ValueType.Number -> when (value.numberType) {
                    NumberType.I32 -> ArrayElement.I32
                    NumberType.I64 -> ArrayElement.I64
                    NumberType.F32 -> ArrayElement.F32
                    NumberType.F64 -> ArrayElement.F64
                }
                is ValueType.Reference, is ValueType.Bottom -> ArrayElement.REFERENCE
                is ValueType.Vector -> null
            }
        }
    }
}
