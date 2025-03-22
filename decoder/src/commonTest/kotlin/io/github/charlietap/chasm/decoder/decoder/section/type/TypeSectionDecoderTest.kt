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
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TypeSectionDecoderTest {

    @Test
    fun `can decode an encoded type section`() {

        val context = decoderContext(
            index = 117,
        )

        val recursiveType = recursiveType()
        val definedTypes = listOf(definedType())
        val factory: DefinedTypeFactory = { recursiveTypes ->
            assertEquals(listOf(recursiveType), recursiveTypes)
            definedTypes
        }

        val type = type(typeIndex(117u), recursiveType)
        val typeDecoder: Decoder<Type> = {
            fail("TypeDecoder should not be called directly")
        }
        val vectorDecoder: VectorDecoder<Type> = { _, _ ->
            Ok(Vector(listOf(type)))
        }

        val actual = TypeSectionDecoder(context, factory, typeDecoder, vectorDecoder)

        val expected = Ok(
            TypeSection(
                listOf(type),
                definedTypes,
            ),
        )

        assertEquals(expected, actual)
    }
}
