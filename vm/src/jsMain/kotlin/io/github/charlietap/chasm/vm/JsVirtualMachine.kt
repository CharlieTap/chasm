package io.github.charlietap.chasm.vm

import org.khronos.webgl.Uint8Array
import kotlin.js.unsafeCast

object JsVirtualMachine : WasmVirtualMachine {

    private val supportsWasmTypeReflection: Boolean by lazy {
        supportsWasmTypeReflection()
    }

    private val hostFunctionAllocator = HostFunctionAllocator()

    override fun storeInit(): Store = Store(Unit)

    override fun moduleDecode(binary: ByteArray): WasmVirtualMachine.Result<Module> = catchJs {
        val buffer = binary.toUint8Array().buffer
        Module(WasmModule(buffer))
    }

    override fun moduleInstantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): WasmVirtualMachine.Result<Instance> = catchJs {
        val jsImports = ImportMapper.build(imports)
        Instance(WasmInstance(module.reference, jsImports))
    }

    override fun allocateFunction(
        store: Store,
        type: FunctionType,
        function: HostFunction,
    ): WasmVirtualMachine.Result<ExternalAddress.Function> = catchJs {
        val allocated = hostFunctionAllocator.allocate(type, function)
        ExternalAddress.Function(allocated)
    }

    override fun exportFunction(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Function> = catchJs {
        val exported = instance.reference.exports[name]
        if (!isFunctionType(exported)) {
            throw IllegalArgumentException("Export '$name' is not a function")
        }
        Function(exported)
    }

    override fun exportGlobal(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Global> = catchJs {
        val exported = instance.reference.exports[name]
        if (!isGlobalType(exported)) {
            throw IllegalArgumentException("Export '$name' is not a global")
        }
        Global(exported)
    }

    override fun exportMemory(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Memory> = catchJs {
        val exported = instance.reference.exports[name]
        if (!isMemoryType(exported)) {
            throw IllegalArgumentException("Export '$name' is not a memory")
        }
        Memory(exported)
    }

    override fun exportTable(
        instance: Instance,
        name: String,
    ): WasmVirtualMachine.Result<Table> = catchJs {
        val exported = instance.reference.exports[name]
        if (!isTableType(exported)) {
            throw IllegalArgumentException("Export '$name' is not a table")
        }
        Table(exported)
    }

    override fun functionInvoke(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<WasmVirtualMachine.Value>,
    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> = catchJs {

        val exported = instance.reference.exports[functionName]
        if (!isFunctionType(exported)) {
            throw IllegalArgumentException("Export '$functionName' is not a function")
        }

        val functionType: WasmFunctionType? = if (supportsWasmTypeReflection) {
            WasmFunction.type(exported)
        } else {
            null
        }

        val args = args.map(ValueMapper::from).toTypedArray()
        val results = exported.apply(UNDEFINED, args)

        if (results === UNDEFINED || results == null) return@catchJs emptyList()

        val types = functionType?.results
        if (types != null) {
            return@catchJs ValueMapper.mapWasmValuesUsingTypes(results, types)
        }

        ValueMapper.mapWasmValuesUsingInference(results)
    }

    override fun globalRead(
        store: Store,
        global: Global,
    ): WasmVirtualMachine.Result<WasmVirtualMachine.Value> = catchJs {
        ValueMapper.to(global.reference.value)
    }

    override fun globalWrite(
        store: Store,
        global: Global,
        value: WasmVirtualMachine.Value,
    ) = catchJs {
        global.reference.value = ValueMapper.from(value)
    }

    override fun memoryReadBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytesToRead: Int,
        buffer: ByteArray,
        bufferPointer: Int,
    ): WasmVirtualMachine.Result<ByteArray> = catchJs {
        val view = Uint8Array(memory.reference.buffer, pointer, bytesToRead)
        for (index in 0 until bytesToRead) {
            val value = view.asDynamic()[index] as Int
            buffer[bufferPointer + index] = value.toByte()
        }
        buffer
    }

    override fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytes: ByteArray,
    ): WasmVirtualMachine.Result<Unit> = catchJs {
        val view = Uint8Array(memory.reference.buffer, pointer, bytes.size)
        for (index in bytes.indices) {
            view.asDynamic()[index] = bytes[index].toInt() and 0xFF
        }
    }

    override fun memoryReadUtf8NullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): WasmVirtualMachine.Result<String> = catchJs {
        val u8 = Uint8Array(memory.reference.buffer)
        val limit = u8.length

        var index = pointer
        var length = -1

        while (index + 7 < limit) {
            var found = false
            var inner = 0
            while (inner < 8) {
                val byte = (u8.asDynamic()[index + inner] as Int)
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
                val byte = (u8.asDynamic()[i] as Int)
                if (byte == 0) {
                    length = i - pointer
                    break
                }
                i++
            }
        }

        if (length == -1) return@catchJs ""
        val bytes = ByteArray(length)
        var i = 0
        while (i < length) {
            bytes[i] = (u8.asDynamic()[pointer + i] as Int).toByte()
            i++
        }
        bytes.decodeToString()
    }
}

internal fun ValueType.toJsValType(): String = when (this) {
    is ValueType.Number -> when (this.numberType) {
        NumberType.I32 -> "i32"
        NumberType.I64 -> "i64"
        NumberType.F32 -> "f32"
        NumberType.F64 -> "f64"
    }
    is ValueType.Vector -> when (this.vectorType) {
        VectorType.V128 -> "v128"
    }
    is ValueType.Reference -> throw IllegalArgumentException("JS host function types do not support reference params/results yet: $this")
}

private inline fun <T> catchJs(block: () -> T): WasmVirtualMachine.Result<T> =
    try {
        WasmVirtualMachine.Result.Ok(block())
    } catch (error: dynamic) {
        WasmVirtualMachine.Result.Error(errorMessage(error))
    }

private fun ByteArray.toUint8Array(): Uint8Array {
    val array = Uint8Array(size)
    for (index in indices) {
        array.asDynamic()[index] = this[index].toInt() and 0xFF
    }
    return array
}

private fun errorMessage(error: dynamic): String {
    if (error == null || error === UNDEFINED) return "Unknown JS error"
    val message = error.message
    return when {
        message !== UNDEFINED && message != null -> message.toString()
        else -> error.toString()
    }
}

private fun supportsWasmTypeReflection(): Boolean {
    return js(
        "typeof WebAssembly !== 'undefined' && WebAssembly.Function && typeof WebAssembly.Function.type === 'function'",
    ).unsafeCast<Boolean>()
}

private fun isFunctionType(value: dynamic): Boolean {
    return if (supportsWasmTypeReflection()) {
        js("value instanceof WebAssembly.Function").unsafeCast<Boolean>()
    } else {
        typeOf(value) == "function"
    }
}

private fun isGlobalType(value: dynamic): Boolean = js("value instanceof WebAssembly.Global").unsafeCast<Boolean>()

private fun isMemoryType(value: dynamic): Boolean = js("value instanceof WebAssembly.Memory").unsafeCast<Boolean>()

private fun isTableType(value: dynamic): Boolean = js("value instanceof WebAssembly.Table").unsafeCast<Boolean>()
