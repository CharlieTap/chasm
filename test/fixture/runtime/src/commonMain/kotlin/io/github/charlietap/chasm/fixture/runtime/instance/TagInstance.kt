package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.TagType

fun tagInstance(
    rtt: RTT = rtt(),
    type: TagType = tagType(),
) = TagInstance(
    rtt = rtt,
    type = type,
)
