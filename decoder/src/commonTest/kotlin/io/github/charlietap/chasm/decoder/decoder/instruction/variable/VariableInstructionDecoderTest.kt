package io.github.charlietap.chasm.decoder.decoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.GLOBAL_GET
import io.github.charlietap.chasm.decoder.decoder.instruction.GLOBAL_SET
import io.github.charlietap.chasm.decoder.decoder.instruction.LOCAL_GET
import io.github.charlietap.chasm.decoder.decoder.instruction.LOCAL_SET
import io.github.charlietap.chasm.decoder.decoder.instruction.LOCAL_TEE
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class VariableInstructionDecoderTest {

    @Test
    fun `can decode LOCAL_GET instruction`() {

        val opcode = LOCAL_GET
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.LocalIndex(117u)
        val expected = Ok(VariableInstruction.LocalGet(expectedIndex))

        val localIndexDecoder: Decoder<Index.LocalIndex> = {
            Ok(expectedIndex)
        }

        val globalIndexDecoder: Decoder<Index.GlobalIndex> = {
            fail("Global index decoder should not be called when decoding local index")
        }

        val actual = VariableInstructionDecoder(context, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode LOCAL_SET instruction`() {

        val opcode = LOCAL_SET
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.LocalIndex(117u)
        val expected = Ok(VariableInstruction.LocalSet(expectedIndex))

        val localIndexDecoder: Decoder<Index.LocalIndex> = {
            Ok(expectedIndex)
        }

        val globalIndexDecoder: Decoder<Index.GlobalIndex> = {
            fail("Global index decoder should not be called when decoding local index")
        }

        val actual = VariableInstructionDecoder(context, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode LOCAL_TEE instruction`() {

        val opcode = LOCAL_TEE
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.LocalIndex(117u)
        val expected = Ok(VariableInstruction.LocalTee(expectedIndex))

        val localIndexDecoder: Decoder<Index.LocalIndex> = {
            Ok(expectedIndex)
        }

        val globalIndexDecoder: Decoder<Index.GlobalIndex> = {
            fail("Global index decoder should not be called when decoding local index")
        }

        val actual = VariableInstructionDecoder(context, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode GLOBAL_GET instruction`() {

        val opcode = GLOBAL_GET
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.GlobalIndex(117u)
        val expected = Ok(VariableInstruction.GlobalGet(expectedIndex))

        val localIndexDecoder: Decoder<Index.LocalIndex> = {
            fail("Local index decoder should not be called when decoding global index")
        }

        val globalIndexDecoder: Decoder<Index.GlobalIndex> = {
            Ok(expectedIndex)
        }

        val actual = VariableInstructionDecoder(context, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode GLOBAL_SET instruction`() {

        val opcode = GLOBAL_SET
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.GlobalIndex(117u)
        val expected = Ok(VariableInstruction.GlobalSet(expectedIndex))

        val localIndexDecoder: Decoder<Index.LocalIndex> = {
            fail("Local index decoder should not be called when decoding global index")
        }

        val globalIndexDecoder: Decoder<Index.GlobalIndex> = {
            Ok(expectedIndex)
        }

        val actual = VariableInstructionDecoder(context, localIndexDecoder, globalIndexDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidVariableInstruction(opcode))

        val actual = VariableInstructionDecoder(context)

        assertEquals(expected, actual)
    }
}
