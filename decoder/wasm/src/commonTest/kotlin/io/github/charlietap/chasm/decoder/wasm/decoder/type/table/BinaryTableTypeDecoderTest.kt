package io.github.charlietap.chasm.decoder.wasm.decoder.type.table

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTableTypeDecoderTest {

    @Test
    fun `can decode an encoded table type`() {

        val expectedByte: UByte = 0x70u
        val reader = FakeUByteReader {
            Ok(expectedByte)
        }

        val refType = ReferenceType.RefNull(AbstractHeapType.Func)
        val referenceTypeDecoder: ReferenceTypeDecoder = { _reader, _opcode ->
            assertEquals(reader, _reader)
            assertEquals(expectedByte, _opcode)
            Ok(refType)
        }

        val limits = Limits(117u, 121u)
        val limitsDecoder: LimitsDecoder = {
            Ok(limits)
        }

        val expected = Ok(TableType(refType, limits))

        val actual = BinaryTableTypeDecoder(
            reader,
            referenceTypeDecoder,
            limitsDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryTableTypeDecoder(reader)

        assertEquals(expected, actual)
    }
}
