package io.github.charlietap.chasm.decoder.decoder.type.tag

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.type.attribute
import kotlin.test.Test
import kotlin.test.assertEquals

class TagTypeDecoderTest {

    @Test
    fun `can decode an encoded tag type`() {

        val context = decoderContext()

        val attribute = attribute()
        val attributeDecoder: Decoder<TagType.Attribute> = { _context ->
            Ok(attribute)
        }

        val index = typeIndex(117u)
        val typeIndexDecoder: Decoder<Index.TypeIndex> = {
            Ok(index)
        }

        val expected = Ok(TagType(attribute, index))

        val actual = TagTypeDecoder(
            context,
            attributeDecoder,
            typeIndexDecoder,
        )

        assertEquals(expected, actual)
    }
}
