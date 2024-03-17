package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryCodeEntryDecoderTest {

    @Test
    fun `can decode a code entry`() {

        val size = 117u
        val localEntries = listOf(
            LocalEntry(3u, ValueType.Number(NumberType.I32)),
            LocalEntry(2u, ValueType.Number(NumberType.I64)),
        )
        val locals = listOf(
            Local(Index.LocalIndex(0u), ValueType.Number(NumberType.I32)),
            Local(Index.LocalIndex(1u), ValueType.Number(NumberType.I32)),
            Local(Index.LocalIndex(2u), ValueType.Number(NumberType.I32)),
            Local(Index.LocalIndex(3u), ValueType.Number(NumberType.I64)),
            Local(Index.LocalIndex(4u), ValueType.Number(NumberType.I64)),
        )
        val expression = Expression(emptyList())

        val expected = Ok(CodeEntry(size, locals, expression))

        val reader = FakeUIntReader {
            Ok(size)
        }

        val localEntryDecoder: LocalEntryDecoder = { _ ->
            fail("Local decoder shouldn't be called directly")
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(expression)
        }

        val vectorDecoder: VectorDecoder<LocalEntry> = { _, sub ->
            assertEquals(localEntryDecoder, sub)
            Ok(Vector(localEntries))
        }

        val actual = BinaryCodeEntryDecoder(
            reader = reader,
            localEntryDecoder = localEntryDecoder,
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
