package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.type.TagType

fun tagInstance(
    type: TagType = tagType(),
) = TagInstance(
    type = type,
)
