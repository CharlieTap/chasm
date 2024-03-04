package io.github.charlietap.chasm.decoder.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeUByteReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryInstructionBlockDecoderTest {

    @Test
    fun `can decode an expression`() {

        val byteStream = sequenceOf(I32_EQZ, END).iterator()
        val reader = FakeUByteReader {
            Ok(byteStream.next())
        }

        val instructionDecoder: InstructionDecoder = { _, byte ->
            assertEquals(I32_EQZ, byte)
            Ok(NumericInstruction.I32Eqz)
        }
        val expected = Ok(listOf(NumericInstruction.I32Eqz))

        val actual = BinaryInstructionBlockDecoder(reader, END, instructionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = BinaryExpressionDecoder(reader)

        assertEquals(err, actual)
    }
}
