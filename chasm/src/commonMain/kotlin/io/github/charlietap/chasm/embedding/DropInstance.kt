package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.table

fun dropInstance(
    store: Store,
    instance: Instance,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    if (instance.store !== store.store) {
        return Error(ChasmError.ExecutionError("Instance belongs to a different Store"))
    }

    val instance = instance.instance
    val store = store.store

    instance.dataAddresses.forEach { address ->
        store.data(address).let { data ->
            data.bytes = ubyteArrayOf()
        }
    }
    instance.dataAddresses.clear()

    instance.elemAddresses.forEach { address ->
        store.element(address).let { element ->
            element.elements = longArrayOf()
        }
    }
    instance.elemAddresses.clear()

    instance.exports.clear()

    instance.functionAddresses.clear()

    instance.globalAddresses.forEach { address ->
        store.global(address).value = 0L
    }
    instance.globalAddresses.clear()

    instance.memAddresses.clear()

    instance.tableAddresses.forEach { address ->
        store.table(address).let { table ->
            table.elements = longArrayOf()
        }
    }
    instance.tableAddresses.clear()

    instance.tagAddresses.clear()
    instance.deallocated = true

    return Success(Unit)
}
