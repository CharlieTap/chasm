package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.ast.value.NameValue

data class Export(
    val name: NameValue,
    val descriptor: Descriptor,
) {
    sealed interface Descriptor {
        data class Function(val functionIndex: FunctionIndex) : Descriptor

        data class Table(val tableIndex: TableIndex) : Descriptor

        data class Memory(val memoryIndex: MemoryIndex) : Descriptor

        data class Global(val globalIndex: GlobalIndex) : Descriptor
    }
}
