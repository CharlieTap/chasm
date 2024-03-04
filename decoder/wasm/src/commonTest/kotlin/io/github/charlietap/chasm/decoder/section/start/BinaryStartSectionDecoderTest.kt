package io.github.charlietap.chasm.decoder.section.start

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.StartSection
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryStartSectionDecoderTest {

    @Test
    fun `can decode an encoded start section`() {

        val funcIdx = Index.FunctionIndex(117u)
        val startFunction = StartFunction(funcIdx)
        val expected = Ok(StartSection(startFunction))

        val functionIndexDecoder: FunctionIndexDecoder = { _ ->
            Ok(funcIdx)
        }

        val decoder = BinaryStartSectionDecoder(functionIndexDecoder)

        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
