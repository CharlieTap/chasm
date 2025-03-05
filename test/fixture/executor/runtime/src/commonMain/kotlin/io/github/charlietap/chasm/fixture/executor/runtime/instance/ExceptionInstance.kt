package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance

fun exceptionInstance(
    tagAddress: Address.Tag = tagAddress(),
    fields: LongArray = longArrayOf(),
) = ExceptionInstance(
    tagAddress = tagAddress,
    fields = fields,
)
