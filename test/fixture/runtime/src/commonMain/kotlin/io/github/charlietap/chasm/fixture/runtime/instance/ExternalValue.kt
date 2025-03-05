package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun externalValue(): ExternalValue = functionExternalValue()

fun functionExternalValue(
    address: Address.Function = functionAddress(),
) = ExternalValue.Function(
    address = address,
)

fun tableExternalValue(
    address: Address.Table = tableAddress(),
) = ExternalValue.Table(
    address = address,
)

fun memoryExternalValue(
    address: Address.Memory = memoryAddress(),
) = ExternalValue.Memory(
    address = address,
)

fun globalExternalValue(
    address: Address.Global = globalAddress(),
) = ExternalValue.Global(
    address = address,
)

fun tagExternalValue(
    address: Address.Tag = tagAddress(),
) = ExternalValue.Tag(
    address = address,
)
