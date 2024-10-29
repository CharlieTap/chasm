package io.github.charlietap.chasm.decoder.decoder.section.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.GlobalSection
import io.github.charlietap.chasm.fixture.type.globalType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class GlobalSectionDecoderTest {

    @Test
    fun `can decode an encoded global section`() {

        val global = Global(
            idx = Index.GlobalIndex(0u),
            type = globalType(),
            initExpression = Expression(),
        )
        val expected = Ok(GlobalSection(listOf(global)))

        val globalDecoder: Decoder<Global> = { _ ->
            fail("GlobalDecoder should not be called directly")
        }

        val vectorDecoder: VectorDecoder<Global> = { _, _ ->
            Ok(Vector(listOf(global)))
        }

        val context = decoderContext()
        val actual = GlobalSectionDecoder(context, vectorDecoder, globalDecoder)

        assertEquals(expected, actual)
    }
}
