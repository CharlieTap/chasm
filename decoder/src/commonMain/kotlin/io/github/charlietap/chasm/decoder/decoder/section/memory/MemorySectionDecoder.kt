package io.github.charlietap.chasm.decoder.decoder.section.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.MemorySection

internal fun MemorySectionDecoder(
    context: DecoderContext,
): Result<MemorySection, WasmDecodeError> =
    MemorySectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        memoryTypeDecoder = ::MemoryTypeDecoder,
    )

internal fun MemorySectionDecoder(
    context: DecoderContext,
    vectorDecoder: VectorDecoder<MemoryType>,
    memoryTypeDecoder: Decoder<MemoryType>,
): Result<MemorySection, WasmDecodeError> = binding {

    val memoriesTypes = vectorDecoder(context, memoryTypeDecoder).bind()

    val memories = memoriesTypes.vector.mapIndexed { idx, memoryType ->
        val index = Index.MemoryIndex(idx.toUInt())
        Memory(index, memoryType)
    }

    MemorySection(memories)
}
