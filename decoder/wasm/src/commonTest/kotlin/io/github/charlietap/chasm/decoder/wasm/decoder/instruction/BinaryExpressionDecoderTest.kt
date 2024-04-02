package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryExpressionDecoderTest {

    @Test
    fun `can decode an expression`() {

        val instructionBlockDecoder: InstructionBlockDecoder = { _, endBlockOp ->
            assertEquals(END, endBlockOp)
            Ok(listOf(NumericInstruction.I32Eqz))
        }
        val expected = Ok(Expression(NumericInstruction.I32Eqz))

        val actual = BinaryExpressionDecoder(
            FakeWasmBinaryReader(),
            instructionBlockDecoder,
        )

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
