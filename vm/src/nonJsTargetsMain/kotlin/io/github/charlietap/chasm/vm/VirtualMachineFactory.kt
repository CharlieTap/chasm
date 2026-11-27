package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table

internal actual typealias StoreReference = Store
internal actual typealias ModuleReference = Module
internal actual typealias InstanceReference = Instance
internal actual typealias FunctionReference = Function
internal actual typealias GlobalReference = Global
internal actual typealias MemoryReference = Memory
internal actual typealias TableReference = Table

actual fun virtualMachineFactory(): WasmVirtualMachine {
    return NonJsVirtualMachine
}
