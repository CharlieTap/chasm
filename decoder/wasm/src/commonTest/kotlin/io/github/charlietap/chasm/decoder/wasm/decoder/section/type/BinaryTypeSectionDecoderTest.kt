package io.github.charlietap.chasm.decoder.wasm.decoder.section.type

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive.RecursiveTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLength
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.TypeSection
import io.github.charlietap.chasm.fixture.type.recursiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTypeSectionDecoderTest {

    @Test
    fun `can decode an encoded type section`() {

        val sectionSize = SectionSize(14u)

        val vectorLengthDecoder: VectorLengthDecoder = {
            Ok(VectorLength(2u))
        }
        val recursiveType = recursiveType()
        val recursiveTypeDecoder: RecursiveTypeDecoder = {
            Ok(recursiveType)
        }
        val decoder = BinaryTypeSectionDecoder(vectorLengthDecoder, recursiveTypeDecoder)

        val expected = Ok(
            TypeSection(
                listOf(
                    Type(Index.TypeIndex(0u), recursiveType),
                    Type(Index.TypeIndex(1u), recursiveType),
                ),
            ),
        )
        val actual = decoder(FakeWasmBinaryReader(), sectionSize)

        assertEquals(expected, actual)
    }
}
