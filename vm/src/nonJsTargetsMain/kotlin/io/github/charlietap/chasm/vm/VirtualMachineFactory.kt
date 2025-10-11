package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table

actual typealias StoreReference = Store
actual typealias ModuleReference = Module
actual typealias InstanceReference = Instance
actual typealias FunctionReference = Function
actual typealias GlobalReference = Global
actual typealias MemoryReference = Memory
actual typealias TableReference = Table

actual fun virtualMachineFactory(): WasmVirtualMachine {
    return NonJsVirtualMachine
}
