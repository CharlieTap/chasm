package io.github.charlietap.chasm.fixture.executor.runtime.value

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.hostAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.structInstance

fun referenceValue(): ReferenceValue = nullReferenceValue()

fun nullReferenceValue(
    heapType: HeapType = heapType(),
) = ReferenceValue.Null(
    heapType = heapType,
)

fun i31ReferenceValue(
    value: UInt = 0u,
) = ReferenceValue.I31(
    value = value,
)

fun structReferenceValue(
    instance: StructInstance = structInstance(),
) = ReferenceValue.Struct(
    instance = instance,
)

fun arrayReferenceValue(
    instance: ArrayInstance = arrayInstance(),
) = ReferenceValue.Array(
    instance = instance,
)

fun functionReferenceValue(
    address: Address.Function = functionAddress(),
) = ReferenceValue.Function(
    address = address,
)

fun hostReferenceValue(
    address: Address.Host = hostAddress(),
) = ReferenceValue.Host(
    address = address,
)

fun externReferenceValue(
    referenceValue: ReferenceValue = referenceValue(),
) = ReferenceValue.Extern(
    referenceValue = referenceValue,
)
