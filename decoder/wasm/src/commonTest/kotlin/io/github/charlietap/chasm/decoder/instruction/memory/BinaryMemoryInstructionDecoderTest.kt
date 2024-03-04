package io.github.charlietap.chasm.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.decoder.instruction.F32_LOAD
import io.github.charlietap.chasm.decoder.instruction.F32_STORE
import io.github.charlietap.chasm.decoder.instruction.F64_LOAD
import io.github.charlietap.chasm.decoder.instruction.F64_STORE
import io.github.charlietap.chasm.decoder.instruction.I32_LOAD
import io.github.charlietap.chasm.decoder.instruction.I32_LOAD16_S
import io.github.charlietap.chasm.decoder.instruction.I32_LOAD16_U
import io.github.charlietap.chasm.decoder.instruction.I32_LOAD8_S
import io.github.charlietap.chasm.decoder.instruction.I32_LOAD8_U
import io.github.charlietap.chasm.decoder.instruction.I32_STORE
import io.github.charlietap.chasm.decoder.instruction.I32_STORE16
import io.github.charlietap.chasm.decoder.instruction.I32_STORE8
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD16_S
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD16_U
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD32_S
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD32_U
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD8_S
import io.github.charlietap.chasm.decoder.instruction.I64_LOAD8_U
import io.github.charlietap.chasm.decoder.instruction.I64_STORE
import io.github.charlietap.chasm.decoder.instruction.I64_STORE16
import io.github.charlietap.chasm.decoder.instruction.I64_STORE32
import io.github.charlietap.chasm.decoder.instruction.I64_STORE8
import io.github.charlietap.chasm.decoder.instruction.MEMORY_GROW
import io.github.charlietap.chasm.decoder.instruction.MEMORY_SIZE
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.reader.FakeByteReader
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryMemoryInstructionDecoderTest {

    @Test
    fun `can decode an MEMORY_SIZE instruction`() {

        val opcode = MEMORY_SIZE
        val expected = Ok(MemoryInstruction.MemorySize)

        var consumedByte = false
        val reader = FakeByteReader {
            consumedByte = true
            Ok(0x00)
        }

        val actual = BinaryMemoryInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
        assertEquals(true, consumedByte)
    }

    @Test
    fun `can decode an MEMORY_GROW instruction`() {

        val opcode = MEMORY_GROW
        val expected = Ok(MemoryInstruction.MemoryGrow)

        var consumedByte = false
        val reader = FakeByteReader {
            consumedByte = true
            Ok(0x00)
        }

        val actual = BinaryMemoryInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
        assertEquals(true, consumedByte)
    }

    @Test
    fun `can decode memarg memory instructions`() {

        val opcodeInstructionMap = mapOf(
            I32_LOAD to MemoryInstruction.I32Load(MemArg(117u, 118u)),
            I64_LOAD to MemoryInstruction.I64Load(MemArg(117u, 118u)),
            F32_LOAD to MemoryInstruction.F32Load(MemArg(117u, 118u)),
            F64_LOAD to MemoryInstruction.F64Load(MemArg(117u, 118u)),
            I32_LOAD8_S to MemoryInstruction.I32Load8S(MemArg(117u, 118u)),
            I32_LOAD8_U to MemoryInstruction.I32Load8U(MemArg(117u, 118u)),
            I32_LOAD16_S to MemoryInstruction.I32Load16S(MemArg(117u, 118u)),
            I32_LOAD16_U to MemoryInstruction.I32Load16U(MemArg(117u, 118u)),
            I64_LOAD8_S to MemoryInstruction.I64Load8S(MemArg(117u, 118u)),
            I64_LOAD8_U to MemoryInstruction.I64Load8U(MemArg(117u, 118u)),
            I64_LOAD16_S to MemoryInstruction.I64Load16S(MemArg(117u, 118u)),
            I64_LOAD16_U to MemoryInstruction.I64Load16U(MemArg(117u, 118u)),
            I64_LOAD32_S to MemoryInstruction.I64Load32S(MemArg(117u, 118u)),
            I64_LOAD32_U to MemoryInstruction.I64Load32U(MemArg(117u, 118u)),
            I32_STORE to MemoryInstruction.I32Store(MemArg(117u, 118u)),
            I64_STORE to MemoryInstruction.I64Store(MemArg(117u, 118u)),
            F32_STORE to MemoryInstruction.F32Store(MemArg(117u, 118u)),
            F64_STORE to MemoryInstruction.F64Store(MemArg(117u, 118u)),
            I32_STORE8 to MemoryInstruction.I32Store8(MemArg(117u, 118u)),
            I32_STORE16 to MemoryInstruction.I32Store16(MemArg(117u, 118u)),
            I64_STORE8 to MemoryInstruction.I64Store8(MemArg(117u, 118u)),
            I64_STORE16 to MemoryInstruction.I64Store16(MemArg(117u, 118u)),
            I64_STORE32 to MemoryInstruction.I64Store32(MemArg(117u, 118u)),
        )

        val memArgDecoder: MemArgDecoder = { _ ->
            Ok(MemArg(117u, 118u))
        }

        opcodeInstructionMap.map { (opcode, instruction) ->
            val expected = Ok(instruction)

            val actual = BinaryMemoryInstructionDecoder(FakeWasmBinaryReader(), opcode, memArgDecoder)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidMemoryInstruction(opcode))

        val actual = BinaryMemoryInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
