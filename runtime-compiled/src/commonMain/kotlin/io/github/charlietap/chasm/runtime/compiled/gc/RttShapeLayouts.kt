package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.ConcurrentHashMap

@InternalChasmApi
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
            rtt = rtt,
            storageLayout = storage,
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
            rtt = rtt,
            storageLayout = storage,
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
