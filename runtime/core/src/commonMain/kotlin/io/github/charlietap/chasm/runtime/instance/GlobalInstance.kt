package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.type.GlobalType

data class GlobalInstance(
    val type: GlobalType,
    var value: Long,
)
