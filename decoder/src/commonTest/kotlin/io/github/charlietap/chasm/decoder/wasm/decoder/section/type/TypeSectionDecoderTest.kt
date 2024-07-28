package io.github.charlietap.chasm.decoder.wasm.decoder.section.type

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.type.TypeSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorLength
import io.github.charlietap.chasm.decoder.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.section.TypeSection
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.fixture.type.recursiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeSectionDecoderTest {

    @Test
    fun `can decode an encoded type section`() {

        val vectorLengthDecoder: VectorLengthDecoder = {
            Ok(VectorLength(2u))
        }
        val recursiveType = recursiveType()
        val recursiveTypeDecoder: Decoder<RecursiveType> = {
            Ok(recursiveType)
        }
        val context = decoderContext()
        val actual = TypeSectionDecoder(context, vectorLengthDecoder, recursiveTypeDecoder)

        val expected = Ok(
            TypeSection(
                listOf(
                    Type(Index.TypeIndex(0u), recursiveType),
                    Type(Index.TypeIndex(1u), recursiveType),
                ),
            ),
        )

        assertEquals(expected, actual)
    }
}
