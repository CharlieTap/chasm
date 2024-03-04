package io.github.charlietap.chasm.decoder.section.table

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.TableSection
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTableSectionDecoderTest {

    @Test
    fun `can decode an encoded custom section`() {

        val limits = Limits(117u, 121u)
        val refType = ReferenceType.Funcref
        val tableType = TableType(refType, limits)
        val table = Table(Index.TableIndex(0u), tableType)
        val expected = Ok(TableSection(listOf(table)))

        val tableTypeDecoder: TableTypeDecoder = {
            Ok(tableType)
        }

        val vectorDecoder: VectorDecoder<TableType> = { _, sub ->
            assertEquals(tableTypeDecoder, sub)
            Ok(Vector(listOf(tableType)))
        }

        val decoder = BinaryTableSectionDecoder(vectorDecoder, tableTypeDecoder)

        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
