package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ReferenceType.Ref
import io.github.charlietap.chasm.ast.type.ReferenceType.RefNull
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.extremas.BottomOf
import io.github.charlietap.chasm.type.extremas.BottomOfHeapType

fun TypeOfReferenceValue(
    value: ReferenceValue,
    store: Store,
    moduleInstance: ModuleInstance,
): ReferenceType? = TypeOfReferenceValue(
    value = value,
    store = store,
    moduleInstance = moduleInstance,
    bottomOfHeapType = ::BottomOfHeapType,
)

internal fun TypeOfReferenceValue(
    value: ReferenceValue,
    store: Store,
    moduleInstance: ModuleInstance,
    bottomOfHeapType: BottomOf<HeapType>,
): ReferenceType? = when (value) {
    is ReferenceValue.Null -> bottomOfHeapType(value.heapType, moduleInstance.types)?.let(::RefNull)
    is ReferenceValue.Struct -> {
        store.structs.getOrNull(value.address.address)?.value?.definedType?.let { definedType ->
            Ref(ConcreteHeapType.Defined(definedType))
        }
    }
    is ReferenceValue.Array -> {
        store.arrays.getOrNull(value.address.address)?.value?.definedType?.let { definedType ->
            Ref(ConcreteHeapType.Defined(definedType))
        }
    }
    is ReferenceValue.Function -> {
        store.functions.getOrNull(value.address.address)?.type?.let { definedType ->
            Ref(ConcreteHeapType.Defined(definedType))
        }
    }
    is ReferenceValue.I31 -> Ref(AbstractHeapType.I31)
    is ReferenceValue.Extern -> Ref(AbstractHeapType.Extern)
    is ReferenceValue.Host -> Ref(AbstractHeapType.Any)
    else -> null
}
