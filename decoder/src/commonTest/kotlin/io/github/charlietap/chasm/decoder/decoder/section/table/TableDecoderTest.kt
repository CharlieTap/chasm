package io.github.charlietap.chasm.decoder.decoder.section.table

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableImport
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.ast.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TableDecoderTest {

    @Test
    fun `can decode an encoded table without an init expression`() {

        val peekReader = FakeUByteReader {
            Ok(0x00u)
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })
        val context = decoderContext(
            reader = reader,
            index = 2,
            imports = listOf(
                tableImport(),
            ),
        )

        val tableIndex = tableIndex(3u)
        val tableType = tableType()
        val table = table(
            idx = tableIndex,
            type = tableType,
            initExpression = Expression(
                listOf(
                    ReferenceInstruction.RefNull(tableType.referenceType.heapType),
                ),
            ),
        )
        val expected = Ok(table)

        val tableTypeDecoder: Decoder<TableType> = { _context ->
            assertEquals(context, _context)
            Ok(tableType)
        }

        val expressionDecoder: Decoder<Expression> = { _ ->
            fail("expression decoder should not be called in this context")
        }

        val actual = TableDecoder(context, tableTypeDecoder, expressionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded table with an init expression`() {

        var timesCalled = 0
        val fakeUByteReader: () -> Result<UByte, WasmDecodeError> = {
            timesCalled++
            Ok(OPCODE_TABLE_WITH_EXPRESSION)
        }
        val peekReader = FakeWasmBinaryReader(fakeUByteReader = fakeUByteReader)
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = fakeUByteReader,
            fakePeekReader = { peekReader },
        )
        val context = decoderContext(
            reader = reader,
            index = 2,
            imports = listOf(
                tableImport(),
            ),
        )

        val tableIndex = tableIndex(3u)
        val tableType = tableType()
        val initExpression = expression()
        val table = table(
            idx = tableIndex,
            type = tableType,
            initExpression = initExpression,
        )
        val expected = Ok(table)

        val tableTypeDecoder: Decoder<TableType> = { _context ->
            assertEquals(context, _context)
            Ok(tableType)
        }

        val expressionDecoder: Decoder<Expression> = { _reader ->
            Ok(initExpression)
        }

        val actual = TableDecoder(context, tableTypeDecoder, expressionDecoder)

        assertEquals(expected, actual)
        assertEquals(3, timesCalled)
    }
}
