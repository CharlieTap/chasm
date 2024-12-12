package io.github.charlietap.chasm.fixture.executor.runtime.value

import io.github.charlietap.chasm.executor.runtime.value.PackedValue

fun packedValue(): PackedValue = bytePackedValue()

fun bytePackedValue(
    value: UByte = 0u,
) = PackedValue.I8(
    value = value,
)

fun shortPackedValue(
    value: UShort = 0u,
) = PackedValue.I16(
    value = value,
)
