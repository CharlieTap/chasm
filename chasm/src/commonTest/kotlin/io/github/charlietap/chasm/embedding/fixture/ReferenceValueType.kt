package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.embedding.shapes.ValueType

fun publicReferenceValueType(): ValueType.Reference = publicFunctionReferenceValueType()

fun publicExternReferenceValueType() = ValueType.Reference.Ref(AbstractHeapType.Extern)

fun publicFunctionReferenceValueType() = ValueType.Reference.Ref(AbstractHeapType.Func)
