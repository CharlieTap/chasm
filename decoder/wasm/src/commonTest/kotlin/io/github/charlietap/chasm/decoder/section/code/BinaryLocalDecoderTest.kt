package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.section.index.LocalIndexDecoder
import io.github.charlietap.chasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryLocalDecoderTest {

    @Test
    fun `can decode a local`() {

        val valueType = ValueType.Number(NumberType.I32)

        val localIndex = Index.LocalIndex(117u)
        val expected = Ok(Local(localIndex, valueType))

        val localIndexDecoder: LocalIndexDecoder = {
            Ok(localIndex)
        }

        val valueTypeDecoder: ValueTypeDecoder = {
            Ok(valueType)
        }

        val actual = BinaryLocalDecoder(
            reader = FakeWasmBinaryReader(),
            localIndexDecoder = localIndexDecoder,
            valueTypeDecoder = valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
