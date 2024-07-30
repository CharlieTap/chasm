package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndex
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class MemArgWithIndexDecoderTest {

    @Test
    fun `can decode a MemArgWithIndex with no memory index`() {

        val args = sequenceOf(8u, 4u).iterator()
        val reader = FakeUIntReader {
            Ok(args.next())
        }
        val context = decoderContext(reader)

        val actual = MemArgWithIndexDecoder(context)
        val expected = MemArgWithIndex(
            memoryIndex = memoryIndex(0u),
            memArg = MemArg(8u, 4u),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode a MemArgWithIndex with a memory index`() {

        val args = sequenceOf(64u, 117u, 8u).iterator()
        val reader = FakeUIntReader {
            Ok(args.next())
        }
        val context = decoderContext(reader)

        val actual = MemArgWithIndexDecoder(context)
        val expected = MemArgWithIndex(
            memoryIndex = memoryIndex(117u),
            memArg = MemArg(0u, 8u),
        )

        assertEquals(Ok(expected), actual)
    }
}
