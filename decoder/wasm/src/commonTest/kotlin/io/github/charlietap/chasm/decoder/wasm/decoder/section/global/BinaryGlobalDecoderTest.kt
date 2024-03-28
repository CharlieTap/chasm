package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryGlobalDecoderTest {

    @Test
    fun `can decode an encoded global`() {

        val globalType = GlobalType(
            valueType = ValueType.Number(NumberType.I32),
            mutability = GlobalType.Mutability.Var,
        )
        val globalTypeDecoder: GlobalTypeDecoder = { _ ->
            Ok(globalType)
        }

        val expression = Expression()
        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(expression)
        }

        val index = Index.GlobalIndex(177u)
        val expected = Ok(Global(index, globalType, expression))
        val actual = BinaryGlobalDecoder(FakeWasmBinaryReader(), index, globalTypeDecoder, expressionDecoder)

        assertEquals(expected, actual)
    }
}
