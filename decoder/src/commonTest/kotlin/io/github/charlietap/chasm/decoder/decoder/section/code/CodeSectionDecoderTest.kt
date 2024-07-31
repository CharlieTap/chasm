package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.CodeSection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CodeSectionDecoderTest {

    @Test
    fun `can decode an encoded code section`() {

        val entry = CodeEntry(117u, emptyList(), Expression(emptyList()))
        val expectedEntries = listOf(entry, entry)
        val subDecoder: Decoder<CodeEntry> = {
            fail("code entry decoder shouldn't be called directly")
        }
        val vectorDecoder: VectorDecoder<CodeEntry> = { _, _ ->
            Ok(Vector(expectedEntries))
        }
        val context = decoderContext()
        val actual = CodeSectionDecoder(context, subDecoder, vectorDecoder)

        val expectedBodies = List(expectedEntries.size) { index ->
            FunctionBody(Index.FunctionIndex(index.toUInt()), emptyList(), Expression(emptyList()))
        }
        val expected = Ok(CodeSection(expectedBodies))

        assertEquals(expected, actual)
    }
}
