package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.type.TagType
import kotlin.jvm.JvmInline

@JvmInline
value class TagInstance(val type: TagType)
