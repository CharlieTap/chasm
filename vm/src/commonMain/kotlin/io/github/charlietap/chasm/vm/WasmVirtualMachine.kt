package io.github.charlietap.chasm.vm

import kotlin.jvm.JvmInline

interface WasmVirtualMachine {

    sealed class Result<out T> {
        data class Ok<T>(val value: T) : Result<T>()

        data class Error(val message: String) : Result<Nothing>()
    }

    sealed interface Value {
        @JvmInline
        value class I32(val value: Int) : Value

        @JvmInline
        value class I64(val value: Long) : Value

        @JvmInline
        value class F32(val value: Float) : Value

        @JvmInline
        value class F64(val value: Double) : Value
    }

    fun storeInit(): Store

    fun moduleDecode(
        binary: ByteArray,
    ): Result<Module>

    fun moduleInstantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Result<Instance>

    fun allocateFunction(
        store: Store,
        type: FunctionType,
        function: HostFunction,
    ): Result<ExternalAddress.Function>

    fun exportFunction(
        instance: Instance,
        name: String,
    ): Result<Function>

    fun exportGlobal(
        instance: Instance,
        name: String,
    ): Result<Global>

    fun exportMemory(
        instance: Instance,
        name: String,
    ): Result<Memory>

    fun exportTable(
        instance: Instance,
        name: String,
    ): Result<Table>

    fun functionInvoke(
        store: Store,
        instance: Instance,
        functionName: String,
        args: List<Value>,
    ): Result<List<Value>>

    fun globalRead(
        store: Store,
        global: Global,
    ): Result<Value>

    fun globalWrite(
        store: Store,
        global: Global,
        value: Value,
    ): Result<Unit>

    fun memoryReadBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytesToRead: Int,
        buffer: ByteArray,
        bufferPointer: Int = 0,
    ): Result<ByteArray>

    fun memoryWriteBytes(
        store: Store,
        memory: Memory,
        pointer: Int,
        bytes: ByteArray,
    ): Result<Unit>

    fun memoryReadInt(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<Int> {
        val buffer = ByteArray(4)
        return when (val result = memoryReadBytes(store, memory, pointer, Int.SIZE_BYTES, buffer)) {
            is Result.Ok -> {
                val bytes = result.value
                val int = (bytes[0].toInt() and 0xFF) or
                    ((bytes[1].toInt() and 0xFF) shl 8) or
                    ((bytes[2].toInt() and 0xFF) shl 16) or
                    ((bytes[3].toInt() and 0xFF) shl 24)
                Result.Ok(int)
            }

            is Result.Error -> result
        }
    }

    fun memoryReadLong(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<Long> {
        val buffer = ByteArray(8)
        return when (val result = memoryReadBytes(store, memory, pointer, Long.SIZE_BYTES, buffer)) {
            is Result.Ok -> {
                val bytes = result.value
                val value =
                    (bytes[0].toLong() and 0xFF) or
                        ((bytes[1].toLong() and 0xFF) shl 8) or
                        ((bytes[2].toLong() and 0xFF) shl 16) or
                        ((bytes[3].toLong() and 0xFF) shl 24) or
                        ((bytes[4].toLong() and 0xFF) shl 32) or
                        ((bytes[5].toLong() and 0xFF) shl 40) or
                        ((bytes[6].toLong() and 0xFF) shl 48) or
                        ((bytes[7].toLong() and 0xFF) shl 56)
                Result.Ok(value)
            }

            is Result.Error -> result
        }
    }

    fun memoryReadFloat(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<Float> {
        return when (val result = memoryReadInt(store, memory, pointer)) {
            is Result.Ok -> Result.Ok(Float.fromBits(result.value))
            is Result.Error -> result
        }
    }

    fun memoryReadDouble(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<Double> {
        return when (val result = memoryReadLong(store, memory, pointer)) {
            is Result.Ok -> Result.Ok(Double.fromBits(result.value))
            is Result.Error -> result
        }
    }

    fun memoryWriteInt(
        store: Store,
        memory: Memory,
        pointer: Int,
        value: Int,
    ): Result<Unit> {
        val bytes = byteArrayOf(
            value.toByte(),
            (value shr 8).toByte(),
            (value shr 16).toByte(),
            (value shr 24).toByte(),
        )
        return memoryWriteBytes(store, memory, pointer, bytes)
    }

    fun memoryWriteLong(
        store: Store,
        memory: Memory,
        pointer: Int,
        value: Long,
    ): Result<Unit> {
        val bytes = byteArrayOf(
            value.toByte(),
            (value shr 8).toByte(),
            (value shr 16).toByte(),
            (value shr 24).toByte(),
            (value shr 32).toByte(),
            (value shr 40).toByte(),
            (value shr 48).toByte(),
            (value shr 56).toByte(),
        )
        return memoryWriteBytes(store, memory, pointer, bytes)
    }

    fun memoryWriteFloat(
        store: Store,
        memory: Memory,
        pointer: Int,
        value: Float,
    ): Result<Unit> {
        return memoryWriteInt(store, memory, pointer, value.toBits())
    }

    fun memoryWriteDouble(
        store: Store,
        memory: Memory,
        pointer: Int,
        value: Double,
    ): Result<Unit> {
        return memoryWriteLong(store, memory, pointer, value.toBits())
    }

    fun memoryReadUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        lengthInBytes: Int,
    ): Result<String> {
        val buffer = ByteArray(lengthInBytes)
        return when (val result = memoryReadBytes(store, memory, pointer, lengthInBytes, buffer)) {
            is Result.Ok<ByteArray> -> Result.Ok(result.value.decodeToString())
            is Result.Error -> result
        }
    }

    fun memoryReadUtf8NullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): Result<String>

    fun memoryWriteUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        value: String,
    ): Result<Unit> {
        val buffer = value.encodeToByteArray()
        return memoryWriteBytes(store, memory, pointer, buffer)
    }

    fun memoryWriteNullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        value: String,
    ): Result<Unit> {
        val bytes = value.encodeToByteArray()
        val buffer = ByteArray(bytes.size + 1).apply {
            bytes.copyInto(this)
            set(bytes.size, 0)
        }
        return memoryWriteBytes(store, memory, pointer, buffer)
    }
}
