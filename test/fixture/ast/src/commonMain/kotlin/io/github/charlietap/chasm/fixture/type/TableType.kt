package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType

fun tableType(
    referenceType: ReferenceType = ReferenceType.RefNull(AbstractHeapType.Func),
    limits: Limits = limits(),
) = TableType(
    referenceType = referenceType,
    limits = limits,
)
