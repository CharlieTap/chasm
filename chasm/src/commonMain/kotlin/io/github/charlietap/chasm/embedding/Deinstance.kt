package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.map
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun deinstance(
    store: Store,
    instance: Instance,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    return deinstance(
        store = store,
        instance = instance,
        destructor = ::LinearMemoryDestructor,
    )
}

internal fun deinstance(
    store: Store,
    instance: Instance,
    destructor: LinearMemoryDestructor,
): ChasmResult<Unit, ChasmError.ExecutionError> {

    val instance = instance.instance
    val store = store.store
    val ref = ReferenceValue.Null(AbstractHeapType.None)

    instance.dataAddresses.forEach { address ->
        store.data(address).map { data ->
            data.bytes.fill(0u)
        }
    }
    instance.dataAddresses.clear()

    instance.elemAddresses.forEach { address ->
        store.element(address).map { element ->
            element.elements.fill(ref)
        }
    }
    instance.elemAddresses.clear()

    instance.globalAddresses.forEach { address ->
        store.global(address).map { global ->
            global.value = ref
        }
    }
    instance.globalAddresses.clear()

    instance.memAddresses.forEach { address ->
        store.memory(address).map { memory ->
            destructor(memory.data)
        }
    }
    instance.memAddresses.clear()

    instance.tableAddresses.forEach { address ->
        store.table(address).map { table ->
            table.elements.fill(ref)
        }
    }
    instance.tableAddresses.clear()

    instance.deallocated = true

    return Success(Unit)
}
