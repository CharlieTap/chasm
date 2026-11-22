package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.readNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value
import kotlinx.coroutines.runBlocking
import kotlin.contextOf
import io.github.charlietap.chasm.host.HostFunction as ChasmHostFunction

object NonJsVirtualMachine : WasmVirtualMachine {

    object ResultFactory {
        fun <T> new(result: ChasmResult<T, *>): Result<T> {
            return when (result) {
                is ChasmResult.Success -> Result.Ok(result.result)
                is ChasmResult.Error -> Result.Error(result.error.toString())
            }
        }
    }

    override fun storeInit(): Store {
        return Store(store())
    }

    override fun moduleDecode(binary: ByteArray): Result<Module> {
        val module = runBlocking { module(binary) }.expect("Failed to decode module")
        return Result.Ok(Module(module))
    }

    override fun moduleInstantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Result<Instance> {
        val mappedImports = imports.map(ImportMapper::from)
        val instance = runBlocking { instance(store.reference, module.reference, mappedImports) }.expect("Failed to instantiate module")
        return Result.Ok(Instance(instance))
    }

    override fun allocateFunction(
        store: Store,
        type: FunctionType,
        function: HostFunction,
    ): Result<ExternalAddress.Function> {

        val functionType = FunctionTypeMapper.from(type)

        val hostFunction = ChasmHostFunction { parameters, results ->
            val stack = contextOf<HostStack>()
            val mappedParams = List(type.params.size) { index ->
                ValueMapper.fromRaw(stack[parameters + index], type.params[index])
            }
            val resultValues = function(mappedParams)
            resultValues.forEachIndexed { index, value ->
                stack[results + index] = ValueMapper.toRaw(value)
            }
        }

        val result = function(store.reference, functionType, hostFunction)
        return Result.Ok(ExternalAddress.Function(result))
    }

    override fun exportFunction(
        instance: Instance,
        name: String,
    ): Result<Function> {
        val function = exports(instance.reference).function(name)?.let(::Function)
        return function?.let { value -> Result.Ok(value) } ?: Result.Error("Failed to find function export with name $name")
    }

    override fun exportGlobal(
        instance: Instance,
        name: String,
    ): Result<Global> {
        val global = exports(instance.reference).global(name)?.let(::Global)
        return global?.let { value -> Result.Ok(value) } ?: Result.Error("Failed to find global export with name $name")
    }

    override fun exportMemory(
        instance: Instance,
        name: String,
    ): Result<Memory> {
        val memory = exports(instance.reference).memory(name)?.let(::Memory)
        return memory?.let { value -> Result.Ok(value) } ?: Result.Error("Failed to find memory export with name $name")
    }

    override fun exportTable(
        instance: Instance,
        name: String,
    ): Result<Table> {
        val table = exports(instance.reference).table(name)?.let(::Table)
        return table?.let { value -> Result.Ok(value) } ?: Result.Error("Failed to find table export with name $name")
    }

    override fun prepareFunction(
        store: Store,
        instance: Instance,
        functionName: String,
        resultTypes: List<ValueType>,
    ): Result<PreparedFunction> {
        return prepareFunction(instance.reference, functionName).let(ResultFactory::new).map { function ->
            PreparedFunction { args ->
                function(args.map(ValueMapper::from)).map { values ->
                    values.map(ValueMapper::to)
                }.let(ResultFactory::new)
            }
        }
    }

    override fun functionInvoke(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<Value>,
    ): Result<List<Value>> {
        return invoke(instance.reference, functionName, args.map(ValueMapper::from)).map { values ->
            values.map(ValueMapper::to)
        }.let(ResultFactory::new)
    }

    override fun functionInvokeTyped(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<Value>,
        resultTypes: List<ValueType>,
    ): Result<List<Value>> {
        return functionInvoke(store, instance, functionName, args)
    }

    override fun globalRead(
        store: Store,
        global: Global,
    ): Result<Value> {
        return catchingAccessorFailure {
            ValueMapper.fromRaw(readGlobal(global.reference), ValueTypeMapper.to(global.reference.type.valueType))
        }
    }

    override fun globalWrite(
        store: Store,
        global: Global,
        value: Value,
    ): Result<Unit> {

        val globalType = global.reference.type.valueType
        if (ValueTypeMapper.from(ValueMapper.valueType(value)) != globalType) {
            return Result.Error("Cannot write $value to a global of type $globalType")
        }

        return catchingAccessorFailure {
            writeGlobal(global.reference, ValueMapper.toRaw(value))
        }
    }

    override fun memoryReadBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytesToRead: Int,
        buffer: ByteArray,
        bufferPointer: Int,
    ): Result<ByteArray> {
        return catchingAccessorFailure {
            readBytes(memory.reference, buffer, pointer, bytesToRead, bufferPointer)
        }
    }

    override fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytes: ByteArray,
    ): Result<Unit> {
        return catchingAccessorFailure {
            writeBytes(memory.reference, pointer, bytes)
        }
    }

    override fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ): Result<Unit> {
        return catchingAccessorFailure {
            writeBytes(memory.reference, pointer, buffer, bufferPointer, bytesToWrite)
        }
    }

    override fun memoryReadUtf8NullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<String> {
        return catchingAccessorFailure {
            readNullTerminatedUtf8String(memory.reference, pointer)
        }
    }

    private inline fun <T> catchingAccessorFailure(access: () -> T): Result<T> {
        return try {
            Result.Ok(access())
        } catch (failure: IndexOutOfBoundsException) {
            Result.Error(failure.message.orEmpty())
        } catch (failure: IllegalArgumentException) {
            Result.Error(failure.message.orEmpty())
        } catch (failure: IllegalStateException) {
            Result.Error(failure.message.orEmpty())
        }
    }
}
