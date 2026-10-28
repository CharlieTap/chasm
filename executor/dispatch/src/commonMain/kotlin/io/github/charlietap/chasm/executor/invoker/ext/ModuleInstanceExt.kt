package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

inline fun ModuleInstance.functionAddress(
    index: Index.FunctionIndex,
): Address.Function = try {
    functionAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.FunctionAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.FunctionAddressLookupFailed(index.toInt()))
}

inline fun ModuleInstance.tableAddress(
    index: Index.TableIndex,
): Address.Table = try {
    tableAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TableAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.TableAddressLookupFailed(index.toInt()))
}

inline fun ModuleInstance.memoryAddress(
    index: Index.MemoryIndex,
): Address.Memory = try {
    memAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.MemoryAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.MemoryAddressLookupFailed(index.toInt()))
}

inline fun ModuleInstance.tagAddress(
    index: Index.TagIndex,
): Address.Tag = try {
    tagAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TagAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.TagAddressLookupFailed(index.toInt()))
}

inline fun ModuleInstance.globalAddress(
    index: Index.GlobalIndex,
): Address.Global = try {
    globalAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.GlobalAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.GlobalAddressLookupFailed(index.toInt()))
}

inline fun ModuleInstance.elementAddress(
    index: Index.ElementIndex,
): Address.Element = try {
    elemAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ElementAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ElementAddressLookupFailed(index.toInt()))
}

inline fun ModuleInstance.dataAddress(
    index: Index.DataIndex,
): Address.Data = try {
    dataAddresses[index.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.DataAddressLookupFailed(index.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.DataAddressLookupFailed(index.toInt()))
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
