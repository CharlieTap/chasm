package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
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
import io.github.charlietap.chasm.runtime.ext.toNullableReference
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ReferenceType.Ref
import io.github.charlietap.chasm.type.ReferenceType.RefNull
import io.github.charlietap.chasm.type.extremas.BottomOf
import io.github.charlietap.chasm.type.extremas.BottomOfHeapType

fun TypeOfReferenceValue(
    value: Long,
    store: Store,
    moduleInstance: ModuleInstance,
): ReferenceType = TypeOfReferenceValue(
    value = value,
    store = store,
    moduleInstance = moduleInstance,
    bottomOfHeapType = ::BottomOfHeapType,
)

internal inline fun TypeOfReferenceValue(
    value: Long,
    store: Store,
    moduleInstance: ModuleInstance,
    crossinline bottomOfHeapType: BottomOf<HeapType>,
): ReferenceType = when {
    value.isNullableReference() -> {
        bottomOfHeapType(value.toNullableReference().heapType, moduleInstance.types)?.let(::RefNull)
            ?: throw InvocationException(InvocationError.FailedToGetTypeOfReferenceValue)
    }
    value.isStructReference() -> {
        val struct = store.struct(value.toStructAddress())
        Ref(ConcreteHeapType.Defined(struct.definedType))
    }
    value.isArrayReference() -> {
        val array = store.array(value.toArrayAddress())
        Ref(ConcreteHeapType.Defined(array.definedType))
    }
    value.isFunctionReference() -> {
        val function = store.function(value.toFunctionAddress())
        Ref(ConcreteHeapType.Defined(function.type))
    }
    value.isI31Reference() -> Ref(AbstractHeapType.I31)
    value.isExternReference() -> Ref(AbstractHeapType.Extern)
    value.isHostReference() -> Ref(AbstractHeapType.Any)
    else -> throw InvocationException(InvocationError.FailedToGetTypeOfReferenceValue)
}
