package io.github.charlietap.chasm.embedding.ext

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal fun Store.runtime(): RuntimeStore {
    if (isDropped) throw InvocationException(InvocationError.StoreDropped)
    return store
}

internal fun Store.owning(
    owner: RuntimeStore,
): RuntimeStore {
    val runtime = runtime()
    if (owner !== runtime) throw InvocationException(InvocationError.ForeignHandle)
    return runtime
}

internal fun Store.memory(
    memory: Memory,
): MemoryInstance = owning(memory.store).memory(memory.reference.address)

internal fun Store.global(
    global: Global,
): GlobalInstance = owning(global.store).global(global.reference.address)

internal fun Store.table(
    table: Table,
): TableInstance = owning(table.store).table(table.reference.address)
