package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.Memory
import kotlin.jvm.JvmInline

@JvmInline
internal value class MemorySection(val memories: List<Memory>) : Section
