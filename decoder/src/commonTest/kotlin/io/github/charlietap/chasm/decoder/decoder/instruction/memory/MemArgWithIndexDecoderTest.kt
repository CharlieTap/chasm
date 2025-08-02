package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class MemArgWithIndexDecoderTest {

    @Test
    fun `can decode a MemArgWithIndex with no memory index`() {

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = { Ok(8u) },
            fakeULongReader = { Ok(4uL) },
        )
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

        val args = sequenceOf(64u, 117u).iterator()
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = { Ok(args.next()) },
            fakeULongReader = { Ok(8uL) },
        )
        val context = decoderContext(reader)

        val actual = MemArgWithIndexDecoder(context)
        val expected = MemArgWithIndex(
            memoryIndex = memoryIndex(117u),
            memArg = MemArg(0u, 8u),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `error is returned if alignment exponent is greater than max`() {

        val reader = FakeUIntReader {
            Ok(128u)
        }
        val context = decoderContext(reader)

        val actual = MemArgWithIndexDecoder(context)
        val expected = Err(InstructionDecodeError.InvalidAlignment(128u))

        assertEquals(expected, actual)
    }
}
