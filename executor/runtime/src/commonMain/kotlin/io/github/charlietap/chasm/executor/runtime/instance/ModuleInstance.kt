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
) {
    fun addFunctionAddress(address: Address.Function) = apply { this.functionAddresses.add(address) }

    fun addTableAddress(address: Address.Table) = apply { this.tableAddresses.add(address) }

    fun addMemoryAddress(address: Address.Memory) = apply { this.memAddresses.add(address) }

    fun addGlobalAddress(address: Address.Global) = apply { this.globalAddresses.add(address) }

    fun addElementAddress(address: Address.Element) = apply { this.elemAddresses.add(address) }

    fun addDataAddress(address: Address.Data) = apply { this.dataAddresses.add(address) }

    fun addExport(exportInstance: ExportInstance) = apply { this.exports.add(exportInstance) }
}
