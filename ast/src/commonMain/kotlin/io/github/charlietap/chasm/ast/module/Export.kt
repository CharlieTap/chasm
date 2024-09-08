package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.ast.value.NameValue
import kotlin.jvm.JvmInline

data class Export(
    val name: NameValue,
    val descriptor: Descriptor,
) {
    sealed interface Descriptor {

        @JvmInline
        value class Function(val functionIndex: FunctionIndex) : Descriptor

        @JvmInline
        value class Table(val tableIndex: TableIndex) : Descriptor

        @JvmInline
        value class Memory(val memoryIndex: MemoryIndex) : Descriptor

        @JvmInline
        value class Global(val globalIndex: GlobalIndex) : Descriptor

        @JvmInline
        value class Tag(val tagIndex: TagIndex) : Descriptor
    }
}
