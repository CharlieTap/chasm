package io.github.charlietap.chasm.decoder.decoder.section.table

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.TableSection
import io.github.charlietap.chasm.fixture.ast.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TableSectionDecoderTest {

    @Test
    fun `can decode an encoded custom section`() {

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)
        val tableType = tableType()
        val table = Table(Index.TableIndex(0u), tableType, Expression.EMPTY)
        val expected = Ok(TableSection(listOf(table)))

        val tableDecoder: Decoder<Table> = { _ ->
            fail("TableDecoder should not be called directly")
        }

        val vectorDecoder: VectorDecoder<Table> = { _, _ ->
            Ok(Vector(listOf(table)))
        }

        val actual = TableSectionDecoder(context, vectorDecoder, tableDecoder)

        assertEquals(expected, actual)
    }
}
