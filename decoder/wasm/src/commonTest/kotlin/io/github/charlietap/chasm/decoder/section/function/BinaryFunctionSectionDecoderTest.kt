package io.github.charlietap.chasm.decoder.section.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.section.FunctionSection
import io.github.charlietap.chasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryFunctionSectionDecoderTest {

    @Test
    fun `can decode an encoded function section`() {

        val expectedIndices = listOf(Index.TypeIndex(0u), Index.TypeIndex(1u))
        val subDecoder: TypeIndexDecoder = { Ok(Index.TypeIndex(0u)) }
        val vectorDecoder: VectorDecoder<Index.TypeIndex> = { _, sub ->
            assertEquals(subDecoder, sub)
            Ok(Vector(expectedIndices))
        }
        val decoder = BinaryFunctionSectionDecoder(vectorDecoder, subDecoder)

        val expectedHeaders = expectedIndices.mapIndexed { index, typeIndex ->
            FunctionHeader(Index.FunctionIndex(index.toUInt()), typeIndex)
        }
        val expected = Ok(FunctionSection(expectedHeaders))
        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
