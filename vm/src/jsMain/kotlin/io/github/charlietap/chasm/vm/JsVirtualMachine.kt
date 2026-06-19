package io.github.charlietap.chasm.vm

object JsVirtualMachine : WasmVirtualMachine {

    override fun storeInit(): Store {
        return WebVirtualMachine.storeInit()
    }

    override fun moduleDecode(binary: ByteArray): WasmVirtualMachine.Result<Module> {
        return WebVirtualMachine.moduleDecode(binary)
    }

    override fun moduleInstantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): WasmVirtualMachine.Result<Instance> {
        return WebVirtualMachine.moduleInstantiate(store, module, imports)
    }

    override fun allocateFunction(
        store: Store,
        type: FunctionType,
        function: HostFunction,
    ): WasmVirtualMachine.Result<ExternalAddress.Function> {
        return WebVirtualMachine.allocateFunction(store, type, function)
    }

    override fun exportFunction(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Function> {
        return WebVirtualMachine.exportFunction(instance, name)
    }

    override fun exportGlobal(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Global> {
        return WebVirtualMachine.exportGlobal(instance, name)
    }

    override fun exportMemory(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Memory> {
        return WebVirtualMachine.exportMemory(instance, name)
    }

    override fun exportTable(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Table> {
        return WebVirtualMachine.exportTable(instance, name)
    }

    override fun functionInvoke(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<WasmVirtualMachine.Value>,
    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> {
        return WebVirtualMachine.functionInvoke(store, instance, functionName, args)
    }

    override fun functionInvokeTyped(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<WasmVirtualMachine.Value>,
        resultTypes: List<ValueType>,
    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> {
        return WebVirtualMachine.functionInvokeTyped(store, instance, functionName, args, resultTypes)
    }

    override fun globalRead(
        store: Store,
        global: Global,
    ): WasmVirtualMachine.Result<WasmVirtualMachine.Value> {
        return WebVirtualMachine.globalRead(store, global)
    }

    override fun globalWrite(
        store: Store,
        global: Global,
        value: WasmVirtualMachine.Value,
    ): WasmVirtualMachine.Result<Unit> {
        return WebVirtualMachine.globalWrite(store, global, value)
    }

    override fun memoryReadBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytesToRead: Int,
        buffer: ByteArray,
        bufferPointer: Int,
    ): WasmVirtualMachine.Result<ByteArray> {
        return WebVirtualMachine.memoryReadBytes(store, memory, pointer, bytesToRead, buffer, bufferPointer)
    }

    override fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytes: ByteArray,
    ): WasmVirtualMachine.Result<Unit> {
        return WebVirtualMachine.memoryWriteBytes(store, memory, pointer, bytes)
    }

    override fun memoryReadUtf8NullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): WasmVirtualMachine.Result<String> {
        return WebVirtualMachine.memoryReadUtf8NullTerminatedUtf8String(store, memory, pointer)
    }
}
