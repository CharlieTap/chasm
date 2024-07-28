package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.global.GlobalSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.section.GlobalSection
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.fixture.module.globalIndex
import io.github.charlietap.chasm.fixture.type.globalType
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalSectionDecoderTest {

    @Test
    fun `can decode an encoded global section`() {

        val global1 = Global(
            idx = Index.GlobalIndex(0u),
            type = globalType(),
            initExpression = Expression(),
        )
        val global2 = Global(
            idx = Index.GlobalIndex(0u),
            type = globalType(),
            initExpression = Expression(),
        )
        val expected = Ok(GlobalSection(listOf(global1, global2.copy(idx = globalIndex(1u)))))

        val globalDecoder: Decoder<Global> = { _ ->
            Ok(global1)
        }

        val vectorDecoder: VectorDecoder<Global> = { _, _ ->
            Ok(Vector(listOf(global1, global2)))
        }

        val context = decoderContext()
        val actual = GlobalSectionDecoder(context, vectorDecoder, globalDecoder)

        assertEquals(expected, actual)
    }
}
