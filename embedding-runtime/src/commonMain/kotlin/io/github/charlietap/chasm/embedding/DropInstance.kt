package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.table

fun dropInstance(
    store: Store,
    instance: Instance,
) {

    require(instance.store === store.store) { "Instance belongs to a different Store" }
    check(!store.isDropped) { InvocationError.StoreDropped.toString() }

    store.diagnostics.unregister(instance.instance)

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
            store.referenceStorage?.release(element)
        }
    }
    instance.elemAddresses.clear()

    instance.exports.clear()

    instance.functionAddresses.clear()

    instance.globalAddresses.forEach { address ->
        store.global(address).value = 0L
        store.referenceStorage?.release(store.global(address))
    }
    instance.globalAddresses.clear()

    instance.memAddresses.clear()

    instance.tableAddresses.forEach { address ->
        store.table(address).let { table ->
            table.elements = longArrayOf()
            store.referenceStorage?.release(table)
        }
    }
    instance.tableAddresses.clear()

    instance.tagAddresses.clear()
    instance.deallocated = true
}
