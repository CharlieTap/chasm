package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
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

interface Engine {

    fun name(): String

    fun instantiate(
        bytes: ByteArray,
        store: Store,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>>

    fun invoke(
        store: Store,
        instance: Instance,
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError<ReferenceValue.Exception>>

    fun exports(
        instance: Instance,
    ): List<Export>

    fun gc(
        store: Store,
    )

    fun addFuel(
        store: Store,
        units: Long,
    )

    fun remainingFuel(
        store: Store,
    ): Long

    fun resetFuel(
        store: Store,
    )

    fun interrupt(
        store: Store,
    ): Boolean

    fun growMemory(
        store: Store,
        memory: Memory,
        pagesToAdd: Int,
    ): Int

    fun readBytes(
        store: Store,
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
    ): ByteArray

    fun readUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): String

    fun readNullTerminatedUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
    ): String

    fun writeUtf8String(
        store: Store,
        memory: Memory,
        pointer: Int,
        string: String,
    )

    fun createExternRef(
        store: Store,
        value: Any?,
    ): ExternRef
}

expect val engines: Set<Engine>
