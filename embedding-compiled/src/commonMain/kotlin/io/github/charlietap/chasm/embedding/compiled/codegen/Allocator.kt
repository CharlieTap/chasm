package io.github.charlietap.chasm.embedding.compiled.codegen

import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.prepareFunction
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue
import io.github.charlietap.chasm.embedding.shapes.expect

interface Allocator<T> {
    fun alloc(size: T): T

    fun free(address: T)
}

class Wasm32Allocator(
    instance: Instance,
    store: Store,
    allocFunction: String,
    freeFunction: String,
) : Allocator<Int> {

    private val preparedAllocFunction = prepareFunction(store, instance, allocFunction)
        .expect("Failed to prepare allocation function $allocFunction")

    private val preparedFreeFunction = prepareFunction(store, instance, freeFunction)
        .expect("Failed to prepare deallocation function $freeFunction")

    override fun alloc(size: Int): Int {

        val result = preparedAllocFunction(listOf(NumberValue.I32(size)))

        return (result.expect("Failed to allocate $size bytes").first() as NumberValue.I32).value
    }

    override fun free(address: Int) {
        preparedFreeFunction(listOf(NumberValue.I32(address))).expect("Failed to free address $address")
    }
}
