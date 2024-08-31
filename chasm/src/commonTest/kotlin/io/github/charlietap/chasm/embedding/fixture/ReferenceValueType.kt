package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.ValueType

fun publicReferenceValueType(): ValueType.Reference = publicFunctionReferenceValueType()

fun publicExternReferenceValueType() = ValueType.Reference.Ref(HeapType.Extern)

fun publicFunctionReferenceValueType() = ValueType.Reference.Ref(HeapType.Func)
