package io.github.charlietap.chasm.fixture.runtime.type

import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.ReferenceType

fun referenceTypeTest(
    referenceType: ReferenceType = referenceType(),
    runtimeTypes: RuntimeTypeMap = RuntimeTypeMap.Empty,
) = ReferenceTypeTest.from(referenceType, runtimeTypes)
