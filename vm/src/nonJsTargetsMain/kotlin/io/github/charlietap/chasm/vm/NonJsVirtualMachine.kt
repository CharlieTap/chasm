package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.dsl.hostFunction
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.readNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result.Ok
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value
import io.github.charlietap.chasm.embedding.shapes.Function as ChasmFunction
import io.github.charlietap.chasm.embedding.shapes.Global as ChasmGlobal
import io.github.charlietap.chasm.embedding.shapes.Memory as ChasmMemory
import io.github.charlietap.chasm.embedding.shapes.Table as ChasmTable

object NonJsVirtualMachine : WasmVirtualMachine {

    object ResultFactory {
        fun <T, E : ChasmError> new(result: ChasmResult<T, E>): Result<T> {
            return when (result) {
                is ChasmResult.Success -> Result.Ok(result.result)
                is ChasmResult.Error -> Result.Error(result.error.error)
            }
        }
    }

    override fun storeInit(): Store {
        return Store(store())
    }

    override fun moduleDecode(binary: ByteArray): Result<Module> {
        val module = module(binary).expect("Failed to decode module")
        return Ok(Module(module))
    }

    override fun moduleInstantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Result<Instance> {
        val mappedImports = imports.map(ImportMapper::from)
        val instance = instance(store.reference, module.reference, mappedImports).expect("Failed to instantiate module")
        return Ok(Instance(instance))
    }

    override fun allocateFunction(
        store: Store,
        type: FunctionType,
        function: HostFunction,
    ): Result<ExternalAddress.Function> {

        val functionType = FunctionTypeMapper.from(type)

        val hostFunction = hostFunction { params ->
            val mappedParams = params.map(ValueMapper::to)
            val results = function(mappedParams)
            results.map(ValueMapper::from)
        }

        val result = function(store.reference, functionType, hostFunction)
        return Ok(ExternalAddress.Function(result))
    }

    override fun exportFunction(
        instance: Instance,
        name: String,
    ): Result<Function> {
        val export = instance.reference.exports.firstOrNull { it.name == name }
        val function = (export?.value as? ChasmFunction)?.let(::Function)
        return function?.let(::Ok) ?: Result.Error("Failed to find function export with name $name")
    }

    override fun exportGlobal(
        instance: Instance,
        name: String,
    ): Result<Global> {
        val export = instance.reference.exports.firstOrNull { it.name == name }
        val global = (export?.value as? ChasmGlobal)?.let(::Global)
        return global?.let(::Ok) ?: Result.Error("Failed to find global export with name $name")
    }

    override fun exportMemory(
        instance: Instance,
        name: String,
    ): Result<Memory> {
        val export = instance.reference.exports.firstOrNull { it.name == name }
        val memory = (export?.value as? ChasmMemory)?.let(::Memory)
        return memory?.let(::Ok) ?: Result.Error("Failed to find memory export with name $name")
    }

    override fun exportTable(
        instance: Instance,
        name: String,
    ): Result<Table> {
        val export = instance.reference.exports.firstOrNull { it.name == name }
        val table = (export?.value as? ChasmTable)?.let(::Table)
        return table?.let(::Ok) ?: Result.Error("Failed to find table export with name $name")
    }

    override fun functionInvoke(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<Value>,
    ): Result<List<Value>> {
        return invoke(store.reference, instance.reference, functionName, args.map(ValueMapper::from)).map { values ->
            values.map(ValueMapper::to)
        }.let(ResultFactory::new)
    }

    override fun globalRead(
        store: Store,
        global: Global,
    ): Result<Value> {
        val result = readGlobal(store.reference, global.reference).map(ValueMapper::to)
        return ResultFactory.new(result)
    }

    override fun globalWrite(
        store: Store,
        global: Global,
        value: Value,
    ): Result<Unit> {
        val result = writeGlobal(store.reference, global.reference, value.let(ValueMapper::from))
        return ResultFactory.new(result)
    }

    override fun memoryReadBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytesToRead: Int,
        buffer: ByteArray,
        bufferPointer: Int,
    ): Result<ByteArray> {
        val result = readBytes(store.reference, memory.reference, buffer, pointer, bytesToRead, bufferPointer)
        return ResultFactory.new(result)
    }

    override fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytes: ByteArray,
    ): Result<Unit> {
        val result = writeBytes(store.reference, memory.reference, pointer, bytes)
        return ResultFactory.new(result)
    }

    override fun memoryReadUtf8NullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<String> {
        val result = readNullTerminatedUtf8String(store.reference, memory.reference, pointer)
        return ResultFactory.new(result)
    }
}
