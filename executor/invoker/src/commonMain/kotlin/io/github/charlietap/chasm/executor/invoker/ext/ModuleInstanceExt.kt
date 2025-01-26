package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address

inline fun ModuleInstance.definedType(
    index: Index.TypeIndex,
): DefinedType = try {
    types[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.FunctionTypeLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.FunctionTypeLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.functionAddress(
    index: Index.FunctionIndex,
): Address.Function = try {
    functionAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.FunctionAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.FunctionAddressLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.tableAddress(
    index: Index.TableIndex,
): Address.Table = try {
    tableAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TableAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.TableAddressLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.memoryAddress(
    index: Index.MemoryIndex,
): Address.Memory = try {
    memAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.MemoryAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.MemoryAddressLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.tagAddress(
    index: Index.TagIndex,
): Address.Tag = try {
    tagAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TagAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.TagAddressLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.globalAddress(
    index: Index.GlobalIndex,
): Address.Global = try {
    globalAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.GlobalAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.GlobalAddressLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.elementAddress(
    index: Index.ElementIndex,
): Address.Element = try {
    elemAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ElementAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ElementAddressLookupFailed(index.idx.toInt()))
}

inline fun ModuleInstance.dataAddress(
    index: Index.DataIndex,
): Address.Data = try {
    dataAddresses[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.DataAddressLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.DataAddressLookupFailed(index.idx.toInt()))
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
