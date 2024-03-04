package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

data class GlobalInstance(
    val type: GlobalType,
    var value: ExecutionValue,
)
