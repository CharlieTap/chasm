package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.ast.type.globalType
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue

fun globalInstance(
    type: GlobalType = globalType(),
    value: ExecutionValue = executionValue(),
) = GlobalInstance(
    type = type,
    value = value,
)
