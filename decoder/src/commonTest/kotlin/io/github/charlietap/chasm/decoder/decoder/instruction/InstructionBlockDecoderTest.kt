package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionBlockDecoderTest {

    @Test
    fun `can decode an expression`() {

        val byteStream = sequenceOf(I32_EQZ, END).iterator()

        val opcodeReader = FakeUByteReader {
            Ok(byteStream.next())
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = { Ok(END) }, // consume end
            fakePeekReader = { opcodeReader },
        )
        val context = decoderContext(
            reader = reader,
            blockEndOpcode = END,
        )

        val instructionDecoder: Decoder<Instruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(NumericInstruction.I32Eqz)
        }
        val expected = Ok(listOf(NumericInstruction.I32Eqz))

        val actual = InstructionBlockDecoder(context, instructionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = InstructionBlockDecoder(context)

        assertEquals(err, actual)
    }
}
