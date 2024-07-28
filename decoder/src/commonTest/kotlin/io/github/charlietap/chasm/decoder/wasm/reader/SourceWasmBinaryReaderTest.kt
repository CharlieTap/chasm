package io.github.charlietap.chasm.decoder.wasm.reader

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.FakeByteArraySourceReader
import io.github.charlietap.chasm.decoder.FakeByteSourceReader
import io.github.charlietap.chasm.decoder.FakeExhaustedSourceReader
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.reader.SourceWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.const.Leb128
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import kotlin.test.Test
import kotlin.test.assertEquals

class SourceWasmBinaryReaderTest {

    @Test
    fun `can read a byte from source and forward it on`() {
        val expected: Result<Byte, WasmDecodeError> = Ok(0x01)
        val sourceReader = FakeByteSourceReader {
            expected.value
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.byte()

        assertEquals(expected, actual)
        assertEquals(1u, reader.position())
    }

    @Test
    fun `can catch io exceptions when reading a byte from the source byte reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).byte()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a ubyte from source and forward it on`() {
        val expected: Result<UByte, WasmDecodeError> = Ok(0x01u)
        val sourceReader = FakeByteSourceReader {
            expected.value.toByte()
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.ubyte()

        assertEquals(expected, actual)
        assertEquals(1u, reader.position())
    }

    @Test
    fun `can catch io exceptions when reading a ubyte from the source byte reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).ubyte()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read bytes from source and forward them on`() {
        val expected: Result<ByteArray, WasmDecodeError> = Ok(byteArrayOf(0x01, 0x02))
        val sourceReader = FakeByteArraySourceReader { amount ->
            assertEquals(2, amount)
            expected.value
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.bytes(2)

        assertEquals(expected, actual)
        assertEquals(2u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source bytearray reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).bytes(2)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read ubytes from source and forward them on`() {
        val expected: Result<UByteArray, WasmDecodeError> = Ok(ubyteArrayOf(0x01u, 0x02u))
        val sourceReader = FakeByteArraySourceReader { amount ->
            assertEquals(2, amount)
            expected.value.asByteArray()
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.ubytes(2u)

        assertEquals(expected, actual)
        assertEquals(2u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source ubytearray reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).ubytes(2u)

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a leb128 encoded int and forward it on`() {
        val expected: Result<Int, WasmDecodeError> = Ok(128)
        val source = Leb128.Integer.TWO_BYTES_SIGNED_POSITIVE.asSequence().iterator()
        val sourceReader = FakeByteSourceReader {
            source.next()
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.int()

        assertEquals(expected, actual)
        assertEquals(2u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source int reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).int()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a leb128 encoded uint and forward it on`() {
        val expected: Result<UInt, WasmDecodeError> = Ok(128u)
        val source = Leb128.Integer.TWO_BYTES_UNSIGNED.asSequence().iterator()
        val sourceReader = FakeByteSourceReader {
            source.next().toByte()
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.uint()

        assertEquals(expected, actual)
        assertEquals(2u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source uint reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).uint()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a leb128 encoded long and forward it on`() {
        val expected: Result<Long, WasmDecodeError> = Ok(Long.MAX_VALUE)
        val source = Leb128.Long.TEN_BYTES_SIGNED_POSITIVE.asSequence().iterator()
        val sourceReader = FakeByteSourceReader {
            source.next()
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.long()

        assertEquals(expected, actual)
        assertEquals(10u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source long reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).long()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read an IEEE 754 encoded float and forward it on`() {
        val expected: Result<Float, WasmDecodeError> = Ok(1.5f)
        val sourceReader = FakeByteArraySourceReader {
            byteArrayOf(0x00.toByte(), 0x00.toByte(), 0xC0.toByte(), 0x3F.toByte())
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.float()

        assertEquals(expected, actual)
        assertEquals(4u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source float reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).float()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read an IEEE 754 encoded double and forward it on`() {
        val expected: Result<Double, WasmDecodeError> = Ok(12345.6789)
        val sourceReader = FakeByteArraySourceReader {
            byteArrayOf(161.toByte(), 248.toByte(), 49.toByte(), 230.toByte(), 214.toByte(), 28.toByte(), 200.toByte(), 64.toByte())
        }

        val reader = SourceWasmBinaryReader(sourceReader)
        val actual = reader.double()

        assertEquals(expected, actual)
        assertEquals(8u, reader.position())
    }

    @Test
    fun `can catch io exceptions from the source double reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).double()

        assertEquals(expected, actual)
    }

    @Test
    fun `can check if the source is exhausted and forward it on`() {
        val expected = Ok(true)
        val sourceReader = FakeExhaustedSourceReader {
            expected.value
        }

        val actual = SourceWasmBinaryReader(sourceReader).exhausted()

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the source exhausted reader`() {
        val expected = ioError()
        val sourceReader = IOErrorSourceReader(expected.error.throwable)

        val actual = SourceWasmBinaryReader(sourceReader).exhausted()

        assertEquals(expected, actual)
    }
}
