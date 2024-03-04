package io.github.charlietap.chasm.decoder.type.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryMemoryTypeDecoderTest {

    @Test
    fun `can decode an encoded memory type`() {

        val limits = Limits(117u, 121u)
        val limitsDecoder: LimitsDecoder = {
            Ok(limits)
        }
        val expected = Ok(MemoryType(limits))

        val actual = BinaryMemoryTypeDecoder(FakeWasmBinaryReader(), limitsDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryMemoryTypeDecoder(reader)

        assertEquals(expected, actual)
    }
}
