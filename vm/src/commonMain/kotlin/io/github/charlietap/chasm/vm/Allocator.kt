package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value

interface Allocator<T> {
    fun alloc(size: T): T

    fun free(address: T)
}

class Wasm32Allocator(
    private val virtualMachine: WasmVirtualMachine,
    private val store: Store,
    private val instance: Instance,
    private val allocFunction: String,
    private val freeFunction: String,
) : Allocator<Int> {
    override fun alloc(size: Int): Int {
        val result = virtualMachine.functionInvoke(store, instance, allocFunction, listOf(Value.I32(size)))
        return result.expectFirstInt("Failed to allocate $size bytes using function $allocFunction")
    }

    override fun free(address: Int) {
        val result = virtualMachine.functionInvoke(store, instance, freeFunction, listOf(Value.I32(address)))
        result.expect("Failed to free address $address using function $freeFunction")
    }
}
