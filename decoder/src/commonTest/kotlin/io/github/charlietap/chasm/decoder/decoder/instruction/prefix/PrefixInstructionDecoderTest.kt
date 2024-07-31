package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.PREFIX_FB
import io.github.charlietap.chasm.decoder.decoder.instruction.PREFIX_FC
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.instruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PrefixInstructionDecoderTest {

    @Test
    fun `delegate FB prefix to FBPrefixDecoder`() {

        val prefix = PREFIX_FB
        val opcode = 117u
        val prefixReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(prefix)
        }
        val opcodeReader = FakeUIntReader {
            Ok(opcode)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = prefixReader,
            fakePeekReader = { opcodeReader },
        )
        val context = decoderContext(reader)
        val instruction = instruction()

        val prefixFBInstructionDecoder: Decoder<Instruction> = { _context ->
            assertEquals(context, _context)
            Ok(instruction)
        }

        val actual = PrefixInstructionDecoder(
            context = context,
            prefixFBInstructionDecoder = prefixFBInstructionDecoder,
            prefixFCInstructionDecoder = neverFCInstructionDecoder,
        )

        assertEquals(Ok(instruction), actual)
    }

    @Test
    fun `delegate FC prefix to FCPrefixDecoder`() {

        val prefix = PREFIX_FC
        val opcode = 117u
        val prefixReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(prefix)
        }
        val opcodeReader = FakeUIntReader {
            Ok(opcode)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = prefixReader,
            fakePeekReader = { opcodeReader },
        )
        val context = decoderContext(reader)
        val instruction = instruction()

        val prefixFCInstructionDecoder: Decoder<Instruction> = { _context ->
            assertEquals(context, _context)
            Ok(instruction)
        }

        val actual = PrefixInstructionDecoder(
            context = context,
            prefixFBInstructionDecoder = neverFBInstructionDecoder,
            prefixFCInstructionDecoder = prefixFCInstructionDecoder,
        )

        assertEquals(Ok(instruction), actual)
    }

    @Test
    fun `returns unknown prefix error when prefix isn't recognised`() {
        val prefix: UByte = 0x00u
        val opcode = 117u
        val prefixReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(prefix)
        }
        val opcodeReader = FakeUIntReader {
            Ok(opcode)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = prefixReader,
            fakePeekReader = { opcodeReader },
        )
        val context = decoderContext(reader)
        val error = InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode)

        val actual = PrefixInstructionDecoder(
            context = context,
            prefixFBInstructionDecoder = neverFBInstructionDecoder,
            prefixFCInstructionDecoder = neverFBInstructionDecoder,
        )

        assertEquals(Err(error), actual)
    }

    companion object {
        private val neverFBInstructionDecoder: Decoder<Instruction> = { _ ->
            fail("FBInstructionDecoder should not be used in this context")
        }
        private val neverFCInstructionDecoder: Decoder<Instruction> = { _ ->
            fail("FBInstructionDecoder should not be used in this context")
        }
    }
}
