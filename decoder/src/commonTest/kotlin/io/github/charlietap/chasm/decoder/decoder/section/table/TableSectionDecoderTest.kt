package io.github.charlietap.chasm.decoder.decoder.section.table

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorLength
import io.github.charlietap.chasm.decoder.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.TableSection
import io.github.charlietap.chasm.fixture.module.tableIndex
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableSectionDecoderTest {

    @Test
    fun `can decode an encoded custom section`() {

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)
        val tableType = tableType()
        val table = Table(Index.TableIndex(0u), tableType, Expression.EMPTY)
        val expected = Ok(TableSection(listOf(table, table.copy(idx = tableIndex(1u)))))

        val vectorDecoder: VectorLengthDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(VectorLength(2u))
        }

        val tableDecoder: Decoder<Table> = { _ ->
            Ok(table)
        }

        val actual = TableSectionDecoder(context, vectorDecoder, tableDecoder)

        assertEquals(expected, actual)
    }
}
