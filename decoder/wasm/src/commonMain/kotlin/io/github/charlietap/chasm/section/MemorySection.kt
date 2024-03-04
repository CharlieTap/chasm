package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Memory
import kotlin.jvm.JvmInline

@JvmInline
value class MemorySection(val memories: List<Memory>) : Section
