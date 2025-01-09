package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.TagType
import kotlin.jvm.JvmInline

@JvmInline
value class TagInstance(val type: TagType)
