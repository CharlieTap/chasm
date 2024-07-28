package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.END
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionDecoderTest {

    @Test
    fun `can decode an expression`() {

        val context = decoderContext()
        val scope: Scope<UByte> = { ctx, opcode ->
            assertEquals(END, opcode)
            assertEquals(context, ctx)
            Ok(context)
        }
        val instructionBlockDecoder: Decoder<List<Instruction>> = { ctx ->
            assertEquals(context, ctx)
            Ok(listOf(NumericInstruction.I32Eqz))
        }
        val expected = Ok(Expression(NumericInstruction.I32Eqz))

        val actual = io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder(
            context,
            scope,
            instructionBlockDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder(context)

        assertEquals(err, actual)
    }
}
