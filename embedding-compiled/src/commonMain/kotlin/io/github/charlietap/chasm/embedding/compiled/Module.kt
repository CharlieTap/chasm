package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

class Module
    @InternalChasmApi
    constructor(
        val imports: List<ModuleImport>,
        val exports: List<ModuleExport>,
        @property:InternalChasmApi val payload: Any,
    )

data class ModuleImport(
    val moduleName: String,
    val entityName: String,
    val type: ExternalType,
)

data class ModuleExport(
    val name: String,
    val index: ModuleIndex,
    val type: ExternalType,
)

sealed interface ExternalType {

    data class Function(val functionType: FunctionType) : ExternalType

    data class Memory(val memoryType: MemoryType) : ExternalType

    data class Table(val tableType: TableType) : ExternalType

    data class Global(val globalType: GlobalType) : ExternalType

    data class Tag(val tagType: TagType) : ExternalType
}
