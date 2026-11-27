package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Index.ComponentFunctionIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentInstanceIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentModuleIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentModuleInstanceIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentValueIndex
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ast.module.Index.TypeIndex as ModuleTypeIndex

@InternalChasmApi
sealed interface AliasDefinition {

    data class InstanceExport(val target: InstanceExportAliasTarget) : AliasDefinition

    data class CoreInstanceExport(val target: CoreInstanceExportAliasTarget) : AliasDefinition

    data class Outer(val target: OuterAliasTarget) : AliasDefinition
}

@InternalChasmApi
sealed interface InstanceExportAliasTarget {

    data class Module(
        val instance: ComponentInstanceIndex,
        val name: NameValue,
    ) : InstanceExportAliasTarget

    data class Function(
        val instance: ComponentInstanceIndex,
        val name: NameValue,
    ) : InstanceExportAliasTarget

    data class Value(
        val instance: ComponentInstanceIndex,
        val name: NameValue,
    ) : InstanceExportAliasTarget

    data class Type(
        val instance: ComponentInstanceIndex,
        val name: NameValue,
    ) : InstanceExportAliasTarget

    data class Component(
        val instance: ComponentInstanceIndex,
        val name: NameValue,
    ) : InstanceExportAliasTarget

    data class Instance(
        val instance: ComponentInstanceIndex,
        val name: NameValue,
    ) : InstanceExportAliasTarget
}

@InternalChasmApi
sealed interface CoreInstanceExportAliasTarget {

    data class Function(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Table(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Memory(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Global(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Tag(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Type(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Module(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget

    data class Instance(
        val instance: ComponentModuleInstanceIndex,
        val name: NameValue,
    ) : CoreInstanceExportAliasTarget
}

@InternalChasmApi
sealed interface OuterAliasTarget {

    data class Module(
        val count: UInt,
        val index: ComponentModuleIndex,
    ) : OuterAliasTarget

    data class CoreType(
        val count: UInt,
        val index: ModuleTypeIndex,
    ) : OuterAliasTarget

    data class Type(
        val count: UInt,
        val index: ComponentTypeIndex,
    ) : OuterAliasTarget

    data class Component(
        val count: UInt,
        val index: ComponentIndex,
    ) : OuterAliasTarget
}
