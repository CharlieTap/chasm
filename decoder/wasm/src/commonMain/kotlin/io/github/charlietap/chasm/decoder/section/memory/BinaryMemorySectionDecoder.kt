package io.github.charlietap.chasm.decoder.section.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.type.memory.BinaryMemoryTypeDecoder
import io.github.charlietap.chasm.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.MemorySection
import io.github.charlietap.chasm.section.SectionSize

class BinaryMemorySectionDecoder(
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
