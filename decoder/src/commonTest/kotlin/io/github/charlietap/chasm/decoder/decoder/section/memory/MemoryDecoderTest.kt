package io.github.charlietap.chasm.decoder.decoder.section.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.fixture.ast.module.memory
import io.github.charlietap.chasm.fixture.ast.module.memoryImport
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.type.MemoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryDecoderTest {

    @Test
    fun `can decode an encoded memory`() {

        val memoryType = memoryType()
        val memory = memory(
            idx = memoryIndex(3u),
            type = memoryType,
        )
        val expected = Ok(memory)

        val memoryTypeDecoder: Decoder<MemoryType> = { _ ->
            Ok(memoryType)
        }

        val context = decoderContext(
            index = 2,
            imports = listOf(
                memoryImport(),
            ),
        )
        val actual = MemoryDecoder(
            context = context,
            memoryTypeDecoder = memoryTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
