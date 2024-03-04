package io.github.charlietap.chasm.decoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.decoder.instruction.GLOBAL_GET
import io.github.charlietap.chasm.decoder.instruction.GLOBAL_SET
import io.github.charlietap.chasm.decoder.instruction.LOCAL_GET
import io.github.charlietap.chasm.decoder.instruction.LOCAL_SET
import io.github.charlietap.chasm.decoder.instruction.LOCAL_TEE
import io.github.charlietap.chasm.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.LocalIndexDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryVariableInstructionDecoderTest {

    @Test
    fun `can decode LOCAL_GET instruction`() {

        val opcode = LOCAL_GET

        val expectedIndex = Index.LocalIndex(117u)
        val expected = Ok(VariableInstruction.LocalGet(expectedIndex))

        val localIndexDecoder: LocalIndexDecoder = {
            Ok(expectedIndex)
        }

        val globalIndexDecoder: GlobalIndexDecoder = {
            fail("Global index decoder should not be called when decoding local index")
        }

        val actual = BinaryVariableInstructionDecoder(FakeWasmBinaryReader(), opcode, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode LOCAL_SET instruction`() {

        val opcode = LOCAL_SET

        val expectedIndex = Index.LocalIndex(117u)
        val expected = Ok(VariableInstruction.LocalSet(expectedIndex))

        val localIndexDecoder: LocalIndexDecoder = {
            Ok(expectedIndex)
        }

        val globalIndexDecoder: GlobalIndexDecoder = {
            fail("Global index decoder should not be called when decoding local index")
        }

        val actual = BinaryVariableInstructionDecoder(FakeWasmBinaryReader(), opcode, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode LOCAL_TEE instruction`() {

        val opcode = LOCAL_TEE

        val expectedIndex = Index.LocalIndex(117u)
        val expected = Ok(VariableInstruction.LocalTee(expectedIndex))

        val localIndexDecoder: LocalIndexDecoder = {
            Ok(expectedIndex)
        }

        val globalIndexDecoder: GlobalIndexDecoder = {
            fail("Global index decoder should not be called when decoding local index")
        }

        val actual = BinaryVariableInstructionDecoder(FakeWasmBinaryReader(), opcode, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode GLOBAL_GET instruction`() {

        val opcode = GLOBAL_GET

        val expectedIndex = Index.GlobalIndex(117u)
        val expected = Ok(VariableInstruction.GlobalGet(expectedIndex))

        val localIndexDecoder: LocalIndexDecoder = {
            fail("Local index decoder should not be called when decoding global index")
        }

        val globalIndexDecoder: GlobalIndexDecoder = {
            Ok(expectedIndex)
        }

        val actual = BinaryVariableInstructionDecoder(FakeWasmBinaryReader(), opcode, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode GLOBAL_SET instruction`() {

        val opcode = GLOBAL_SET

        val expectedIndex = Index.GlobalIndex(117u)
        val expected = Ok(VariableInstruction.GlobalSet(expectedIndex))

        val localIndexDecoder: LocalIndexDecoder = {
            fail("Local index decoder should not be called when decoding global index")
        }

        val globalIndexDecoder: GlobalIndexDecoder = {
            Ok(expectedIndex)
        }

        val actual = BinaryVariableInstructionDecoder(FakeWasmBinaryReader(), opcode, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidVariableInstruction(opcode))

        val actual = BinaryVariableInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
