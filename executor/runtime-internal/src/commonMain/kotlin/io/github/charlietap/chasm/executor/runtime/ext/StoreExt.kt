package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instance.TagInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

inline fun Store.function(address: Address.Function): FunctionInstance = try {
    functions[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.table(address: Address.Table): TableInstance = try {
    tables[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.memory(address: Address.Memory): MemoryInstance = try {
    memories[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.tag(address: Address.Tag): TagInstance = try {
    tags[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.global(address: Address.Global): GlobalInstance = try {
    globals[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.element(address: Address.Element): ElementInstance = try {
    elements[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.data(address: Address.Data): DataInstance = try {
    data[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.exception(address: Address.Exception): ExceptionInstance = try {
    exceptions[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}

inline fun Store.instruction(address: Address.Function): DispatchableInstruction = try {
    instructions[address.address]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StoreLookupFailed(address))
}
