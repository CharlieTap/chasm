package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.DataInstance

fun dataInstance(
    bytes: UByteArray = ubyteArrayOf(),
    dropped: Boolean = false,
) = DataInstance(
    bytes = bytes,
    dropped = dropped,
)
