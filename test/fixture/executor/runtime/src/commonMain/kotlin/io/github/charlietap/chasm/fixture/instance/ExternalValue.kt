package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address

fun externalValue(): ExternalValue = functionExternalValue()

fun functionExternalValue(
    address: Address.Function = functionAddress(),
) = ExternalValue.Function(address)

fun tableExternalValue(
    address: Address.Table = tableAddress(),
) = ExternalValue.Table(address)

fun memoryExternalValue(
    address: Address.Memory = memoryAddress(),
) = ExternalValue.Memory(address)

fun globalExternalValue(
    address: Address.Global = globalAddress(),
) = ExternalValue.Global(address)
