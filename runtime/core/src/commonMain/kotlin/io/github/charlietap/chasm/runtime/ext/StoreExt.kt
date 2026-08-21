@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.InvocationError.DataLookupFailed
import io.github.charlietap.chasm.runtime.error.InvocationError.ElementLookupFailed
import io.github.charlietap.chasm.runtime.error.InvocationError.FunctionLookupFailed
import io.github.charlietap.chasm.runtime.error.InvocationError.GlobalLookupFailed
import io.github.charlietap.chasm.runtime.error.InvocationError.MemoryLookupFailed
import io.github.charlietap.chasm.runtime.error.InvocationError.TableLookupFailed
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.HostInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.store.Store

inline fun Store.function(address: Address.Function): FunctionInstance = try {
    functions[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(FunctionLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(FunctionLookupFailed(address))
}

inline fun Store.table(address: Address.Table): TableInstance = try {
    tables[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(TableLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(TableLookupFailed(address))
}

inline fun Store.memory(address: Address.Memory): MemoryInstance = try {
    memories[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(MemoryLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(MemoryLookupFailed(address))
}

inline fun Store.global(address: Address.Global): GlobalInstance = try {
    globals[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(GlobalLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(GlobalLookupFailed(address))
}

inline fun Store.element(address: Address.Element): ElementInstance = try {
    elements[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(ElementLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(ElementLookupFailed(address))
}

inline fun Store.data(address: Address.Data): DataInstance = try {
    data[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(DataLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(DataLookupFailed(address))
}

inline fun Store.host(address: Address.Host): HostInstance = try {
    hosts[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.HostLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.HostLookupFailed(address))
}

fun Store.allocateHost(
    instance: HostInstance,
): Address.Host {
    hosts.add(instance)

    return Address.Host(hosts.lastIndex)
}
