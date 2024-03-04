package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeUIntReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryCodeEntryDecoderTest {

    @Test
    fun `can decode a code entry`() {

        val size = 117u
        val locals = emptyList<Local>()
        val expression = Expression(emptyList())

        val expected = Ok(CodeEntry(size, locals, expression))

        val reader = FakeUIntReader {
            Ok(size)
        }

        val localDecoder: LocalDecoder = { _ ->
            fail("Local decoder shouldn't be called directly")
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(expression)
        }

        val vectorDecoder: VectorDecoder<Local> = { _, sub ->
            assertEquals(localDecoder, sub)
            Ok(Vector(locals))
        }

        val actual = BinaryCodeEntryDecoder(
            reader = reader,
            localDecoder = localDecoder,
            expressionDecoder = expressionDecoder,
            vectorDecoder = vectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryCodeEntryDecoder(reader)

        assertEquals(expected, actual)
    }
}
