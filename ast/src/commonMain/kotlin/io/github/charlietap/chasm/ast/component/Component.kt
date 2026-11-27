package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module

@InternalChasmApi
data class Component(
    val version: Version,
    val definitions: List<Definition>,
    val customs: List<Custom>,
)

@InternalChasmApi
sealed interface Definition

@InternalChasmApi
data class CoreModule(val module: Module) : Definition

@InternalChasmApi
data class CoreInstance(val instance: CoreInstanceDefinition) : Definition

@InternalChasmApi
data class CoreType(val type: CoreTypeDefinition) : Definition

@InternalChasmApi
data class NestedComponent(val component: Component) : Definition

@InternalChasmApi
data class Instance(val instance: InstanceDefinition) : Definition

@InternalChasmApi
data class Alias(val alias: AliasDefinition) : Definition

@InternalChasmApi
data class Type(val type: TypeDefinition) : Definition

@InternalChasmApi
data class Canon(val canon: CanonicalDefinition) : Definition

@InternalChasmApi
data class Start(val start: StartDefinition) : Definition

@InternalChasmApi
data class Import(
    val name: NameAttributes,
    val type: ExternalType,
) : Definition

@InternalChasmApi
data class Export(
    val name: NameAttributes,
    val target: ExportTarget,
    val type: ExternalType?,
) : Definition

@InternalChasmApi
data class Value(val value: ComponentValue) : Definition
