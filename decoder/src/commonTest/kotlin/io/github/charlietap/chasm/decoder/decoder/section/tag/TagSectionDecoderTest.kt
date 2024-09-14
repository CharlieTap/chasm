package io.github.charlietap.chasm.decoder.decoder.section.tag

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.TagSection
import io.github.charlietap.chasm.fixture.module.tag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TagSectionDecoderTest {

    @Test
    fun `can decode an encoded tag section`() {

        val tag = tag()
        val tagSection = TagSection(
            listOf(
                tag.copy(Index.TagIndex(0u)),
                tag.copy(Index.TagIndex(1u)),
            ),
        )
        val expected = Ok(tagSection)

        val tagDecoder: Decoder<Tag> = { _ ->
            fail("tag decoder should not be called in this scenario")
        }

        val vectorDecoder: VectorDecoder<Tag> = { _, _ ->
            Ok(Vector(listOf(tag, tag)))
        }

        val context = decoderContext()
        val actual = TagSectionDecoder(
            context = context,
            tagDecoder = tagDecoder,
            vectorDecoder = vectorDecoder,
        )

        assertEquals(expected, actual)
    }
}
