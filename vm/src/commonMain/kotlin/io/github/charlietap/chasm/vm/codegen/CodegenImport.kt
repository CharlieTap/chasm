package io.github.charlietap.chasm.vm.codegen

import io.github.charlietap.chasm.vm.FunctionType
import io.github.charlietap.chasm.vm.HostFunction

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
