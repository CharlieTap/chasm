package io.github.charlietap.chasm.decoder.wasm.decoder.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.decoder.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class LimitsDecoderTest {

    @Test
    fun `can decode encoded limits with only minimum`() {

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(0u.toUByte())
        }
        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(117u)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = intReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(Limits(117u))

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `can decode encoded limits with maximum`() {

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(1u.toUByte())
        }
        val minMax = sequenceOf(117u, 121u).iterator()
        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(minMax.next())
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = intReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(Limits(117u, 121u))

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown flag error when flag doesn't match`() {

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(3u.toUByte())
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
        )
        val context = decoderContext(reader)

        val expected = Err(TypeDecodeError.UnknownLimitsFlag(3u.toUByte()))

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = LimitsDecoder(context)

        assertEquals(expected, actual)
    }
}
