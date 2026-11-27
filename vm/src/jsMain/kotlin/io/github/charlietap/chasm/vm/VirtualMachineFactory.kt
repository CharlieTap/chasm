package io.github.charlietap.chasm.vm

internal actual typealias StoreReference = Unit
internal actual typealias ModuleReference = WasmModule
internal actual typealias InstanceReference = WasmInstance
internal actual typealias FunctionReference = WasmFunction
internal actual typealias GlobalReference = WasmGlobal
internal actual typealias MemoryReference = WasmMemory
internal actual typealias TableReference = WasmTable

actual fun virtualMachineFactory(): WasmVirtualMachine {
    return JsVirtualMachine
}
