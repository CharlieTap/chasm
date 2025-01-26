package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address

fun exceptionInstance(
    tagAddress: Address.Tag = tagAddress(),
    fields: LongArray = longArrayOf(),
) = ExceptionInstance(
    tagAddress = tagAddress,
    fields = fields,
)
