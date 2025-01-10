package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
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

inline fun Store.function(address: Address.Function): Result<FunctionInstance, InvocationError.StoreLookupFailed> {
    return functions.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.table(address: Address.Table): Result<TableInstance, InvocationError.StoreLookupFailed> {
    return tables.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.memory(address: Address.Memory): Result<MemoryInstance, InvocationError.StoreLookupFailed> {
    return memories.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.tag(address: Address.Tag): Result<TagInstance, InvocationError.StoreLookupFailed> {
    return tags.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.global(address: Address.Global): Result<GlobalInstance, InvocationError.StoreLookupFailed> {
    return globals.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.element(address: Address.Element): Result<ElementInstance, InvocationError.StoreLookupFailed> {
    return elements.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.data(address: Address.Data): Result<DataInstance, InvocationError.StoreLookupFailed> {
    return data.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.exception(address: Address.Exception): Result<ExceptionInstance, InvocationError.StoreLookupFailed> {
    return exceptions.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}

inline fun Store.instruction(address: Address.Function): Result<DispatchableInstruction, InvocationError.StoreLookupFailed> {
    return instructions.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
}
