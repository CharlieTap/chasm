package io.github.charlietap.chasm.decoder.decoder.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.DROP
import io.github.charlietap.chasm.decoder.decoder.instruction.SELECT
import io.github.charlietap.chasm.decoder.decoder.instruction.SELECT_W_TYPE
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ParametricInstructionDecoderTest {

    @Test
    fun `can decode DROP instruction`() {

        val opcode = DROP
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Ok(ParametricInstruction.Drop)

        val actual = ParametricInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode SELECT instruction`() {

        val opcode = SELECT
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Ok(ParametricInstruction.Select)

        val actual = ParametricInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a SELECT_W_TYPE instruction`() {

        val opcode = SELECT_W_TYPE
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val valueTypeDecoder: Decoder<ValueType> = {
            fail("Value type decoder should never be called")
        }

        val expectedValueTypes = listOf(i32ValueType())
        val vectorDecoder: VectorDecoder<ValueType> = { _, _ ->
            Ok(Vector(expectedValueTypes))
        }
        val expected = Ok(ParametricInstruction.SelectWithType(expectedValueTypes))

        val actual = ParametricInstructionDecoder(
            context,
            vectorDecoder,
            valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidParametricInstruction(opcode))

        val actual = ParametricInstructionDecoder(context)

        assertEquals(expected, actual)
    }
}
