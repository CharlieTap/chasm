package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.invokeUnboxed
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect

interface Allocator<T> {

    fun alloc(size: T): T

    fun free(address: T)
}

@OptIn(ExperimentalChasmApi::class)
class Wasm32Allocator(
    instance: Instance,
    allocFunction: String,
    freeFunction: String,
) : Allocator<Int> {

    private val preparedAllocFunction = prepareFunction(instance, allocFunction)
        .expect("Failed to prepare allocation function $allocFunction")

    private val preparedFreeFunction = prepareFunction(instance, freeFunction)
        .expect("Failed to prepare deallocation function $freeFunction")

    private val argumentWords = LongArray(1)

    private val argumentReferences = arrayOfNulls<Any>(1)

    override fun alloc(size: Int): Int {

        argumentWords[0] = size.toLong()
        preparedAllocFunction.invokeUnboxed(argumentWords, argumentReferences).expect("Failed to allocate $size bytes")

        return argumentWords[0].toInt()
    }

    override fun free(address: Int) {
        argumentWords[0] = address.toLong()
        preparedFreeFunction.invokeUnboxed(argumentWords, argumentReferences).expect("Failed to free address $address")
    }
}
