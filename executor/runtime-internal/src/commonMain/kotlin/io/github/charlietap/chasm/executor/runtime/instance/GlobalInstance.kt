package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ir.type.GlobalType

data class GlobalInstance(
    val type: GlobalType,
    var value: Long,
)
