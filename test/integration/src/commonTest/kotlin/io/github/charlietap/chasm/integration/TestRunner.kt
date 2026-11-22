package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.ExternRef
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

interface TestRunner {

    fun instance(): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>>

    fun invoke(
        name: String,
        arguments: List<ExecutionValue> = [],
    ): ChasmResult<List<ExecutionValue>, ExecutionError<ReferenceValue.Exception>>

    fun exports(): ChasmResult<List<Export>, InstantiationError<ReferenceValue.Exception>>

    fun gc()

    fun readBytes(
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
    ): ByteArray

    fun readUtf8String(
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): String

    fun readNullTerminatedUtf8String(
        memory: Memory,
        pointer: Int,
    ): String

    fun writeUtf8String(
        memory: Memory,
        pointer: Int,
        string: String,
    )

    fun createExternRef(
        value: Any?,
    ): ExternRef
}

class IntegrationTestRunner(
    private val engine: Engine,
    script: String,
    private val store: Store,
    imports: List<Import>,
    config: RuntimeConfig,
) : TestRunner {

    private val instance = engine.instantiate(
        Resource("$FILE_DIR$script.wasm").readBytes(),
        store,
        imports,
        config,
    )

    override fun instance(): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {
        return instance
    }

    override fun invoke(
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ExecutionError<ReferenceValue.Exception>> {
        return when (instance) {
            is ChasmResult.Success -> engine.invoke(store, instance.result, name, arguments)
            is ChasmResult.Error -> instance
        }
    }

    override fun exports(): ChasmResult<List<Export>, InstantiationError<ReferenceValue.Exception>> {
        return instance.map { instance ->
            engine.exports(instance)
        }
    }

    override fun gc() {
        engine.gc(store)
    }

    override fun readBytes(
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
    ): ByteArray {
        return engine.readBytes(store, memory, buffer, memoryPointer, bytesToRead)
    }

    override fun readUtf8String(
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): String {
        return engine.readUtf8String(store, memory, pointer, stringLengthInBytes)
    }

    override fun readNullTerminatedUtf8String(
        memory: Memory,
        pointer: Int,
    ): String {
        return engine.readNullTerminatedUtf8String(store, memory, pointer)
    }

    override fun writeUtf8String(
        memory: Memory,
        pointer: Int,
        string: String,
    ) {
        engine.writeUtf8String(store, memory, pointer, string)
    }

    override fun createExternRef(
        value: Any?,
    ): ExternRef {
        return engine.createExternRef(store, value)
    }

    private companion object {
        const val FILE_DIR = "integration/"
    }
}
