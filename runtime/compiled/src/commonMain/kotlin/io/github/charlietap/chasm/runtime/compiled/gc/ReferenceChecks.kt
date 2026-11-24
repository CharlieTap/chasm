package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ReferenceType

interface ReferenceChecks {

    fun check(
        type: ReferenceType,
        owner: DefinedType?,
    ): ReferenceCheck

    fun owned(value: Any): Boolean
}

sealed interface ReferenceCheck {

    fun accepts(value: Any?): Boolean
}

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
    private val nullable: Boolean,
    private val heap: AbstractHeapType,
    private val checks: ReferenceChecks,
) : ReferenceCheck {

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
    private val nullable: Boolean,
    private val expected: RTT,
    private val types: GcTypes,
    private val functions: ManagedFunctions,
    private val heap: WasmHeap,
) : ReferenceCheck {

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
    private val nullable: Boolean,
    private val checks: ReferenceChecks,
) : ReferenceCheck {

    override fun accepts(value: Any?): Boolean {

        if (value == null) {
            return nullable
        }

        val managed = value is ManagedStruct || value is ManagedArray || value is ManagedFunction

        return managed && checks.owned(value)
    }
}
