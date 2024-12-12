package io.github.charlietap.chasm.decoder.decoder.section.tag

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.fixture.ast.module.tag
import io.github.charlietap.chasm.fixture.ast.module.tagImport
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.ast.type.tagType
import kotlin.test.Test
import kotlin.test.assertEquals

class TagDecoderTest {

    @Test
    fun `can decode an encoded tag `() {

        val tagType = tagType()
        val tag = tag(
            index = tagIndex(3u),
            type = tagType,
        )
        val expected = Ok(tag)

        val tagTypeDecoder: Decoder<TagType> = { _ ->
            Ok(tagType)
        }

        val context = decoderContext(
            index = 2,
            imports = listOf(
                tagImport(),
            ),
        )
        val actual = TagDecoder(
            context = context,
            typeDecoder = tagTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
