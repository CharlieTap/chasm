package io.github.charlietap.chasm.executor.runtime.type

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.TagType
import kotlin.jvm.JvmInline

sealed interface ExternalType {
    @JvmInline
    value class Function(val functionType: FunctionType) : ExternalType

    @JvmInline
    value class Table(val tableType: TableType) : ExternalType

    @JvmInline
    value class Memory(val memoryType: MemoryType) : ExternalType

    @JvmInline
    value class Tag(val tagType: TagType) : ExternalType

    @JvmInline
    value class Global(val globalType: GlobalType) : ExternalType
}
