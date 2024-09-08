package io.github.charlietap.chasm.decoder.decoder.section.tag

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.fixture.module.tag
import io.github.charlietap.chasm.fixture.type.tagType
import kotlin.test.Test
import kotlin.test.assertEquals

class TagDecoderTest {

    @Test
    fun `can decode an encoded tag `() {

        val tagType = tagType()
        val tag = tag(
            type = tagType,
        )
        val expected = Ok(tag)

        val tagTypeDecoder: Decoder<TagType> = { _ ->
            Ok(tagType)
        }

        val context = decoderContext()
        val actual = TagDecoder(
            context = context,
            typeDecoder = tagTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
