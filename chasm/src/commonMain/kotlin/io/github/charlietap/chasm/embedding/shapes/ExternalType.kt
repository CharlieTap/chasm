package io.github.charlietap.chasm.embedding.shapes

import kotlin.jvm.JvmInline

sealed interface ExternalType {

    @JvmInline
    value class Function(val type: FunctionType) : ExternalType

    @JvmInline
    value class Global(val type: GlobalType) : ExternalType

    @JvmInline
    value class Memory(val type: MemoryType) : ExternalType

    @JvmInline
    value class Table(val type: TableType) : ExternalType

    @JvmInline
    value class Tag(val type: TagType) : ExternalType
}
