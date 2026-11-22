@file:OptIn(ExperimentalChasmApi::class, InternalChasmApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompileConfig
import io.github.charlietap.chasm.aot.CompileOutput
import io.github.charlietap.chasm.aot.CompileResult
import io.github.charlietap.chasm.aot.compile
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.ExternRef
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import java.util.concurrent.atomic.AtomicInteger
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

class AotEngine(
    private val configure: CompileConfig.Builder.() -> Unit = {},
) : Engine {

    override fun name(): String {
        return AOT
    }

    override fun instantiate(
        bytes: ByteArray,
        store: Store,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {

        val output = CompileOutput.Memory()
        val compileConfig = CompileConfig("$PACKAGE_NAME.module${modules.incrementAndGet()}") {
            store(storeConfig(store, config))
            stacktraces(config.debugInfo)
            configure()
        }

        return when (val result = compile(bytes, output, compileConfig)) {
            is CompileResult.Success -> instance(store, output.load(result.artifact), imports)
            is CompileResult.Error -> error("Compiling the module ahead of time failed: ${result.error}")
        }
    }

    override fun invoke(
        store: Store,
        instance: Instance,
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError<ReferenceValue.Exception>> {
        return embeddingInvoke(store, instance, name, arguments)
    }

    override fun exports(
        instance: Instance,
    ): List<Export> {
        return embeddingExports(instance)
    }

    override fun gc(
        store: Store,
    ) {
        embeddingGc(store)
    }

    override fun addFuel(
        store: Store,
        units: Long,
    ) {
        embeddingAddFuel(store, units)
    }

    override fun remainingFuel(
        store: Store,
    ): Long {
        return embeddingRemainingFuel(store)
    }

    override fun resetFuel(
        store: Store,
    ) {
        embeddingResetFuel(store)
    }

    override fun interrupt(
        store: Store,
    ): Boolean {
        return embeddingInterrupt(store)
    }

    override fun growMemory(
        store: Store,
        memory: Memory,
        pagesToAdd: Int,
    ): Int {
        return embeddingGrowMemory(store, memory, pagesToAdd)
    }

    override fun readBytes(
        store: Store,
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
    ): ByteArray {
        return embeddingReadBytes(store, memory, buffer, memoryPointer, bytesToRead)
    }

    override fun readUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): String {
        return embeddingReadUtf8String(store, memory, pointer, stringLengthInBytes)
    }

    override fun readNullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): String {
        return embeddingReadNullTerminatedUtf8String(store, memory, pointer)
    }

    override fun writeUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        string: String,
    ) {
        embeddingWriteUtf8String(store, memory, pointer, string)
    }

    override fun createExternRef(
        store: Store,
        value: Any?,
    ): ExternRef {
        return embeddingCreateExternRef(store, value)
    }

    private companion object {
        const val PACKAGE_NAME = "io.github.charlietap.chasm.integration.compiled"

        // Each compiled module gets its own package, as Transmute gives each module, so frames name one module.
        val modules = AtomicInteger()
    }
}

@Suppress("DEPRECATION")
private fun storeConfig(
    store: Store,
    config: RuntimeConfig,
): StoreConfig = store.store.config.let { storeConfig ->
    StoreConfig(
        meterFuel = storeConfig.meterFuel,
        interruptible = storeConfig.interruptible,
        gcStrategy = storeConfig.gcStrategy ?: config.gcStrategy,
        gcThreshold = storeConfig.gcThreshold ?: config.gcThreshold,
    )
}
