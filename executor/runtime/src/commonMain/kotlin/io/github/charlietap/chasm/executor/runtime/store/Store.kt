@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance

data class Store(
    val functions: MutableList<FunctionInstance> = mutableListOf(),
    val tables: MutableList<TableInstance> = mutableListOf(),
    val memories: MutableList<MemoryInstance> = mutableListOf(),
    val globals: MutableList<GlobalInstance> = mutableListOf(),
    val elements: MutableList<ElementInstance> = mutableListOf(),
    val data: MutableList<DataInstance> = mutableListOf(),
) {
    inline fun function(address: Address.Function): Result<FunctionInstance, InvocationError.StoreLookupFailed> {
        return functions.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    inline fun table(address: Address.Table): Result<TableInstance, InvocationError.StoreLookupFailed> {
        return tables.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    inline fun memory(address: Address.Memory): Result<MemoryInstance, InvocationError.StoreLookupFailed> {
        return memories.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    inline fun global(address: Address.Global): Result<GlobalInstance, InvocationError.StoreLookupFailed> {
        return globals.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    inline fun element(address: Address.Element): Result<ElementInstance, InvocationError.StoreLookupFailed> {
        return elements.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    inline fun data(address: Address.Data): Result<DataInstance, InvocationError.StoreLookupFailed> {
        return data.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }
}
