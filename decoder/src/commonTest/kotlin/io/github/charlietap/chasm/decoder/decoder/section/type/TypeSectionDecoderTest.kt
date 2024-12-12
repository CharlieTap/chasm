package io.github.charlietap.chasm.decoder.decoder.section.type

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.TypeSection
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TypeSectionDecoderTest {

    @Test
    fun `can decode an encoded type section`() {

        val context = decoderContext(
            index = 117,
        )

        val type = type(typeIndex(117u))
        val typeDecoder: Decoder<Type> = {
            fail("TypeDecoder should not be called directly")
        }
        val vectorDecoder: VectorDecoder<Type> = { _, _ ->
            Ok(Vector(listOf(type)))
        }

        val actual = TypeSectionDecoder(context, vectorDecoder, typeDecoder)

        val expected = Ok(
            TypeSection(
                listOf(
                    type,
                ),
            ),
        )

        assertEquals(expected, actual)
    }
}
