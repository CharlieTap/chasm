package io.github.charlietap.chasm.vm

internal object WebVirtualMachine : WasmVirtualMachine {

    override fun storeInit(): Store {
        return Store(webStoreReference())
    }

    override fun moduleDecode(binary: ByteArray): WasmVirtualMachine.Result<Module> = webCatch {
        Module(webModule(binary))
    }

    override fun moduleInstantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): WasmVirtualMachine.Result<Instance> = webCatch {
        Instance(webInstance(module.reference, buildImports(imports)))
    }

    override fun allocateFunction(
        store: Store,
        type: FunctionType,
        function: HostFunction,
    ): WasmVirtualMachine.Result<ExternalAddress.Function> = webCatch {
        val paramTypes = type.params.map(ValueType::toWebValType)
        val resultTypes = type.results.map(ValueType::toWebValType)
        val address = webHostFunction(paramTypes, resultTypes) { args ->
            invokeHostFunction(function, args, paramTypes, resultTypes)
        }
        ExternalAddress.Function(address)
    }

    override fun exportFunction(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Function> = webCatch {
        val exported = export(instance, name)
        if (!webIsFunction(exported)) {
            throw IllegalArgumentException("Export '$name' is not a function")
        }
        Function(webFunctionReference(exported))
    }

    override fun exportGlobal(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Global> = webCatch {
        val exported = export(instance, name)
        if (!webIsGlobal(exported)) {
            throw IllegalArgumentException("Export '$name' is not a global")
        }
        Global(webGlobalReference(exported))
    }

    override fun exportMemory(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Memory> = webCatch {
        val exported = export(instance, name)
        if (!webIsMemory(exported)) {
            throw IllegalArgumentException("Export '$name' is not a memory")
        }
        Memory(webMemoryReference(exported))
    }

    override fun exportTable(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Table> = webCatch {
        val exported = export(instance, name)
        if (!webIsTable(exported)) {
            throw IllegalArgumentException("Export '$name' is not a table")
        }
        Table(webTableReference(exported))
    }

    override fun functionInvoke(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<WasmVirtualMachine.Value>,
    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> {
        return WasmVirtualMachine.Result.Error("functionInvoke is unsupported on web targets; use functionInvokeTyped with explicit result types")
    }

    override fun functionInvokeTyped(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<WasmVirtualMachine.Value>,
        resultTypes: List<ValueType>,
    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> = webCatch {
        val exported = export(instance, functionName)
        if (!webIsFunction(exported)) {
            throw IllegalArgumentException("Export '$functionName' is not a function")
        }

        val webArgs = webArrayOf(args.map(WebValueMapper::from))
        val results = webApply(exported, webUndefined(), webArgs)

        if (webIsNullOrUndefined(results)) {
            if (resultTypes.isNotEmpty()) {
                throw IllegalArgumentException("Function '$functionName' returned no values but ${resultTypes.size} result types were provided")
            }
            return@webCatch emptyList()
        }

        val nonNullResults = results ?: return@webCatch emptyList()
        WebValueMapper.mapWasmValuesUsingTypes(nonNullResults, resultTypes)
    }

    override fun globalRead(
        store: Store,
        global: Global,
    ): WasmVirtualMachine.Result<WasmVirtualMachine.Value> = webCatch {
        WebValueMapper.to(webGlobalValue(global.reference))
    }

    override fun globalWrite(
        store: Store,
        global: Global,
        value: WasmVirtualMachine.Value,
    ): WasmVirtualMachine.Result<Unit> = webCatch {
        webSetGlobalValue(global.reference, WebValueMapper.from(value))
    }

    override fun memoryReadBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytesToRead: Int,
        buffer: ByteArray,
        bufferPointer: Int,
    ): WasmVirtualMachine.Result<ByteArray> = webCatch {
        val view = webMemoryView(memory.reference, pointer, bytesToRead)
        for (index in 0 until bytesToRead) {
            buffer[bufferPointer + index] = webReadByte(view, index).toByte()
        }
        buffer
    }

    override fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytes: ByteArray,
    ): WasmVirtualMachine.Result<Unit> = webCatch {
        val view = webMemoryView(memory.reference, pointer, bytes.size)
        for (index in bytes.indices) {
            webWriteByte(view, index, bytes[index].toInt() and 0xFF)
        }
    }

    override fun memoryReadUtf8NullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): WasmVirtualMachine.Result<String> = webCatch {
        val view = webFullMemoryView(memory.reference)
        val limit = webByteViewLength(view)

        var index = pointer
        var length = -1

        while (index + 7 < limit) {
            var found = false
            var inner = 0
            while (inner < 8) {
                val byte = webReadByte(view, index + inner)
                if (byte == 0) {
                    length = index + inner - pointer
                    found = true
                    break
                }
                inner++
            }
            if (found) break
            index += 8
        }

        if (length == -1) {
            var i = index
            while (i < limit) {
                val byte = webReadByte(view, i)
                if (byte == 0) {
                    length = i - pointer
                    break
                }
                i++
            }
        }

        if (length == -1) return@webCatch ""

        val bytes = ByteArray(length)
        var i = 0
        while (i < length) {
            bytes[i] = webReadByte(view, pointer + i).toByte()
            i++
        }
        bytes.decodeToString()
    }

    private fun buildImports(imports: List<Import>): WebObject {
        val root = webNewObject()

        for (import in imports) {
            val moduleName = import.moduleName
            val entityName = import.entityName

            val moduleObj = webGetObject(root, moduleName) ?: webNewObject().also { obj ->
                webSetObject(root, moduleName, obj)
            }

            webSetValue(moduleObj, entityName, externalAddressValue(import.address))
        }

        return root
    }

    private fun export(
        instance: Instance,
        name: String,
    ): WebValue {
        return webGetValue(webExports(instance.reference), name)
            ?: throw IllegalArgumentException("Export '$name' does not exist")
    }

    private fun externalAddressValue(address: ExternalAddress): WebValue = when (address) {
        is ExternalAddress.Function -> webFunctionExternalAddressValue(address.address)
        is ExternalAddress.Global -> webGlobalExternalAddressValue(address.address)
        is ExternalAddress.Memory -> webMemoryExternalAddressValue(address.address)
        is ExternalAddress.Table -> webTableExternalAddressValue(address.address)
        is ExternalAddress.Tag -> webTagExternalAddressValue(address.address)
    }

    private fun invokeHostFunction(
        function: HostFunction,
        args: List<WebValue>,
        paramTypes: List<String>,
        resultTypes: List<String>,
    ): WebValue? {
        val params = args.mapIndexed { index, param ->
            WebValueMapper.toWithType(param, paramTypes[index])
        }

        val results = function(params)
        return when (resultTypes.size) {
            0 -> null
            1 -> WebValueMapper.from(results.first())
            else -> webArrayValue(
                webArrayOf(
                    results.map(WebValueMapper::from),
                ),
            )
        }
    }
}
