package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.executor.runtime.instance.TagInstance
import io.github.charlietap.chasm.fixture.ast.type.tagType

fun tagInstance(
    type: TagType = tagType(),
) = TagInstance(
    type = type,
)
