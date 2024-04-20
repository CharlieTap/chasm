package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.type.referenceType

fun elementInstance(
    type: ReferenceType = referenceType(),
    elements: MutableList<ReferenceValue> = mutableListOf(),
    dropped: Boolean = false,
) = ElementInstance(
    type = type,
    elements = elements,
    dropped = dropped,
)
