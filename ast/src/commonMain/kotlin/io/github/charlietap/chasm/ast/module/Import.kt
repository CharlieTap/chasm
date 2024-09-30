package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.ast.value.NameValue
import kotlin.jvm.JvmInline

data class Import(
    val moduleName: NameValue,
    val entityName: NameValue,
    val descriptor: Descriptor,
) {
    sealed interface Descriptor {

        @JvmInline
        value class Function(val type: FunctionType) : Descriptor

        @JvmInline
        value class Table(val type: TableType) : Descriptor

        @JvmInline
        value class Memory(val type: MemoryType) : Descriptor

        @JvmInline
        value class Global(val type: GlobalType) : Descriptor

        @JvmInline
        value class Tag(val type: TagType) : Descriptor
    }
}
