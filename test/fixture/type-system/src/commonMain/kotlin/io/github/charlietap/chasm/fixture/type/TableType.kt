package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import kotlin.invoke

fun tableType(
    addressType: AddressType = addressType(),
    referenceType: ReferenceType = ReferenceType.Ref(AbstractHeapType.Func),
    limits: Limits = limits(),
) = TableType(
    addressType = addressType,
    referenceType = referenceType,
    limits = limits,
)
