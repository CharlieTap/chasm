package io.github.charlietap.chasm.decoder.decoder.type.table

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.SharedStatus
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.sharedStatus
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableTypeDecoderTest {

    @Test
    fun `can decode an encoded table type`() {

        val expectedByte: UByte = 0x70u
        val reader = FakeUByteReader {
            Ok(expectedByte)
        }
        val context = decoderContext(reader)

        val refType = refNullReferenceType(AbstractHeapType.Func)
        val referenceTypeDecoder: Decoder<ReferenceType> = { _context ->
            assertEquals(context, _context)
            Ok(refType)
        }

        val limits = limits(117u, 121u)
        val status = sharedStatus()
        val limitsDecoder: Decoder<Pair<Limits, SharedStatus>> = {
            Ok(limits to status)
        }

        val expected = Ok(tableType(refType, limits))

        val actual = TableTypeDecoder(
            context,
            referenceTypeDecoder,
            limitsDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when io fails`() {

        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = TableTypeDecoder(context)

        assertEquals(expected, actual)
    }
}
