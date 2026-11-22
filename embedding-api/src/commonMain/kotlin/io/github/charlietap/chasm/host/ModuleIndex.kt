package io.github.charlietap.chasm.host

import kotlin.jvm.JvmInline

sealed interface ModuleIndex {

    val index: Int

    @JvmInline
    value class FunctionIndex(override val index: Int) : ModuleIndex

    @JvmInline
    value class TableIndex(override val index: Int) : ModuleIndex

    @JvmInline
    value class MemoryIndex(override val index: Int) : ModuleIndex

    @JvmInline
    value class GlobalIndex(override val index: Int) : ModuleIndex

    @JvmInline
    value class TagIndex(override val index: Int) : ModuleIndex
}
