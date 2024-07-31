package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.F32_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.F32_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.F64_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.F64_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD16_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD16_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD8_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_LOAD8_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_STORE16
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_STORE8
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD16_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD16_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD32_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD32_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD8_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_LOAD8_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE16
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE32
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_STORE8
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_GROW
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_SIZE
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.memArg
import io.github.charlietap.chasm.fixture.instruction.memoryGrowInstruction
import io.github.charlietap.chasm.fixture.instruction.memorySizeInstruction
import io.github.charlietap.chasm.fixture.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MemoryInstructionDecoderTest {

    @Test
    fun `can decode an MEMORY_SIZE instruction`() {

        val opcode = MEMORY_SIZE
        val expected = Ok(memorySizeInstruction())

        val memorySizeDecoder: Decoder<MemoryInstruction.MemorySize> = {
            Ok(memorySizeInstruction())
        }

        val fakeOpcodeReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = fakeOpcodeReader,
        )
        val context = decoderContext(reader)

        val actual = MemoryInstructionDecoder(
            context = context,
            memArgWithIndexDecoder = neverMemArgWithIndexDecoder,
            memoryGrowDecoder = neverMemoryGrowDecoder,
            memorySizeDecoder = memorySizeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an MEMORY_GROW instruction`() {

        val opcode = MEMORY_GROW
        val expected = Ok(memoryGrowInstruction())

        val memoryGrowDecoder: Decoder<MemoryInstruction.MemoryGrow> = {
            Ok(memoryGrowInstruction())
        }

        val fakeOpcodeReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = fakeOpcodeReader,
        )
        val context = decoderContext(reader)

        val actual = MemoryInstructionDecoder(
            context = context,
            memArgWithIndexDecoder = neverMemArgWithIndexDecoder,
            memoryGrowDecoder = memoryGrowDecoder,
            memorySizeDecoder = neverMemorySizeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode memarg memory instructions`() {

        val expectedMemoryIndex = memoryIndex(117u)
        val expectedMemArg = memArg(118u, 119u)

        val opcodeInstructionMap = mapOf(
            I32_LOAD to MemoryInstruction.I32Load(expectedMemoryIndex, expectedMemArg),
            I64_LOAD to MemoryInstruction.I64Load(expectedMemoryIndex, expectedMemArg),
            F32_LOAD to MemoryInstruction.F32Load(expectedMemoryIndex, expectedMemArg),
            F64_LOAD to MemoryInstruction.F64Load(expectedMemoryIndex, expectedMemArg),
            I32_LOAD8_S to MemoryInstruction.I32Load8S(expectedMemoryIndex, expectedMemArg),
            I32_LOAD8_U to MemoryInstruction.I32Load8U(expectedMemoryIndex, expectedMemArg),
            I32_LOAD16_S to MemoryInstruction.I32Load16S(expectedMemoryIndex, expectedMemArg),
            I32_LOAD16_U to MemoryInstruction.I32Load16U(expectedMemoryIndex, expectedMemArg),
            I64_LOAD8_S to MemoryInstruction.I64Load8S(expectedMemoryIndex, expectedMemArg),
            I64_LOAD8_U to MemoryInstruction.I64Load8U(expectedMemoryIndex, expectedMemArg),
            I64_LOAD16_S to MemoryInstruction.I64Load16S(expectedMemoryIndex, expectedMemArg),
            I64_LOAD16_U to MemoryInstruction.I64Load16U(expectedMemoryIndex, expectedMemArg),
            I64_LOAD32_S to MemoryInstruction.I64Load32S(expectedMemoryIndex, expectedMemArg),
            I64_LOAD32_U to MemoryInstruction.I64Load32U(expectedMemoryIndex, expectedMemArg),
            I32_STORE to MemoryInstruction.I32Store(expectedMemoryIndex, expectedMemArg),
            I64_STORE to MemoryInstruction.I64Store(expectedMemoryIndex, expectedMemArg),
            F32_STORE to MemoryInstruction.F32Store(expectedMemoryIndex, expectedMemArg),
            F64_STORE to MemoryInstruction.F64Store(expectedMemoryIndex, expectedMemArg),
            I32_STORE8 to MemoryInstruction.I32Store8(expectedMemoryIndex, expectedMemArg),
            I32_STORE16 to MemoryInstruction.I32Store16(expectedMemoryIndex, expectedMemArg),
            I64_STORE8 to MemoryInstruction.I64Store8(expectedMemoryIndex, expectedMemArg),
            I64_STORE16 to MemoryInstruction.I64Store16(expectedMemoryIndex, expectedMemArg),
            I64_STORE32 to MemoryInstruction.I64Store32(expectedMemoryIndex, expectedMemArg),
        )

        val memArgWithIndexDecoder: Decoder<MemArgWithIndex> = {
            Ok(MemArgWithIndex(expectedMemoryIndex, expectedMemArg))
        }

        var opcode: UByte = 0u
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        opcodeInstructionMap.map { (_opcode, _instruction) ->
            opcode = _opcode
            val expected = Ok(_instruction)

            val actual = MemoryInstructionDecoder(
                context = context,
                memArgWithIndexDecoder = memArgWithIndexDecoder,
                memoryGrowDecoder = neverMemoryGrowDecoder,
                memorySizeDecoder = neverMemorySizeDecoder,
            )
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidMemoryInstruction(opcode))

        val actual = MemoryInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    private companion object {
        private val neverMemArgWithIndexDecoder: Decoder<MemArgWithIndex> = { _ ->
            fail("memarg with index decoder should not run in this scenario")
        }
        private val neverMemoryGrowDecoder: Decoder<MemoryInstruction.MemoryGrow> = { _ ->
            fail("memory grow decoder should not run in this scenario")
        }
        private val neverMemorySizeDecoder: Decoder<MemoryInstruction.MemorySize> = { _ ->
            fail("memory size decoder should not run in this scenario")
        }
    }
}
