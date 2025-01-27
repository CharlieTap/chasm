package io.github.charlietap.chasm.fixture.executor.runtime.value

import io.github.charlietap.chasm.executor.runtime.value.PackedValue

fun packedValue(): PackedValue = bytePackedValue()

fun bytePackedValue(
    value: Long = 0L,
) = PackedValue.I8(
    value = value,
)

fun shortPackedValue(
    value: Long = 0L,
) = PackedValue.I16(
    value = value,
)
