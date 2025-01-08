package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun exceptionInstance(
    tagAddress: Address.Tag = tagAddress(),
    fields: List<ExecutionValue> = emptyList(),
) = ExceptionInstance(
    tagAddress = tagAddress,
    fields = fields,
)
