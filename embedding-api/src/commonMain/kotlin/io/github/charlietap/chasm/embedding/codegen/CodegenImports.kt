package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.Import
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

fun allocateImports(
    store: Store,
    imports: List<CodegenImport>,
): List<Import> {
    return imports.map { import ->

        val value = when (import) {
            is FunctionImport -> function(store, import.type, import.function)
        }

        Import(
            moduleName = import.moduleName,
            entityName = import.entityName,
            value = value,
        )
    }
}
