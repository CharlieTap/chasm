package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.TagType

@InternalChasmApi
data class TagInstance(
    val rtt: RTT,
    val type: TagType,
)
