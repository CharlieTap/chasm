package io.github.charlietap.chasm.decoder.wasm.decoder.section.start

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.section.StartSection
import kotlin.test.Test
import kotlin.test.assertEquals

class StartSectionDecoderTest {

    @Test
    fun `can decode an encoded start section`() {

        val funcIdx = Index.FunctionIndex(117u)
        val startFunction = StartFunction(funcIdx)
        val expected = Ok(StartSection(startFunction))

        val functionIndexDecoder: Decoder<Index.FunctionIndex> = { _ ->
            Ok(funcIdx)
        }

        val context = decoderContext()
        val actual = StartSectionDecoder(context, functionIndexDecoder)

        assertEquals(expected, actual)
    }
}
