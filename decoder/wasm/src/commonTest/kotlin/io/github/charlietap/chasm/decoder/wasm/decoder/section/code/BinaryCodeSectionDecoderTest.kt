package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.CodeSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryCodeSectionDecoderTest {

    @Test
    fun `can decode an encoded code section`() {

        val entry = CodeEntry(117u, emptyList(), Expression(emptyList()))
        val expectedEntries = listOf(entry, entry)
        val subDecoder: CodeEntryDecoder = {
            fail("code entry decoder shouldn't be called directly")
        }
        val vectorDecoder: VectorDecoder<CodeEntry> = { _, sub ->
            assertEquals(subDecoder, sub)
            Ok(Vector(expectedEntries))
        }
        val decoder = BinaryCodeSectionDecoder(subDecoder, vectorDecoder)

        val expectedBodies = List(expectedEntries.size) { index ->
            FunctionBody(Index.FunctionIndex(index.toUInt()), emptyList(), Expression(emptyList()))
        }
        val expected = Ok(CodeSection(expectedBodies))
        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
