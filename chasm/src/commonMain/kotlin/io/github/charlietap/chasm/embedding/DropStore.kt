package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.drop.MemoryInstanceDropper

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
        exception.fields = longArrayOf()
    }
    store.exceptions.clear()

    store.elements.forEach { element ->
        element.elements = longArrayOf()
    }
    store.elements.clear()

    store.functions.clear()

    store.globals.forEach { global ->
        global.value = 0L
    }
    store.globals.clear()

    store.memories.forEach { memory ->
        memoryDropper(memory)
    }
    store.memories.clear()

    store.tables.forEach { table ->
        table.elements = longArrayOf()
    }
    store.tables.clear()

    store.tags.clear()

    return Success(Unit)
}
