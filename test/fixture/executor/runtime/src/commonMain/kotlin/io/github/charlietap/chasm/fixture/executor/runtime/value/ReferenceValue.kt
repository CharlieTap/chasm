package io.github.charlietap.chasm.fixture.executor.runtime.value

import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.hostAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.structAddress
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.HeapType

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
    address: Address.Struct = structAddress(),
) = ReferenceValue.Struct(
    address = address,
)

fun arrayReferenceValue(
    address: Address.Array = arrayAddress(),
) = ReferenceValue.Array(
    address = address,
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
