package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.ELSE
import io.github.charlietap.chasm.decoder.decoder.instruction.END
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_EQZ
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_GROW
import io.github.charlietap.chasm.decoder.decoder.instruction.control.IfDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.instruction.memoryGrowInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class IfDecoderTest {

    @Test
    fun `can decode an if instruction block`() {

        val byteStream = sequenceOf(I32_EQZ, END).iterator()
        val peekReader = FakeUByteReader {
            Ok(byteStream.next())
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = { Ok(END) },
            fakePeekReader = { peekReader },
        )
        val context = decoderContext(reader)

        val instructionDecoder: Decoder<Instruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(NumericInstruction.I32Eqz)
        }
        val expected = Ok(listOf(NumericInstruction.I32Eqz) to null)

        val actual = IfDecoder(context, instructionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an if else instruction block`() {

        val byteStream = sequenceOf(I32_EQZ, ELSE, MEMORY_GROW, END).iterator()
        val endByteStream = sequenceOf(ELSE, END).iterator()
        val peekReader = FakeUByteReader {
            Ok(byteStream.next())
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = { Ok(endByteStream.next()) },
            fakePeekReader = { peekReader },
        )
        val context = decoderContext(reader)

        val instructions = sequenceOf(NumericInstruction.I32Eqz, memoryGrowInstruction()).iterator()
        val instructionDecoder: Decoder<Instruction> = { ctx ->
            Ok(instructions.next())
        }
        val expected = Ok(listOf(NumericInstruction.I32Eqz) to listOf(memoryGrowInstruction()))

        val actual = IfDecoder(context, instructionDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = IfDecoder(context)

        assertEquals(err, actual)
    }
}
