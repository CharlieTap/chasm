package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceByteReader
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceByteReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun readByte(
    store: Store,
    memory: Memory,
    pointer: Int,
): ChasmResult<Byte, ChasmError.ExecutionError> =
    readByte(
        store = store,
        memory = memory,
        pointer = pointer,
        byteReader = ::MemoryInstanceByteReaderImpl,
    )
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    byteReader: MemoryInstanceByteReader,
): Result<Byte, ModuleTrapError> = binding {
    val instance = store.store.memory(memory.reference.address).bind()
    byteReader(instance, pointer).bind()
}
