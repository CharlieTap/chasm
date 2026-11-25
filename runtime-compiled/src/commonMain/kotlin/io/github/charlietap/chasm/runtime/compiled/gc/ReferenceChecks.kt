package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ReferenceType

@InternalChasmApi
interface ReferenceChecks {

    fun check(
        type: ReferenceType,
        owner: DefinedType?,
    ): ReferenceCheck

    fun owned(value: Any): Boolean
}

@InternalChasmApi
sealed interface ReferenceCheck {

    val nullable: Boolean

    val admitsUncheckedWrites: Boolean

    fun accepts(value: Any?): Boolean

    fun includesHeap(other: ReferenceCheck): Boolean

    fun includes(other: ReferenceCheck): Boolean {
        return (nullable || !other.nullable) && includesHeap(other)
    }
}

@InternalChasmApi
class StoreReferenceChecks(
    private val managedStore: ManagedStore,
    private val functions: ManagedFunctions,
    private val store: Store,
) : ReferenceChecks {

    @Synchronized
    override fun check(
        type: ReferenceType,
        owner: DefinedType?,
    ): ReferenceCheck {

        val nullable = type is ReferenceType.RefNull

        return when (val heap = type.heapType) {
            is AbstractHeapType -> AbstractReferenceCheck(nullable, heap, this)
            is ConcreteHeapType.Defined -> definedCheck(nullable, heap.definedType)
            is ConcreteHeapType.RecursiveTypeIndex if owner != null -> definedCheck(nullable, member(owner, heap.index))
            else -> ClassReferenceCheck(nullable, this)
        }
    }

    override fun owned(value: Any): Boolean {
        return when (value) {
            is ManagedFunction -> functions.owns(value)
            is ManagedStruct, is ManagedArray -> managedStore.types.type(value) >= 0
            is WasmException -> value.store === store
            else -> true
        }
    }

    private fun definedCheck(
        nullable: Boolean,
        type: DefinedType,
    ): ReferenceCheck {
        return DefinedReferenceCheck(nullable, store.heap.registerRuntimeType(type), managedStore.types, functions, store.heap)
    }

    private fun member(
        owner: DefinedType,
        index: Int,
    ): DefinedType {
        return DefinedType(owner.recursiveType, index, owner.typeIndex - owner.recursiveTypeIndex + index)
    }
}

private class AbstractReferenceCheck(
    override val nullable: Boolean,
    val heap: AbstractHeapType,
    private val checks: ReferenceChecks,
) : ReferenceCheck {

    override val admitsUncheckedWrites: Boolean = nullable && (heap == AbstractHeapType.Any || heap == AbstractHeapType.Extern)

    override fun includesHeap(other: ReferenceCheck): Boolean {
        return when (other) {
            is AbstractReferenceCheck -> abstractSubtype(other.heap, heap)
            is DefinedReferenceCheck -> abstractSubtype(other.kind, heap)
            is ClassReferenceCheck -> false
        }
    }

    override fun accepts(value: Any?): Boolean {

        if (value == null) {
            return nullable
        }

        if (!checks.owned(value)) {
            return false
        }

        return when (heap) {
            AbstractHeapType.Func -> value is ManagedFunction
            AbstractHeapType.Extern, AbstractHeapType.Any -> value !is ManagedFunction && value !is WasmException
            AbstractHeapType.Eq -> value is ManagedStruct || value is ManagedArray || value is I31Reference
            AbstractHeapType.Struct -> value is ManagedStruct
            AbstractHeapType.Array -> value is ManagedArray
            AbstractHeapType.I31 -> value is I31Reference
            AbstractHeapType.Exception -> value is WasmException
            else -> false
        }
    }
}

private class DefinedReferenceCheck(
    override val nullable: Boolean,
    val expected: RTT,
    private val types: GcTypes,
    private val functions: ManagedFunctions,
    private val heap: WasmHeap,
) : ReferenceCheck {

    val kind: AbstractHeapType = when {
        heap.isStructType(HostGcType(expected.value)) -> AbstractHeapType.Struct
        heap.isArrayType(HostGcType(expected.value)) -> AbstractHeapType.Array
        else -> AbstractHeapType.Func
    }

    override val admitsUncheckedWrites: Boolean = false

    override fun includesHeap(other: ReferenceCheck): Boolean {
        return when (other) {
            is DefinedReferenceCheck -> heap.matchesRuntimeType(other.expected, expected)
            is AbstractReferenceCheck -> other.heap in BOTTOM_TYPES && abstractSubtype(other.heap, kind)
            is ClassReferenceCheck -> false
        }
    }

    override fun accepts(value: Any?): Boolean {

        if (value == null) {
            return nullable
        }

        val actual = when (value) {
            is ManagedFunction -> if (functions.owns(value)) value.rtt else return false
            is ManagedStruct, is ManagedArray -> types.type(value)
            else -> return false
        }

        return actual >= 0 && heap.matchesRuntimeType(RTT(actual), expected)
    }
}

private class ClassReferenceCheck(
    override val nullable: Boolean,
    private val checks: ReferenceChecks,
) : ReferenceCheck {

    override val admitsUncheckedWrites: Boolean = false

    override fun includesHeap(other: ReferenceCheck): Boolean {
        return when (other) {
            is DefinedReferenceCheck, is ClassReferenceCheck -> true
            is AbstractReferenceCheck -> other.heap in MANAGED_TYPES || other.heap in BOTTOM_TYPES
        }
    }

    override fun accepts(value: Any?): Boolean {

        if (value == null) {
            return nullable
        }

        val managed = value is ManagedStruct || value is ManagedArray || value is ManagedFunction

        return managed && checks.owned(value)
    }
}

private fun abstractSubtype(
    subtype: AbstractHeapType,
    supertype: AbstractHeapType,
): Boolean {

    if (subtype == supertype) {
        return true
    }

    return when (supertype) {
        AbstractHeapType.Any -> subtype in EQ_TYPES || subtype == AbstractHeapType.Eq
        AbstractHeapType.Eq -> subtype in EQ_TYPES
        AbstractHeapType.Struct, AbstractHeapType.Array, AbstractHeapType.I31 -> subtype == AbstractHeapType.None
        AbstractHeapType.Func -> subtype == AbstractHeapType.NoFunc
        AbstractHeapType.Extern -> subtype == AbstractHeapType.NoExtern
        AbstractHeapType.Exception -> subtype == AbstractHeapType.NoException
        else -> false
    }
}

private val EQ_TYPES = setOf(AbstractHeapType.Struct, AbstractHeapType.Array, AbstractHeapType.I31, AbstractHeapType.None)

private val MANAGED_TYPES = setOf(AbstractHeapType.Struct, AbstractHeapType.Array, AbstractHeapType.Func)

private val BOTTOM_TYPES = setOf(AbstractHeapType.None, AbstractHeapType.NoFunc, AbstractHeapType.NoExtern, AbstractHeapType.NoException)
