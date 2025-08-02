package io.github.charlietap.chasm.decoder.decoder.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.i32AddressType
import io.github.charlietap.chasm.fixture.type.i64AddressType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.sharedStatus
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class LimitsDecoderTest {

    @Test
    fun `can decode encoded i32 limits with only minimum`() {
        val byteReader = { Ok(LIMIT_NO_MAX_UNSHARED_I32) }
        val intReader = { Ok(117u) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = intReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(
            Triple(
                limits(117u),
                unsharedStatus(),
                i32AddressType(),
            ),
        )

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `can decode encoded i32 limits with maximum which is not shared`() {
        val byteReader = { Ok(LIMIT_MAX_UNSHARED_I32) }
        val minMax = sequenceOf(117u, 121u).iterator()
        val intReader = { Ok(minMax.next()) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = intReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(
            Triple(
                limits(117u, 121u),
                unsharedStatus(),
                i32AddressType(),
            ),
        )

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `can decode encoded i32 limits with maximum which is shared`() {
        val byteReader = { Ok(LIMIT_MAX_SHARED_I32) }
        val minMax = sequenceOf(117u, 121u).iterator()
        val intReader = { Ok(minMax.next()) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = intReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(
            Triple(
                limits(117u, 121u),
                sharedStatus(),
                i32AddressType(),
            ),
        )

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when i32 shared unbounded limits flag`() {
        val byteReader = { Ok(LIMIT_NO_MAX_SHARED_I32) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
        )
        val context = decoderContext(reader)

        val expected = Err(TypeDecodeError.UnboundedSharedLimits)

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `can decode encoded i64 limits with only minimum`() {
        val byteReader = { Ok(LIMIT_NO_MAX_UNSHARED_I64) }
        val longReader = { Ok(217uL) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeULongReader = longReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(
            Triple(
                limits(217uL),
                unsharedStatus(),
                i64AddressType(),
            ),
        )

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `can decode encoded i64 limits with maximum which is not shared`() {
        val byteReader = { Ok(LIMIT_MAX_UNSHARED_I64) }
        val minMax = sequenceOf(217uL, 221uL).iterator()
        val longReader = { Ok(minMax.next()) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeULongReader = longReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(
            Triple(
                limits(217uL, 221uL),
                unsharedStatus(),
                i64AddressType(),
            ),
        )

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `can decode encoded i64 limits with maximum which is shared`() {
        val byteReader = { Ok(LIMIT_MAX_SHARED_I64) }
        val minMax = sequenceOf(217uL, 221uL).iterator()
        val longReader = { Ok(minMax.next()) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeULongReader = longReader,
        )
        val context = decoderContext(reader)

        val expected = Ok(
            Triple(
                limits(217uL, 221uL),
                sharedStatus(),
                i64AddressType(),
            ),
        )

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when i64 shared unbounded limits flag`() {
        val byteReader = { Ok(LIMIT_NO_MAX_SHARED_I64) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
        )
        val context = decoderContext(reader)

        val expected = Err(TypeDecodeError.UnboundedSharedLimits)

        val actual = LimitsDecoder(context)
        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown flag error when flag doesn't match`() {
        val byteReader = { Ok(117u.toUByte()) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
        )
        val context = decoderContext(reader)

        val expected = Err(TypeDecodeError.UnknownLimitsFlag(117u.toUByte()))

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
