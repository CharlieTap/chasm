package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.ConcurrentHashMap

class RttShapeLayouts(
    private val managedStore: ManagedStore,
    private val registry: RttRegistry,
) : ShapeLayouts {

    private val structs = ConcurrentHashMap<Rtt, StructLayout>()
    private val arrays = ConcurrentHashMap<Rtt, ArrayLayout>()

    override fun struct(value: Any): StructLayout? {

        if (value !is CompiledStruct || managedStore.types.type(value) < 0) {
            return null
        }

        return structLayout(value.rtt)
    }

    override fun array(value: Any): ArrayLayout? {

        if (value !is CompiledArray || managedStore.types.type(value) < 0) {
            return null
        }

        return arrayLayout(value.rtt)
    }

    override fun struct(type: HostGcType): StructLayout {
        return structLayout(registry.rtt(type.id))
    }

    override fun array(type: HostGcType): ArrayLayout {
        return arrayLayout(registry.rtt(type.id))
    }

    private fun structLayout(rtt: Rtt): StructLayout {

        structs[rtt]?.let { layout -> return layout }

        val storage = rtt.layout
        require(storage is StructStorageLayout) { "type ${rtt.id} is not a struct type" }

        val type = HostGcType(rtt.id)
        val owner = managedStore.definedType(type)
        val fields = storage.fields
        val layout = StructLayout(
            type = type,
            accessor = RttStructAccessor(rtt, storage),
            bytes = storage.bytes,
            mutable = BooleanArray(fields.size) { index -> fields[index].type.mutability == Mutability.Var },
            checks = Array(fields.size) { index -> referenceCheck(fields[index].type, owner) },
        )

        return structs.putIfAbsent(rtt, layout) ?: layout
    }

    private fun arrayLayout(rtt: Rtt): ArrayLayout {

        arrays[rtt]?.let { layout -> return layout }

        val storage = rtt.layout
        require(storage is ArrayStorageLayout) { "type ${rtt.id} is not an array type" }

        val type = HostGcType(rtt.id)
        val layout = ArrayLayout(
            type = type,
            createAccessor = { RttArrayAccessor(rtt, storage) },
            storage = storage.element.storage,
            shift = storage.element.shift,
            mutable = storage.type.mutability == Mutability.Var,
            check = referenceCheck(storage.type, managedStore.definedType(type)),
        )

        return arrays.putIfAbsent(rtt, layout) ?: layout
    }

    private fun referenceCheck(
        fieldType: FieldType,
        owner: DefinedType?,
    ): ReferenceCheck? {

        val value = (fieldType.storageType as? StorageType.Value)?.type
        val reference: ReferenceType = (value as? ValueType.Reference)?.referenceType ?: return null

        return managedStore.checks.check(reference, owner)
    }
}

class RttStructAccessor(
    private val rtt: Rtt,
    private val layout: StructStorageLayout,
) : StructAccessor {

    override fun readBits(
        struct: Any,
        index: Int,
    ): Long {
        return layout.readBits(struct as CompiledStruct, index)
    }

    override fun readReference(
        struct: Any,
        index: Int,
    ): Any? {
        return layout.readReference(struct as CompiledStruct, index)
    }

    override fun writeBits(
        struct: Any,
        index: Int,
        value: Long,
    ) {
        layout.writeBits(struct as CompiledStruct, index, value)
    }

    override fun writeReference(
        struct: Any,
        index: Int,
        value: Any?,
    ) {
        layout.writeReference(struct as CompiledStruct, index, value)
    }

    override fun newStruct(): ManagedStruct {
        return layout.newStruct(rtt)
    }
}

class RttArrayAccessor(
    private val rtt: Rtt,
    private val layout: ArrayStorageLayout,
) : ArrayAccessor {

    override fun newStorage(length: Int): Any {
        return layout.newStorage(length)
    }

    override fun newArray(storage: Any): ManagedArray {
        return layout.newArray(rtt, storage)
    }
}
