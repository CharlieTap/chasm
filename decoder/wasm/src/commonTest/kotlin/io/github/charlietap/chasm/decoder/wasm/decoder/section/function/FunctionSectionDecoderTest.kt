package io.github.charlietap.chasm.decoder.wasm.decoder.section.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.section.FunctionSection
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
