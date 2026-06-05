package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.instance.HostInstance

fun hostInstance(
    value: Any? = null,
) = HostInstance(
    value = value,
)
