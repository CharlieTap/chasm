package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Value

fun publicReferenceValue() = publicFunctionReferenceValue()

fun publicExternReferenceValue(
    referenceValue: Value.Reference = publicReferenceValue(),
) = Value.Reference.Extern(referenceValue)

fun publicFunctionReferenceValue(
    address: Int = 0,
) = Value.Reference.Func(address)
