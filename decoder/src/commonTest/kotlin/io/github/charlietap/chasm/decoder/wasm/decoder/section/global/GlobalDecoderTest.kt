package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.global.GlobalDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.fixture.module.globalIndex
import io.github.charlietap.chasm.fixture.type.globalType
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalDecoderTest {

    @Test
    fun `can decode an encoded global`() {

        val context = decoderContext()

        val globalType = globalType()
        val globalTypeDecoder: Decoder<GlobalType> = { _ ->
            Ok(globalType)
        }

        val expression = Expression()
        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(expression)
        }

        val index = globalIndex(0u)
        val expected = Ok(Global(index, globalType, expression))
        val actual = GlobalDecoder(context, globalTypeDecoder, expressionDecoder)

        assertEquals(expected, actual)
    }
}
