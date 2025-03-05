package io.github.charlietap.chasm.predecoder.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.DefinedType

inline fun ModuleInstance.definedType(index: Index.TypeIndex): Result<DefinedType, InvocationError.FunctionTypeLookupFailed> =
    types.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.FunctionTypeLookupFailed(index.idx))

inline fun ModuleInstance.functionAddress(index: Index.FunctionIndex): Result<Address.Function, InvocationError.FunctionAddressLookupFailed> =
    functionAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.FunctionAddressLookupFailed(index.idx))

inline fun ModuleInstance.tableAddress(index: Index.TableIndex): Result<Address.Table, InvocationError.TableAddressLookupFailed> =
    tableAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.TableAddressLookupFailed(index.idx))

inline fun ModuleInstance.memoryAddress(index: Index.MemoryIndex): Result<Address.Memory, InvocationError.MemoryAddressLookupFailed> =
    memAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.MemoryAddressLookupFailed(index.idx))

inline fun ModuleInstance.tagAddress(index: Index.TagIndex): Result<Address.Tag, InvocationError.TagAddressLookupFailed> =
    tagAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.TagAddressLookupFailed(index.idx))

inline fun ModuleInstance.globalAddress(index: Index.GlobalIndex): Result<Address.Global, InvocationError.GlobalAddressLookupFailed> =
    globalAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.GlobalAddressLookupFailed(index.idx))

inline fun ModuleInstance.elementAddress(index: Index.ElementIndex): Result<Address.Element, InvocationError.ElementAddressLookupFailed> =
    elemAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.ElementAddressLookupFailed(index.idx))

inline fun ModuleInstance.dataAddress(index: Index.DataIndex): Result<Address.Data, InvocationError.DataAddressLookupFailed> =
    dataAddresses.getOrNull(index.idx)?.let(::Ok) ?: Err(InvocationError.DataAddressLookupFailed(index.idx))

inline fun ModuleInstance.exportInstance(index: Int): Result<ExportInstance, InvocationError.ExportInstanceLookupFailed> =
    exports.getOrNull(index)?.let(::Ok) ?: Err(InvocationError.ExportInstanceLookupFailed(index))
