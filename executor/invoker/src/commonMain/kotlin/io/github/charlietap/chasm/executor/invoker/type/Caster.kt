package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.isArrayReference
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isFunctionReference
import io.github.charlietap.chasm.runtime.ext.isHostReference
import io.github.charlietap.chasm.runtime.ext.isI31Reference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.isStructReference
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType

typealias Caster = (Long, ReferenceType, ModuleInstance, Store) -> Boolean

internal inline fun Caster(
    referenceValue: Long,
    referenceType: ReferenceType,
    moduleInstance: ModuleInstance,
    store: Store,
): Boolean {

    if (referenceValue.isNullableReference()) {
        return when (referenceType) {
            is ReferenceType.Ref -> false
            is ReferenceType.RefNull -> true
        }
    }

    val heapType = referenceType.heapType
    return when (heapType) {
        is AbstractHeapType -> referenceValue.isTypeOf(heapType)
        is ConcreteHeapType -> {
            when (heapType) {
                is ConcreteHeapType.TypeIndex -> referenceValue.isInstanceOf(heapType, moduleInstance, store)
                is ConcreteHeapType.Defined,
                is ConcreteHeapType.RecursiveTypeIndex,
                -> false
            }
        }
    }
}

private inline fun Long.isTypeOf(
    heapType: AbstractHeapType,
): Boolean = when (heapType) {
    AbstractHeapType.Func -> isFunctionReference()
    AbstractHeapType.Extern -> isExternReference()
    AbstractHeapType.Any ->
        isI31Reference() ||
            isArrayReference() ||
            isStructReference() ||
            isHostReference()

    AbstractHeapType.Eq ->
        isI31Reference() ||
            isArrayReference() ||
            isStructReference()

    AbstractHeapType.I31 -> isI31Reference()
    AbstractHeapType.Array -> isArrayReference()
    AbstractHeapType.Struct -> isStructReference()
    AbstractHeapType.Exception,
    AbstractHeapType.NoException,
    is AbstractHeapType.Bottom,
    AbstractHeapType.NoExtern,
    AbstractHeapType.NoFunc,
    AbstractHeapType.None,
    -> false
}

private inline fun Long.isInstanceOf(
    typeIndex: ConcreteHeapType.TypeIndex,
    moduleInstance: ModuleInstance,
    store: Store,
): Boolean {

    val castRuntimeType = moduleInstance.runtimeTypes.getOrNull(typeIndex.index) ?: return false

    val actualRuntimeType = when {
        isStructReference() -> {
            store.struct(toStructAddress()).rtt
        }

        isArrayReference() -> {
            store.array(toArrayAddress()).rtt
        }

        isFunctionReference() -> {
            store.function(toFunctionAddress()).rtt
        }
        else -> return false
    }

    return when {
        actualRuntimeType === castRuntimeType -> true
        else -> {
            actualRuntimeType.superTypes.any { superType ->
                superType === castRuntimeType
            }
        }
    }
}
