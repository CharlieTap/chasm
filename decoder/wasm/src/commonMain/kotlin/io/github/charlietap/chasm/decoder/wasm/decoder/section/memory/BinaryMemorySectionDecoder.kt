package io.github.charlietap.chasm.decoder.wasm.decoder.section.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.memory.BinaryMemoryTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.MemorySection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryMemorySectionDecoder(
    private val vectorDecoder: VectorDecoder<MemoryType> = ::BinaryVectorDecoder,
    private val memoryTypeDecoder: MemoryTypeDecoder = ::BinaryMemoryTypeDecoder,
) : MemorySectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<MemorySection, WasmDecodeError> = binding {

        val memoriesTypes = vectorDecoder(reader, memoryTypeDecoder).bind()

        val memories = memoriesTypes.vector.mapIndexed { idx, memoryType ->
            val index = Index.MemoryIndex(idx.toUInt())
            Memory(index, memoryType)
        }

        MemorySection(memories)
    }
}
