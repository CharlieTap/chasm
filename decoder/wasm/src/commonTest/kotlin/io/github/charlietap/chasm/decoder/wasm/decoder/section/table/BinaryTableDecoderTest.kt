package io.github.charlietap.chasm.decoder.wasm.decoder.section.table

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.expression
import io.github.charlietap.chasm.fixture.instruction.tableIndex
import io.github.charlietap.chasm.fixture.module.table
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryTableDecoderTest {

    @Test
    fun `can decode an encoded table without an init expression`() {

        val peekReader = FakeUByteReader {
            Ok(0x00u)
        }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })

        val tableIndex = tableIndex()
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

        val tableTypeDecoder: TableTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(tableType)
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            fail("expression decoder should not be called in this context")
        }

        val actual = BinaryTableDecoder(tableIndex, reader, tableTypeDecoder, expressionDecoder)

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
        val reader = FakeWasmBinaryReader(fakeUByteReader = fakeUByteReader, fakePeekReader = { peekReader })

        val tableIndex = tableIndex()
        val tableType = tableType()
        val initExpression = expression()
        val table = table(
            idx = tableIndex,
            type = tableType,
            initExpression = initExpression,
        )
        val expected = Ok(table)

        val tableTypeDecoder: TableTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(tableType)
        }

        val expressionDecoder: ExpressionDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(initExpression)
        }

        val actual = BinaryTableDecoder(tableIndex, reader, tableTypeDecoder, expressionDecoder)

        assertEquals(expected, actual)
        assertEquals(3, timesCalled)
    }
}
