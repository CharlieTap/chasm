package io.github.charlietap.chasm.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.instruction.REF_FUNC
import io.github.charlietap.chasm.decoder.instruction.REF_ISNULL
import io.github.charlietap.chasm.decoder.instruction.REF_NULL
import io.github.charlietap.chasm.decoder.instruction.numeric.BinaryNumericInstructionDecoder
import io.github.charlietap.chasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.reader.FakeUByteReader
import io.github.charlietap.chasm.reader.FakeUIntReader
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryReferenceInstructionDecoderTest {

    @Test
    fun `can decode the REF_NULL instruction`() {

        val opcode = REF_NULL
        val refType = ReferenceType.Funcref
        val reader = FakeUByteReader {
            Ok(117u)
        }
        val referenceTypeDecoder: ReferenceTypeDecoder = { ubyte ->
            assertEquals(117u, ubyte)
            Ok(refType)
        }
        val expected = Ok(ReferenceInstruction.RefNull(refType))

        val actual = BinaryReferenceInstructionDecoder(reader, opcode, referenceTypeDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_ISNULL instruction`() {

        val opcode = REF_ISNULL
        val expected = Ok(ReferenceInstruction.RefIsNull)

        val actual = BinaryReferenceInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_FUNC instruction`() {

        val opcode = REF_FUNC
        val reader = FakeUIntReader {
            Ok(117u)
        }
        val expected = Ok(ReferenceInstruction.RefFunc(Index.FunctionIndex(117u)))

        val actual = BinaryReferenceInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidNumericInstruction(opcode))

        val actual = BinaryNumericInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
