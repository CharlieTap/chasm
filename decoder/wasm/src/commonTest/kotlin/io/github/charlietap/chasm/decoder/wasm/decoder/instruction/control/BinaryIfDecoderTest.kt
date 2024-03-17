package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ELSE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.END
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_EQZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.InstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.MEMORY_GROW
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryIfDecoderTest {

    @Test
    fun `can decode an if instruction block`() {

        val byteStream = sequenceOf(I32_EQZ, END).iterator()
        val reader = FakeUByteReader {
            Ok(byteStream.next())
        }

        val instructionDecoder: InstructionDecoder = { _, byte ->
            assertEquals(I32_EQZ, byte)
            Ok(NumericInstruction.I32Eqz)
        }
        val expected = Ok(listOf(NumericInstruction.I32Eqz) to null)

        val actual = BinaryIfDecoder(reader, instructionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an if else instruction block`() {

        val byteStream = sequenceOf(I32_EQZ, ELSE, MEMORY_GROW, END).iterator()
        val reader = FakeUByteReader {
            Ok(byteStream.next())
        }

        val instructionDecoder: InstructionDecoder = { _, byte ->
            if (byte == I32_EQZ) {
                Ok(NumericInstruction.I32Eqz)
            } else {
                Ok(MemoryInstruction.MemoryGrow)
            }
        }
        val expected = Ok(listOf(NumericInstruction.I32Eqz) to listOf(MemoryInstruction.MemoryGrow))

        val actual = BinaryIfDecoder(reader, instructionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BinaryExpressionDecoder(reader)

        assertEquals(err, actual)
    }
}
