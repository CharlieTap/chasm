package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.type.referenceType

fun elementInstance(
    type: ReferenceType = referenceType(),
    elements: Array<ReferenceValue> = arrayOf(),
    dropped: Boolean = false,
) = ElementInstance(
    type = type,
    elements = elements,
    dropped = dropped,
)
