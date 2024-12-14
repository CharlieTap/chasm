package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CodeEntryDecoderTest {

    @Test
    fun `can decode a code entry`() {

        val size = 117u
        val localEntries = listOf(
            LocalEntry(3u, i32ValueType()),
            LocalEntry(2u, i64ValueType()),
        )
        val locals = listOf(
            Local(Index.LocalIndex(0u), i32ValueType()),
            Local(Index.LocalIndex(1u), i32ValueType()),
            Local(Index.LocalIndex(2u), i32ValueType()),
            Local(Index.LocalIndex(3u), i64ValueType()),
            Local(Index.LocalIndex(4u), i64ValueType()),
        )
        val expression = Expression(emptyList())

        val expected = Ok(CodeEntry(size, locals, expression))

        val reader = FakeUIntReader {
            Ok(size)
        }
        val context = decoderContext(reader)

        val localEntryDecoder: Decoder<LocalEntry> = { _ ->
            fail("Local decoder shouldn't be called directly")
        }

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(expression)
        }

        val vectorDecoder: VectorDecoder<LocalEntry> = { _, _ ->
            Ok(Vector(localEntries))
        }

        val actual = CodeEntryDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val actual = CodeEntryDecoder(context)

        assertEquals(expected, actual)
    }
}
