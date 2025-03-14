package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType

fun tableType(
    referenceType: ReferenceType = ReferenceType.Ref(AbstractHeapType.Func),
    limits: Limits = limits(),
) = TableType(
    referenceType = referenceType,
    limits = limits,
)
