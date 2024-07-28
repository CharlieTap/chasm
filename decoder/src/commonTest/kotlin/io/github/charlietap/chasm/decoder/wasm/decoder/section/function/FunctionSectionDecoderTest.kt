package io.github.charlietap.chasm.decoder.wasm.decoder.section.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.function.FunctionHeader
import io.github.charlietap.chasm.decoder.decoder.section.function.FunctionSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.section.FunctionSection
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionSectionDecoderTest {

    @Test
    fun `can decode an encoded function section`() {

        val context = decoderContext()
        val expectedIndices = listOf(Index.TypeIndex(0u), Index.TypeIndex(1u))
        val decoder: Decoder<Index.TypeIndex> = { Ok(Index.TypeIndex(0u)) }
        val vectorDecoder: VectorDecoder<Index.TypeIndex> = { _, _ ->
            Ok(Vector(expectedIndices))
        }

        val actual = FunctionSectionDecoder(context, vectorDecoder, decoder)

        val expectedHeaders = expectedIndices.mapIndexed { index, typeIndex ->
            FunctionHeader(Index.FunctionIndex(index.toUInt()), typeIndex)
        }
        val expected = Ok(FunctionSection(expectedHeaders))

        assertEquals(expected, actual)
    }
}
