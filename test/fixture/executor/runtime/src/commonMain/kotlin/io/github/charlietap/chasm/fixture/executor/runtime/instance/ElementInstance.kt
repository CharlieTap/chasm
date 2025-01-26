package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.fixture.ast.type.referenceType

fun elementInstance(
    type: ReferenceType = referenceType(),
    elements: LongArray = longArrayOf(),
) = ElementInstance(
    type = type,
    elements = elements,
)
