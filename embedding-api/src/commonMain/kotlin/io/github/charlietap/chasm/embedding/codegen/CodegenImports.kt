package io.github.charlietap.chasm.embedding.codegen

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
