package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.fixture.ir.type.referenceType
import io.github.charlietap.chasm.ir.type.ReferenceType

fun elementInstance(
    type: ReferenceType = referenceType(),
    elements: LongArray = longArrayOf(),
) = ElementInstance(
    type = type,
    elements = elements,
)
