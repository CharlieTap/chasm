package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.type.GlobalType

fun globalInstance(
    type: GlobalType = globalType(),
    value: Long = 0L,
) = GlobalInstance(
    type = type,
    value = value,
)
