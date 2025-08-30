package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.runtime.value.NumberValue

interface Allocator<T> {
    fun alloc(size: T): T

    fun free(address: T)
}

class Wasm32Allocator(
    private val instance: Instance,
    private val store: Store,
    private val allocFunction: String,
    private val freeFunction: String,
) : Allocator<Int> {
    override fun alloc(size: Int): Int {
        val result = invoke(store, instance, allocFunction, listOf(NumberValue.I32(size)))
        val expected = result.expect("Failed to allocate $size bytes using function $allocFunction")
        return (expected.first() as NumberValue.I32).value
    }

    override fun free(address: Int) {
        val result = invoke(store, instance, freeFunction, listOf(NumberValue.I32(address)))
        result.expect("Failed to free address $address using function $freeFunction")
    }
}
