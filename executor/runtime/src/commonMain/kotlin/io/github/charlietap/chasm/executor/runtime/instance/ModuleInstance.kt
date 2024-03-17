package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.store.Address

data class ModuleInstance(
    val types: List<FunctionType>,
    val functionAddresses: MutableList<Address.Function> = mutableListOf(),
    val tableAddresses: MutableList<Address.Table> = mutableListOf(),
    val memAddresses: MutableList<Address.Memory> = mutableListOf(),
    val globalAddresses: MutableList<Address.Global> = mutableListOf(),
    val elemAddresses: MutableList<Address.Element> = mutableListOf(),
    val dataAddresses: MutableList<Address.Data> = mutableListOf(),
    val exports: MutableList<ExportInstance> = mutableListOf(),
)
