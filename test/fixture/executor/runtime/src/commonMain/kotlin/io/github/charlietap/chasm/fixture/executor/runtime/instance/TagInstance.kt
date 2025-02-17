package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.TagInstance
import io.github.charlietap.chasm.fixture.ir.type.tagType
import io.github.charlietap.chasm.ir.type.TagType

fun tagInstance(
    type: TagType = tagType(),
) = TagInstance(
    type = type,
)
