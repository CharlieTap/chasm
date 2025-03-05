package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.DefinedType

inline fun ModuleInstance.definedType(
    index: Index.TypeIndex,
): DefinedType = try {
    types[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.FunctionTypeLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.FunctionTypeLookupFailed(index.idx))
}

inline fun ModuleInstance.functionAddress(
    index: Index.FunctionIndex,
): Address.Function = try {
    functionAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.FunctionAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.FunctionAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.tableAddress(
    index: Index.TableIndex,
): Address.Table = try {
    tableAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TableAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.TableAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.memoryAddress(
    index: Index.MemoryIndex,
): Address.Memory = try {
    memAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.MemoryAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.MemoryAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.tagAddress(
    index: Index.TagIndex,
): Address.Tag = try {
    tagAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TagAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.TagAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.globalAddress(
    index: Index.GlobalIndex,
): Address.Global = try {
    globalAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.GlobalAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.GlobalAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.elementAddress(
    index: Index.ElementIndex,
): Address.Element = try {
    elemAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ElementAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ElementAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.dataAddress(
    index: Index.DataIndex,
): Address.Data = try {
    dataAddresses[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.DataAddressLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.DataAddressLookupFailed(index.idx))
}

inline fun ModuleInstance.exportInstance(
    index: Int,
): ExportInstance = try {
    exports[index]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ExportInstanceLookupFailed(index))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ExportInstanceLookupFailed(index))
}
