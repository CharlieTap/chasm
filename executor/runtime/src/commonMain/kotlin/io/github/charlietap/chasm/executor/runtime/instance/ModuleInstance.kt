@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.instance

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
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
    inline fun functionType(index: Int): Result<FunctionType, InvocationError.FunctionTypeLookupFailed> =
        types.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.FunctionTypeLookupFailed(index))

    inline fun functionAddress(index: Int): Result<Address.Function, InvocationError.FunctionAddressLookupFailed> =
        functionAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.FunctionAddressLookupFailed(index))

    inline fun tableAddress(index: Int): Result<Address.Table, InvocationError.TableAddressLookupFailed> =
        tableAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.TableAddressLookupFailed(index))

    inline fun memoryAddress(index: Int): Result<Address.Memory, InvocationError.MemoryAddressLookupFailed> =
        memAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.MemoryAddressLookupFailed(index))

    inline fun globalAddress(index: Int): Result<Address.Global, InvocationError.GlobalAddressLookupFailed> =
        globalAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.GlobalAddressLookupFailed(index))

    inline fun elementAddress(index: Int): Result<Address.Element, InvocationError.ElementAddressLookupFailed> =
        elemAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ElementAddressLookupFailed(index))

    inline fun dataAddress(index: Int): Result<Address.Data, InvocationError.DataAddressLookupFailed> =
        dataAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.DataAddressLookupFailed(index))

    inline fun exportInstance(index: Int): Result<ExportInstance, InvocationError.ExportInstanceLookupFailed> =
        exports.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ExportInstanceLookupFailed(index))

    inline fun addFunctionAddress(address: Address.Function) = apply { this.functionAddresses.add(address) }

    inline fun addTableAddress(address: Address.Table) = apply { this.tableAddresses.add(address) }

    inline fun addMemoryAddress(address: Address.Memory) = apply { this.memAddresses.add(address) }

    inline fun addGlobalAddress(address: Address.Global) = apply { this.globalAddresses.add(address) }

    inline fun addElementAddress(address: Address.Element) = apply { this.elemAddresses.add(address) }

    inline fun addDataAddress(address: Address.Data) = apply { this.dataAddresses.add(address) }

    inline fun addExport(exportInstance: ExportInstance) = apply { this.exports.add(exportInstance) }
}
