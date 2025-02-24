package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.value.NameValue
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import kotlin.jvm.JvmInline

data class Import(
    val moduleName: NameValue,
    val entityName: NameValue,
    val descriptor: Descriptor,
) {
    sealed interface Descriptor {

        @JvmInline
        value class Function(val type: DefinedType) : Descriptor

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
