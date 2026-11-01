package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.ExternRef
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

interface Engine {

    fun name(): String

    fun instantiate(
        bytes: ByteArray,
        store: Store,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, ChasmError>

    fun invoke(
        store: Store,
        instance: Instance,
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ChasmError>

    fun exports(
        instance: Instance,
    ): List<Export>

    fun gc(
        store: Store,
    ): ChasmResult<Unit, ChasmError>

    fun readBytes(
        store: Store,
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
    ): ChasmResult<ByteArray, ChasmError>

    fun readUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): ChasmResult<String, ChasmError>

    fun readNullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): ChasmResult<String, ChasmError>

    fun writeUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        string: String,
    ): ChasmResult<Unit, ChasmError>

    fun createExternRef(
        store: Store,
        value: Any?,
    ): ChasmResult<ExternRef, ChasmError>
}

expect val engines: Set<Engine>
