package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.GlobalSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryGlobalSectionDecoderTest {

    @Test
    fun `can decode an encoded global section`() {

        val global1 = Global(
            idx = Index.GlobalIndex(0u),
            type = GlobalType(ValueType.Number(NumberType.I32), GlobalType.Mutability.Var),
            initExpression = Expression(),
        )
        val global2 = Global(
            idx = Index.GlobalIndex(1u),
            type = GlobalType(ValueType.Number(NumberType.I32), GlobalType.Mutability.Var),
            initExpression = Expression(),
        )
        val expected = Ok(GlobalSection(listOf(global1, global2)))

        val indexIter = sequenceOf(global1.idx, global2.idx).iterator()
        val globalDecoder: GlobalDecoder = { _, idx ->
            assertEquals(indexIter.next(), idx)
            Ok(global1)
        }

        val vectorDecoder: VectorDecoder<Global> = { _, _ ->
            Ok(Vector(listOf(global1, global2)))
        }

        val decoder = BinaryGlobalSectionDecoder(vectorDecoder, globalDecoder)

        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
