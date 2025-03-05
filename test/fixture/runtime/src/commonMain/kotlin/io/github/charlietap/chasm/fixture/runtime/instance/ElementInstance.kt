package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.type.ReferenceType

fun elementInstance(
    type: ReferenceType = referenceType(),
    elements: LongArray = longArrayOf(),
) = ElementInstance(
    type = type,
    elements = elements,
)
