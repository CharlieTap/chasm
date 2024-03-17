package io.github.charlietap.chasm.decoder.wasm.decoder.section.table

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLength
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.TableSection
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTableSectionDecoderTest {

    @Test
    fun `can decode an encoded custom section`() {

        val reader = FakeWasmBinaryReader()
        val limits = Limits(117u, 121u)
        val refType = ReferenceType.RefNull(HeapType.Func)
        val tableType = TableType(refType, limits)
        val table = Table(Index.TableIndex(0u), tableType, Expression.EMPTY)
        val expected = Ok(TableSection(listOf(table)))

        val vectorDecoder: VectorLengthDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(VectorLength(1u))
        }

        val tableDecoder: TableDecoder = { _idx, _reader ->
            assertEquals(table.idx, _idx)
            assertEquals(reader, _reader)
            Ok(table)
        }

        val decoder = BinaryTableSectionDecoder(vectorDecoder, tableDecoder)

        val actual = decoder(reader, SectionSize(0u))

        assertEquals(expected, actual)
    }
}
