package io.github.charlietap.chasm.decoder.decoder.section.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.MemorySection
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MemorySectionDecoderTest {

    @Test
    fun `can decode an encoded memory section`() {

        val memoryType = memoryType()
        val memory = Memory(Index.MemoryIndex(0u), memoryType)
        val expected = Ok(MemorySection(listOf(memory)))

        val memoryDecoder: Decoder<Memory> = {
            fail("MemoryDecoder should not be called directly")
        }

        val vectorDecoder: VectorDecoder<Memory> = { _, _ ->
            Ok(Vector(listOf(memory)))
        }

        val context = decoderContext()
        val actual = MemorySectionDecoder(context, vectorDecoder, memoryDecoder)

        assertEquals(expected, actual)
    }
}
