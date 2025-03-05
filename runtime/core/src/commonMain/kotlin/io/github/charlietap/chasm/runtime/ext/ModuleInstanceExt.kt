package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

inline fun ModuleInstance.addFunctionAddress(address: Address.Function) = apply { this.functionAddresses.add(address) }

inline fun ModuleInstance.addTableAddress(address: Address.Table) = apply { this.tableAddresses.add(address) }

inline fun ModuleInstance.addMemoryAddress(address: Address.Memory) = apply { this.memAddresses.add(address) }

inline fun ModuleInstance.addTagAddress(address: Address.Tag) = apply { this.tagAddresses.add(address) }

inline fun ModuleInstance.addGlobalAddress(address: Address.Global) = apply { this.globalAddresses.add(address) }

inline fun ModuleInstance.addElementAddress(address: Address.Element) = apply { this.elemAddresses.add(address) }

inline fun ModuleInstance.addDataAddress(address: Address.Data) = apply { this.dataAddresses.add(address) }

inline fun ModuleInstance.addExport(exportInstance: ExportInstance) = apply { this.exports.add(exportInstance) }
