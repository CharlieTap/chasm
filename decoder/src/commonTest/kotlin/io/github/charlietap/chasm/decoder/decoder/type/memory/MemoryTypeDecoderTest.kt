package io.github.charlietap.chasm.decoder.decoder.type.memory

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.sharedStatus
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.SharedStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTypeDecoderTest {

    @Test
    fun `can decode an encoded memory type`() {

        val status = sharedStatus()
        val limits = limits(117u, 121u)
        val limitsDecoder: Decoder<Pair<Limits, SharedStatus>> = {
            Ok(limits to status)
        }
        val expected = Ok(memoryType(limits, status))

        val actual = MemoryTypeDecoder(decoderContext(), limitsDecoder)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = MemoryTypeDecoder(context)

        assertEquals(expected, actual)
    }
}
