package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.decoder.decoder.instruction.control.BLOCK_TYPE_EMPTY
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExpressionDecoderTest {

    @Test
    fun `end count must be positive`() {
        assertFailsWith<IllegalArgumentException> {
            ControlInstruction.End(0)
        }
    }

    @Test
    fun `decodes an empty expression`() {
        val actual = decode(END)

        assertEquals(Ok(Expression.EMPTY), actual)
    }

    @Test
    fun `decodes a flat instruction`() {
        val actual = decode(DROP, END)

        assertEquals(Ok(Expression(ParametricInstruction.Drop)), actual)
    }

    @Test
    fun `decodes empty control instructions`() {
        val actual = decode(
            BLOCK,
            BLOCK_TYPE_EMPTY,
            END,
            LOOP,
            BLOCK_TYPE_EMPTY,
            END,
            IF,
            BLOCK_TYPE_EMPTY,
            END,
            END,
        )
        val expected = Expression(
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.End(1),
            ControlInstruction.Loop(BlockType.Empty),
            ControlInstruction.End(1),
            ControlInstruction.If(BlockType.Empty),
            ControlInstruction.End(1),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `decodes empty if arms`() {
        val actual = decode(
            IF,
            BLOCK_TYPE_EMPTY,
            ELSE,
            END,
            END,
        )
        val expected = Expression(
            ControlInstruction.If(BlockType.Empty),
            ControlInstruction.Else,
            ControlInstruction.End(1),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `flattens nested control instructions`() {
        val actual = decode(
            BLOCK,
            BLOCK_TYPE_EMPTY,
            LOOP,
            BLOCK_TYPE_EMPTY,
            IF,
            BLOCK_TYPE_EMPTY,
            DROP,
            ELSE,
            NOP,
            END,
            END,
            END,
            END,
        )
        val expected = Expression(
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.Loop(BlockType.Empty),
            ControlInstruction.If(BlockType.Empty),
            ParametricInstruction.Drop,
            ControlInstruction.Else,
            ControlInstruction.Nop,
            ControlInstruction.End(3),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `does not combine non-consecutive endings`() {
        val actual = decode(
            BLOCK,
            BLOCK_TYPE_EMPTY,
            BLOCK,
            BLOCK_TYPE_EMPTY,
            END,
            NOP,
            END,
            END,
        )
        val expected = Expression(
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.End(1),
            ControlInstruction.Nop,
            ControlInstruction.End(1),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `does not combine endings across else`() {
        val actual = decode(
            BLOCK,
            BLOCK_TYPE_EMPTY,
            BLOCK,
            BLOCK_TYPE_EMPTY,
            END,
            ELSE,
            END,
            END,
        )
        val expected = Expression(
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.End(1),
            ControlInstruction.Else,
            ControlInstruction.End(1),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `decodes try table as a flat control instruction`() {
        val actual = decode(
            TRY_TABLE,
            BLOCK_TYPE_EMPTY,
            0u,
            END,
            END,
        )
        val expected = Expression(
            ControlInstruction.TryTable(BlockType.Empty, emptyList()),
            ControlInstruction.End(1),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `retains else for validation`() {
        val actual = decode(ELSE, ELSE, END)
        val expected = Expression(
            ControlInstruction.Else,
            ControlInstruction.Else,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `decodes deeply nested blocks iteratively`() {
        val depth = 20_000
        val bytes = ByteArray((depth * 2) + depth + 1)

        repeat(depth) { index ->
            bytes[index * 2] = BLOCK.toByte()
            bytes[(index * 2) + 1] = BLOCK_TYPE_EMPTY.toByte()
        }
        bytes.fill(END.toByte(), depth * 2)

        val actual = ExpressionDecoder(decoderContext(BinaryReader(bytes)))
        val expectedInstructions = buildList {
            repeat(depth) {
                add(ControlInstruction.Block(BlockType.Empty))
            }
            add(ControlInstruction.End(depth))
        }

        assertEquals(Ok(Expression(expectedInstructions)), actual)
    }

    @Test
    fun `returns io error when read fails`() {
        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        assertWasmDecodeError(err) {
            ExpressionDecoder(context)
        }
    }

    @Test
    fun `fails when an expression has no end`() {
        val context = decoderContext(BinaryReader(byteArrayOf(DROP.toByte())))

        assertFailsWith<NoSuchElementException> {
            ExpressionDecoder(context)
        }
    }

    private fun decode(vararg bytes: UByte) = ExpressionDecoder(
        decoderContext(BinaryReader(bytes.map(UByte::toByte).toByteArray())),
    )
}
