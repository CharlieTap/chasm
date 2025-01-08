package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.drop.MemoryInstanceDropper
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun dropStore(
    store: Store,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    return dropStore(
        store = store,
        memoryDropper = ::MemoryInstanceDropper,
    )
}

internal fun dropStore(
    store: Store,
    memoryDropper: MemoryInstanceDropper,
): ChasmResult<Unit, ChasmError.ExecutionError> {

    val store = store.store

    store.data.forEach { data ->
        data.bytes = ubyteArrayOf()
    }
    store.data.clear()

    store.exceptions.forEach { exception ->
        exception.fields = emptyList()
    }
    store.exceptions.clear()

    store.elements.forEach { element ->
        element.elements = arrayOf()
    }
    store.elements.clear()

    store.functions.clear()

    val uninit = ExecutionValue.Uninitialised
    store.globals.forEach { global ->
        global.value = uninit
    }
    store.globals.clear()

    store.memories.forEach { memory ->
        memoryDropper(memory)
    }
    store.memories.clear()

    store.tables.forEach { table ->
        table.elements = arrayOf()
    }
    store.tables.clear()

    store.tags.clear()

    return Success(Unit)
}
