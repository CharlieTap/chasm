package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.PREFIX_FB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.PREFIX_FC
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.instruction.instruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryPrefixInstructionDecoderTest {

    @Test
    fun `delegate FB prefix to FBPrefixDecoder`() {

        val prefix = PREFIX_FB
        val opcode = 117u
        val reader = FakeUIntReader { Ok(opcode) }
        val instruction = instruction()

        val prefixFBInstructionDecoder: PrefixFBInstructionDecoder = { _reader, _opcode ->
            assertEquals(reader, _reader)
            assertEquals(opcode, _opcode)
            Ok(instruction)
        }

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = prefix,
            prefixFBInstructionDecoder = prefixFBInstructionDecoder,
            prefixFCInstructionDecoder = neverFCInstructionDecoder,
        )

        assertEquals(Ok(instruction), actual)
    }

    @Test
    fun `delegate FC prefix to FCPrefixDecoder`() {

        val prefix = PREFIX_FC
        val opcode = 117u
        val reader = FakeUIntReader { Ok(opcode) }
        val instruction = instruction()

        val prefixFCInstructionDecoder: PrefixFCInstructionDecoder = { _reader, _opcode ->
            assertEquals(reader, _reader)
            assertEquals(opcode, _opcode)
            Ok(instruction)
        }

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = prefix,
            prefixFBInstructionDecoder = neverFBInstructionDecoder,
            prefixFCInstructionDecoder = prefixFCInstructionDecoder,
        )

        assertEquals(Ok(instruction), actual)
    }

    @Test
    fun `returns unknown prefix error when prefix isn't recognised`() {
        val prefix: UByte = 0x00u
        val opcode = 117u
        val reader = FakeUIntReader { Ok(opcode) }
        val error = InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode)

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = prefix,
            prefixFBInstructionDecoder = neverFBInstructionDecoder,
            prefixFCInstructionDecoder = neverFBInstructionDecoder,
        )

        assertEquals(Err(error), actual)
    }

    companion object {
        private val neverFBInstructionDecoder: PrefixFBInstructionDecoder = { _, _ ->
            fail("FBInstructionDecoder should not be used in this context")
        }
        private val neverFCInstructionDecoder: PrefixFCInstructionDecoder = { _, _ ->
            fail("FBInstructionDecoder should not be used in this context")
        }
    }
}
