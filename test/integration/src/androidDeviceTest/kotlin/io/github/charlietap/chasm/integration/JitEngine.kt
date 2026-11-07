@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.ExternRef
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.jit.instance
import io.github.charlietap.chasm.jit.module
import io.github.charlietap.chasm.jit.validate
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.embedding.addFuel as embeddingAddFuel
import io.github.charlietap.chasm.embedding.exports as embeddingExports
import io.github.charlietap.chasm.embedding.extern.createExternRef as embeddingCreateExternRef
import io.github.charlietap.chasm.embedding.gc as embeddingGc
import io.github.charlietap.chasm.embedding.interrupt as embeddingInterrupt
import io.github.charlietap.chasm.embedding.invoke as embeddingInvoke
import io.github.charlietap.chasm.embedding.memory.growMemory as embeddingGrowMemory
import io.github.charlietap.chasm.embedding.memory.readBytes as embeddingReadBytes
import io.github.charlietap.chasm.embedding.memory.readNullTerminatedUtf8String as embeddingReadNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.memory.readUtf8String as embeddingReadUtf8String
import io.github.charlietap.chasm.embedding.memory.writeUtf8String as embeddingWriteUtf8String
import io.github.charlietap.chasm.embedding.remainingFuel as embeddingRemainingFuel
import io.github.charlietap.chasm.embedding.resetFuel as embeddingResetFuel

class JitEngine : Engine {

    override fun name(): String = "jit"

    override fun instantiate(
        bytes: ByteArray,
        store: Store,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, ChasmError> =
        module(bytes)
            .flatMap { module ->
                validate(module)
            }.flatMap { module ->
                instance(store, module, imports, config)
            }

    override fun invoke(
        store: Store,
        instance: Instance,
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ChasmError> = embeddingInvoke(store, instance, name, arguments)

    override fun exports(
        instance: Instance,
    ): List<Export> = embeddingExports(instance)

    override fun gc(
        store: Store,
    ): ChasmResult<Unit, ChasmError> = embeddingGc(store)

    override fun addFuel(
        store: Store,
        units: Long,
    ): ChasmResult<Unit, ChasmError> = embeddingAddFuel(store, units)

    override fun remainingFuel(
        store: Store,
    ): ChasmResult<Long, ChasmError> = embeddingRemainingFuel(store)

    override fun resetFuel(
        store: Store,
    ): ChasmResult<Unit, ChasmError> = embeddingResetFuel(store)

    override fun interrupt(
        store: Store,
    ): ChasmResult<Boolean, ChasmError> = embeddingInterrupt(store)

    override fun growMemory(
        store: Store,
        memory: Memory,
        pagesToAdd: Int,
    ): ChasmResult<Int, ChasmError> = embeddingGrowMemory(store, memory, pagesToAdd)

    override fun readBytes(
        store: Store,
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
    ): ChasmResult<ByteArray, ChasmError> = embeddingReadBytes(store, memory, buffer, memoryPointer, bytesToRead)

    override fun readUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): ChasmResult<String, ChasmError> = embeddingReadUtf8String(store, memory, pointer, stringLengthInBytes)

    override fun readNullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): ChasmResult<String, ChasmError> = embeddingReadNullTerminatedUtf8String(store, memory, pointer)

    override fun writeUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        string: String,
    ): ChasmResult<Unit, ChasmError> = embeddingWriteUtf8String(store, memory, pointer, string)

    override fun createExternRef(
        store: Store,
        value: Any?,
    ): ChasmResult<ExternRef, ChasmError> = embeddingCreateExternRef(store, value)
}
