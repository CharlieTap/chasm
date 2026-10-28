package io.github.charlietap.chasm.executor.instantiator.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

inline fun ModuleInstance.functionAddress(
    index: Int,
): Result<Address.Function, InvocationError.FunctionAddressLookupFailed> =
    functionAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.FunctionAddressLookupFailed(index))

inline fun ModuleInstance.tableAddress(
    index: Int,
): Result<Address.Table, InvocationError.TableAddressLookupFailed> =
    tableAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.TableAddressLookupFailed(index))

inline fun ModuleInstance.memoryAddress(
    index: Int,
): Result<Address.Memory, InvocationError.MemoryAddressLookupFailed> =
    memAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.MemoryAddressLookupFailed(index))

inline fun ModuleInstance.tagAddress(
    index: Int,
): Result<Address.Tag, InvocationError.TagAddressLookupFailed> =
    tagAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.TagAddressLookupFailed(index))

inline fun ModuleInstance.globalAddress(
    index: Int,
): Result<Address.Global, InvocationError.GlobalAddressLookupFailed> =
    globalAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.GlobalAddressLookupFailed(index))

inline fun ModuleInstance.elementAddress(
    index: Int,
): Result<Address.Element, InvocationError.ElementAddressLookupFailed> =
    elemAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ElementAddressLookupFailed(index))

inline fun ModuleInstance.dataAddress(
    index: Int,
): Result<Address.Data, InvocationError.DataAddressLookupFailed> =
    dataAddresses.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.DataAddressLookupFailed(index))

inline fun ModuleInstance.exportInstance(index: Int): Result<ExportInstance, InvocationError.ExportInstanceLookupFailed> =
    exports.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ExportInstanceLookupFailed(index))
