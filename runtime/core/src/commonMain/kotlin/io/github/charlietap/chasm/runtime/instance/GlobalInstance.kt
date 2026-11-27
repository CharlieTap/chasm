package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.host.HostGlobal
import io.github.charlietap.chasm.type.GlobalType

@InternalChasmApi
data class GlobalInstance(
    val type: GlobalType,
    var value: Long,
) : HostGlobal {

    override var rawValue: Long
        get() = value
        set(value) {
            this.value = value
        }
}
