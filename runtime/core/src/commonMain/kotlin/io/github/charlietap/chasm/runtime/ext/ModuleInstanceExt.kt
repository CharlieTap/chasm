package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

@InternalChasmApi
inline fun ModuleInstance.addFunctionAddress(address: Address.Function) = apply { this.functionAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addTableAddress(address: Address.Table) = apply { this.tableAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addMemoryAddress(address: Address.Memory) = apply { this.memAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addTagAddress(address: Address.Tag) = apply { this.tagAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addGlobalAddress(address: Address.Global) = apply { this.globalAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addElementAddress(address: Address.Element) = apply { this.elemAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addDataAddress(address: Address.Data) = apply { this.dataAddresses.add(address) }

@InternalChasmApi
inline fun ModuleInstance.addExport(exportInstance: ExportInstance) = apply { this.exports.add(exportInstance) }
