package io.github.charlietap.chasm.vm

actual typealias StoreReference = Unit
actual typealias ModuleReference = WasmModule
actual typealias InstanceReference = WasmInstance
actual typealias FunctionReference = WasmFunction
actual typealias GlobalReference = WasmGlobal
actual typealias MemoryReference = WasmMemory
actual typealias TableReference = WasmTable

actual fun virtualMachineFactory(): WasmVirtualMachine {
    return JsVirtualMachine
}
