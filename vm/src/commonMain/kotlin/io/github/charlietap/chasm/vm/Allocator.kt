package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value

private val i32ResultTypes = listOf(ValueType.Number(NumberType.I32))

interface Allocator<T> {
    fun alloc(size: T): T

    fun free(address: T)
}

class Wasm32Allocator(
    private val virtualMachine: WasmVirtualMachine,
    private val store: Store,
    private val instance: Instance,
    allocFunction: String,
    freeFunction: String,
) : Allocator<Int> {
    private val preparedAllocFunction = virtualMachine.prepareFunction(store, instance, allocFunction, i32ResultTypes)
        .expect("Failed to prepare allocation function $allocFunction")
    private val preparedFreeFunction = virtualMachine.prepareFunction(store, instance, freeFunction, emptyList())
        .expect("Failed to prepare deallocation function $freeFunction")

    override fun alloc(size: Int): Int {
        val result = preparedAllocFunction(listOf(Value.I32(size)))
        return result.expectFirstInt("Failed to allocate $size bytes")
    }

    override fun free(address: Int) {
        val result = preparedFreeFunction(listOf(Value.I32(address)))
        result.expect("Failed to free address $address")
    }
}
