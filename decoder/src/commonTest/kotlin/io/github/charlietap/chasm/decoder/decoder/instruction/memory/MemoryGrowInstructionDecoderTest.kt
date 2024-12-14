package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.fixture.ast.instruction.memoryGrowInstruction
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryGrowInstructionDecoderTest {

    @Test
    fun `can decode a MemoryGrow instruction`() {

        val context = decoderContext()

        val expectedMemoryIndex = memoryIndex(117u)
        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = {
            Ok(expectedMemoryIndex)
        }
        val expected = memoryGrowInstruction(expectedMemoryIndex)

        val actual = MemoryGrowInstructionDecoder(
            context = context,
            memoryIndexDecoder = memoryIndexDecoder,
        )

        assertEquals(Ok(expected), actual)
    }
}
