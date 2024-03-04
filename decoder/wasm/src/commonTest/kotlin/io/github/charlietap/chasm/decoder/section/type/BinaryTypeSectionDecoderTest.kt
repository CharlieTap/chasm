package io.github.charlietap.chasm.decoder.section.type

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.type.function.FunctionTypeDecoder
import io.github.charlietap.chasm.decoder.vector.VectorLength
import io.github.charlietap.chasm.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.TypeSection
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTypeSectionDecoderTest {

    @Test
    fun `can decode an encoded type section`() {

        val sectionSize = SectionSize(14u)
        val params = ResultType(
            listOf(
                ValueType.Vector(VectorType.V128),
                ValueType.Number(NumberType.I32),
            ),
        )

        val results = ResultType(
            listOf(
                ValueType.Vector(VectorType.V128),
                ValueType.Number(NumberType.I64),
            ),
        )
        val functionType = FunctionType(params, results)

        val vectorLengthDecoder: VectorLengthDecoder = {
            Ok(VectorLength(2u))
        }
        val functionTypeDecoder: FunctionTypeDecoder = {
            Ok(functionType)
        }
        val decoder = BinaryTypeSectionDecoder(vectorLengthDecoder, functionTypeDecoder)

        val expected = Ok(
            TypeSection(
                listOf(
                    Type(Index.TypeIndex(0u), functionType),
                    Type(Index.TypeIndex(1u), functionType),
                ),
            ),
        )
        val actual = decoder(FakeWasmBinaryReader(), sectionSize)

        assertEquals(expected, actual)
    }
}
