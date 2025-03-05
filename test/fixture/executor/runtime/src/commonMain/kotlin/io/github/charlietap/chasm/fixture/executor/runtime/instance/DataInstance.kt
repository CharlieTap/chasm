package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.runtime.instance.DataInstance

fun dataInstance(
    bytes: UByteArray = DataInstance.EMPTY,
) = DataInstance(
    bytes = bytes,
)
