package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class MemArgDecoderTest {

    @Test
    fun `can decode a MemArg`() {

        val args = sequenceOf(117u, 118u).iterator()
        val reader = FakeUIntReader {
            Ok(args.next())
        }
        val context = decoderContext(reader)

        val actual = MemArgDecoder(context)

        assertEquals(Ok(MemArg(117u, 118u)), actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = MemArgDecoder(context)

        assertEquals(expected, actual)
    }
}
