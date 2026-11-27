package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.ast.module.Index.TypeIndex as ModuleTypeIndex

@InternalChasmApi
sealed interface CoreTypeDefinition {

    data class DefinedType(val type: RecursiveType) : CoreTypeDefinition

    data class ModuleType(val declarations: List<CoreModuleDeclaration>) : CoreTypeDefinition
}

@InternalChasmApi
sealed interface CoreModuleDeclaration {

    data class Type(val type: CoreTypeDefinition) : CoreModuleDeclaration

    data class Import(
        val moduleName: NameValue,
        val entityName: NameValue,
        val descriptor: CoreExternalType,
    ) : CoreModuleDeclaration

    data class Export(
        val name: NameValue,
        val descriptor: CoreExternalType,
    ) : CoreModuleDeclaration

    data class OuterAlias(
        val count: UInt,
        val typeIndex: ModuleTypeIndex,
    ) : CoreModuleDeclaration
}

@InternalChasmApi
sealed interface CoreExternalType {

    data class Function(val typeIndex: ModuleTypeIndex) : CoreExternalType

    data class Table(val type: TableType) : CoreExternalType

    data class Memory(val type: MemoryType) : CoreExternalType

    data class Global(val type: GlobalType) : CoreExternalType

    data class Tag(
        val attribute: TagType.Attribute,
        val typeIndex: ModuleTypeIndex,
    ) : CoreExternalType
}
