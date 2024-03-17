package io.github.charlietap.chasm.decoder.wasm.decoder.section.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.MemorySection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryMemorySectionDecoderTest {

    @Test
    fun `can decode an encoded memory section`() {

        val limits = Limits(117u, 121u)
        val memoryType = MemoryType(limits)
        val memory = Memory(Index.MemoryIndex(0u), memoryType)
        val expected = Ok(MemorySection(listOf(memory)))

        val memoryTypeDecoder: MemoryTypeDecoder = {
            Ok(memoryType)
        }

        val vectorDecoder: VectorDecoder<MemoryType> = { _, sub ->
            assertEquals(memoryTypeDecoder, sub)
            Ok(Vector(listOf(memoryType)))
        }

        val decoder = BinaryMemorySectionDecoder(vectorDecoder, memoryTypeDecoder)

        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
