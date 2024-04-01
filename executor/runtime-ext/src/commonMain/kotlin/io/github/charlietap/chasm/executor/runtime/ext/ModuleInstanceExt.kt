@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address

inline fun ModuleInstance.definedType(index: Int): Result<DefinedType, InvocationError.FunctionTypeLookupFailed> =
    types.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.FunctionTypeLookupFailed(index))

inline fun ModuleInstance.functionAddress(index: Int): Result<Address.Function, InvocationError.FunctionAddressLookupFailed> =
    functionAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.FunctionAddressLookupFailed(index))

inline fun ModuleInstance.tableAddress(index: Int): Result<Address.Table, InvocationError.TableAddressLookupFailed> =
    tableAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.TableAddressLookupFailed(index))

inline fun ModuleInstance.memoryAddress(index: Int): Result<Address.Memory, InvocationError.MemoryAddressLookupFailed> =
    memAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.MemoryAddressLookupFailed(index))

inline fun ModuleInstance.globalAddress(index: Int): Result<Address.Global, InvocationError.GlobalAddressLookupFailed> =
    globalAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.GlobalAddressLookupFailed(index))

inline fun ModuleInstance.elementAddress(index: Int): Result<Address.Element, InvocationError.ElementAddressLookupFailed> =
    elemAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ElementAddressLookupFailed(index))

inline fun ModuleInstance.dataAddress(index: Int): Result<Address.Data, InvocationError.DataAddressLookupFailed> =
    dataAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.DataAddressLookupFailed(index))

inline fun ModuleInstance.exportInstance(index: Int): Result<ExportInstance, InvocationError.ExportInstanceLookupFailed> =
    exports.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ExportInstanceLookupFailed(index))

inline fun ModuleInstance.addFunctionAddress(address: Address.Function) = apply { this.functionAddresses.add(address) }

inline fun ModuleInstance.addTableAddress(address: Address.Table) = apply { this.tableAddresses.add(address) }

inline fun ModuleInstance.addMemoryAddress(address: Address.Memory) = apply { this.memAddresses.add(address) }

inline fun ModuleInstance.addGlobalAddress(address: Address.Global) = apply { this.globalAddresses.add(address) }

inline fun ModuleInstance.addElementAddress(address: Address.Element) = apply { this.elemAddresses.add(address) }

inline fun ModuleInstance.addDataAddress(address: Address.Data) = apply { this.dataAddresses.add(address) }

inline fun ModuleInstance.addExport(exportInstance: ExportInstance) = apply { this.exports.add(exportInstance) }
