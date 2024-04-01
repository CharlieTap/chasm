package io.github.charlietap.chasm.executor.runtime.type

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import kotlin.jvm.JvmInline

sealed interface ExternalType {
    @JvmInline
    value class Function(val definedType: DefinedType) : ExternalType

    @JvmInline
    value class Table(val tableType: TableType) : ExternalType

    @JvmInline
    value class Memory(val memoryType: MemoryType) : ExternalType

    @JvmInline
    value class Global(val globalType: GlobalType) : ExternalType
}
