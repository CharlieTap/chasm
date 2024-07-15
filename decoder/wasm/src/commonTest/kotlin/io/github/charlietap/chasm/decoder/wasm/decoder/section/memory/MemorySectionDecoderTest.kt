package io.github.charlietap.chasm.decoder.wasm.decoder.section.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.section.MemorySection
import kotlin.test.Test
import kotlin.test.assertEquals

class MemorySectionDecoderTest {

    @Test
    fun `can decode an encoded memory section`() {

        val limits = Limits(117u, 121u)
        val memoryType = MemoryType(limits)
        val memory = Memory(Index.MemoryIndex(0u), memoryType)
        val expected = Ok(MemorySection(listOf(memory)))

        val memoryTypeDecoder: Decoder<MemoryType> = {
            Ok(memoryType)
        }

        val vectorDecoder: VectorDecoder<MemoryType> = { _, _ ->
            Ok(Vector(listOf(memoryType)))
        }

        val context = decoderContext()
        val actual = MemorySectionDecoder(context, vectorDecoder, memoryTypeDecoder)

        assertEquals(expected, actual)
    }
}
