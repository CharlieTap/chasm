package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.DataInstance

fun dataInstance(
    bytes: UByteArray = DataInstance.EMPTY,
) = DataInstance(
    bytes = bytes,
)
