package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.fixture.ast.type.globalType

fun globalInstance(
    type: GlobalType = globalType(),
    value: Long = 0L,
) = GlobalInstance(
    type = type,
    value = value,
)
