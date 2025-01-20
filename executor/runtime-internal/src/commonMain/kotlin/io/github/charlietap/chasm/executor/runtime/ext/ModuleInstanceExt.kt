package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address

inline fun ModuleInstance.functionAddress(index: Index.FunctionIndex): Result<Address.Function, InvocationError.FunctionAddressLookupFailed> =
    functionAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.FunctionAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.tableAddress(index: Index.TableIndex): Result<Address.Table, InvocationError.TableAddressLookupFailed> =
    tableAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.TableAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.memoryAddress(index: Index.MemoryIndex): Result<Address.Memory, InvocationError.MemoryAddressLookupFailed> =
    memAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.MemoryAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.tagAddress(index: Index.TagIndex): Result<Address.Tag, InvocationError.TagAddressLookupFailed> =
    tagAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.TagAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.globalAddress(index: Index.GlobalIndex): Result<Address.Global, InvocationError.GlobalAddressLookupFailed> =
    globalAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.GlobalAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.elementAddress(index: Index.ElementIndex): Result<Address.Element, InvocationError.ElementAddressLookupFailed> =
    elemAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.ElementAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.dataAddress(index: Index.DataIndex): Result<Address.Data, InvocationError.DataAddressLookupFailed> =
    dataAddresses.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.DataAddressLookupFailed(index.idx.toInt()))

inline fun ModuleInstance.exportInstance(index: Int): Result<ExportInstance, InvocationError.ExportInstanceLookupFailed> =
    exports.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ExportInstanceLookupFailed(index))

inline fun ModuleInstance.addFunctionAddress(address: Address.Function) = apply { this.functionAddresses.add(address) }

inline fun ModuleInstance.addTableAddress(address: Address.Table) = apply { this.tableAddresses.add(address) }

inline fun ModuleInstance.addMemoryAddress(address: Address.Memory) = apply { this.memAddresses.add(address) }

inline fun ModuleInstance.addTagAddress(address: Address.Tag) = apply { this.tagAddresses.add(address) }

inline fun ModuleInstance.addGlobalAddress(address: Address.Global) = apply { this.globalAddresses.add(address) }

inline fun ModuleInstance.addElementAddress(address: Address.Element) = apply { this.elemAddresses.add(address) }

inline fun ModuleInstance.addDataAddress(address: Address.Data) = apply { this.dataAddresses.add(address) }

inline fun ModuleInstance.addExport(exportInstance: ExportInstance) = apply { this.exports.add(exportInstance) }
