package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun publicReferenceValue() = publicFunctionReferenceValue()

fun publicExternReferenceValue(
    referenceValue: ReferenceValue = publicReferenceValue(),
) = ReferenceValue.Extern(referenceValue)

fun publicFunctionReferenceValue(
    address: Int = 0,
) = ReferenceValue.Function(Address.Function(address))
