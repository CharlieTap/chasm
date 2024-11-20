package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.atomicMemoryInstruction
import io.github.charlietap.chasm.fixture.instruction.instruction
import io.github.charlietap.chasm.fixture.instruction.vectorInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PrefixedInstructionDecoderTest {

    @Test
    fun `delegate FB prefix to GCInstructionDecoder`() {

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

        val gcInstructionDecoder: Decoder<Instruction> = { _context ->
            assertEquals(context, _context)
            Ok(instruction)
        }

        val actual = PrefixedInstructionDecoder(
            context = context,
            prefixFCInstructionDecoder = neverFCInstructionDecoder,
            atomicMemoryInstructionDecoder = neverAtomicInstructionDecoder,
            gcInstructionDecoder = gcInstructionDecoder,
            vectorInstructionDecoder = neverVectorInstructionDecoder,
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

        val actual = PrefixedInstructionDecoder(
            context = context,
            prefixFCInstructionDecoder = prefixFCInstructionDecoder,
            atomicMemoryInstructionDecoder = neverAtomicInstructionDecoder,
            gcInstructionDecoder = neverGCInstructionDecoder,
            vectorInstructionDecoder = neverVectorInstructionDecoder,
        )

        assertEquals(Ok(instruction), actual)
    }

    @Test
    fun `delegate FD prefix to VectorInstructionDecoder`() {

        val prefix = PREFIX_FD
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
        val instruction = vectorInstruction()

        val vectorInstructionDecoder: Decoder<VectorInstruction> = { _context ->
            assertEquals(context, _context)
            Ok(instruction)
        }

        val actual = PrefixedInstructionDecoder(
            context = context,
            prefixFCInstructionDecoder = neverFCInstructionDecoder,
            atomicMemoryInstructionDecoder = neverAtomicInstructionDecoder,
            gcInstructionDecoder = neverGCInstructionDecoder,
            vectorInstructionDecoder = vectorInstructionDecoder,
        )

        assertEquals(Ok(instruction), actual)
    }

    @Test
    fun `delegate FE prefix to AtomicMemoryInstructionDecoder`() {

        val prefix = PREFIX_FE
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
        val instruction = atomicMemoryInstruction()

        val atomicMemoryInstructionDecoder: Decoder<AtomicMemoryInstruction> = { _context ->
            assertEquals(context, _context)
            Ok(instruction)
        }

        val actual = PrefixedInstructionDecoder(
            context = context,
            prefixFCInstructionDecoder = neverFCInstructionDecoder,
            atomicMemoryInstructionDecoder = atomicMemoryInstructionDecoder,
            gcInstructionDecoder = neverGCInstructionDecoder,
            vectorInstructionDecoder = neverVectorInstructionDecoder,
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

        val actual = PrefixedInstructionDecoder(
            context = context,
            prefixFCInstructionDecoder = neverFCInstructionDecoder,
            atomicMemoryInstructionDecoder = neverAtomicInstructionDecoder,
            gcInstructionDecoder = neverGCInstructionDecoder,
            vectorInstructionDecoder = neverVectorInstructionDecoder,
        )

        assertEquals(Err(error), actual)
    }

    companion object {
        private val neverAtomicInstructionDecoder: Decoder<AtomicMemoryInstruction> = { _ ->
            fail("AtomicMemoryInstructionDecoder should not be used in this context")
        }
        private val neverGCInstructionDecoder: Decoder<Instruction> = { _ ->
            fail("FBInstructionDecoder should not be used in this context")
        }
        private val neverFCInstructionDecoder: Decoder<Instruction> = { _ ->
            fail("FBInstructionDecoder should not be used in this context")
        }
        private val neverVectorInstructionDecoder: Decoder<VectorInstruction> = { _ ->
            fail("VectorDecoder should not be used in this context")
        }
    }
}
