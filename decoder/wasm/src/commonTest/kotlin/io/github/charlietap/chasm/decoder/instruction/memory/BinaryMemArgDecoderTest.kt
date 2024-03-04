package io.github.charlietap.chasm.decoder.instruction.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeUIntReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryMemArgDecoderTest {

    @Test
    fun `can decode a MemArg`() {

        val args = sequenceOf(117u, 118u).iterator()
        val reader = FakeUIntReader {
            Ok(args.next())
        }

        val actual = BinaryMemArgDecoder(reader)

        assertEquals(Ok(MemArg(117u, 118u)), actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryMemArgDecoder(reader)

        assertEquals(expected, actual)
    }
}
