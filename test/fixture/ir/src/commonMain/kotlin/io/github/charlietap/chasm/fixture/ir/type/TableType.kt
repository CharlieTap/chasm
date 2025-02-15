package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.Limits
import io.github.charlietap.chasm.ir.type.ReferenceType
import io.github.charlietap.chasm.ir.type.TableType

fun tableType(
    referenceType: ReferenceType = ReferenceType.Ref(AbstractHeapType.Func),
    limits: Limits = limits(),
) = TableType(
    referenceType = referenceType,
    limits = limits,
)
