package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.value.NameValue
import kotlin.jvm.JvmInline

data class Export(
    val name: NameValue,
    val descriptor: Descriptor,
) {
    sealed interface Descriptor {

        @JvmInline
        value class Function(val functionIndex: Index.FunctionIndex) : Descriptor

        @JvmInline
        value class Table(val tableIndex: Index.TableIndex) : Descriptor

        @JvmInline
        value class Memory(val memoryIndex: Index.MemoryIndex) : Descriptor

        @JvmInline
        value class Global(val globalIndex: Index.GlobalIndex) : Descriptor

        @JvmInline
        value class Tag(val tagIndex: Index.TagIndex) : Descriptor
    }
}
