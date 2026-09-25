package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.type.FunctionType

sealed interface CodegenImport {
    val moduleName: String
    val entityName: String
}

data class FunctionImport(
    override val moduleName: String,
    override val entityName: String,
    val type: FunctionType,
    val function: HostFunction,
) : CodegenImport

typealias ModuleFactory = (ByteArray) -> Module
typealias SuspendModuleFactory = suspend (ByteArray) -> Module
typealias InstanceFactory = (Store, Module, List<Import>) -> Instance
typealias SuspendInstanceFactory = suspend (Store, Module, List<Import>) -> Instance

fun allocateImports(
    store: Store,
    imports: List<CodegenImport>,
): List<Import> = imports.map { import ->
    val value = when (import) {
        is FunctionImport -> function(store, import.type, import.function)
    }
    Import(
        moduleName = import.moduleName,
        entityName = import.entityName,
        value = value,
    )
}
