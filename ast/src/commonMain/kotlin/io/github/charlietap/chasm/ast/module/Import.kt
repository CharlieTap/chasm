package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.value.NameValue

data class Import(
    val moduleName: NameValue,
    val entityName: NameValue,
    val descriptor: Descriptor,
) {
    sealed interface Descriptor {
        data class Function(val typeIndex: TypeIndex) : Descriptor

        data class Table(val type: TableType) : Descriptor

        data class Memory(val type: MemoryType) : Descriptor

        data class Global(val type: GlobalType) : Descriptor
    }
}
