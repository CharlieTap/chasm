package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store

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
