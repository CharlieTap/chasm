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
    private val allocFunction: String,
    private val freeFunction: String,
) : Allocator<Int> {
    override fun alloc(size: Int): Int {
        val result = virtualMachine.functionInvokeTyped(store, instance, allocFunction, listOf(Value.I32(size)), i32ResultTypes)
        return result.expectFirstInt("Failed to allocate $size bytes using function $allocFunction")
    }

    override fun free(address: Int) {
        val result = virtualMachine.functionInvokeTyped(store, instance, freeFunction, listOf(Value.I32(address)), emptyList())
        result.expect("Failed to free address $address using function $freeFunction")
    }
}
