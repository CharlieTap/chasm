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
    fun function(address: Address.Function): Result<FunctionInstance, InvocationError> {
        return functions.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    fun table(address: Address.Table): TableInstance? = tables.getOrNull(address.address)

    fun memory(address: Address.Memory): MemoryInstance? = memories.getOrNull(address.address)

    fun global(address: Address.Global): Result<GlobalInstance, InvocationError> {
        return globals.getOrNull(address.address)?.let(::Ok) ?: Err(InvocationError.StoreLookupFailed(address))
    }

    fun element(address: Address.Element): ElementInstance? = elements.getOrNull(address.address)

    fun data(address: Address.Data): DataInstance? = data.getOrNull(address.address)
}
