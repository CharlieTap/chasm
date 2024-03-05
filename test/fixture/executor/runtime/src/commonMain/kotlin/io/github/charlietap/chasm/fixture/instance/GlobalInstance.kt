package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.value.executionValue

fun globalInstance(
    type: GlobalType = globalType(),
    value: ExecutionValue = executionValue(),
) = GlobalInstance(
    type = type,
    value = value,
)
