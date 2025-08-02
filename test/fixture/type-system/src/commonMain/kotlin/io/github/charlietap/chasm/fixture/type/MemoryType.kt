package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus

fun memoryType(
    addressType: AddressType = addressType(),
    limits: Limits = limits(),
    shared: SharedStatus = unsharedStatus(),
) = MemoryType(
    addressType = addressType,
    limits = limits,
    shared = shared,
)
