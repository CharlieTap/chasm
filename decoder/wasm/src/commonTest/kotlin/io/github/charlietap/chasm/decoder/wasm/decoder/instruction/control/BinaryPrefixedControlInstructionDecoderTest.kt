package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.module.labelIndex
import io.github.charlietap.chasm.fixture.type.heapType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryPrefixedControlInstructionDecoderTest {

    @Test
    fun `can decode an encoded br on cast instruction`() {

        val reader = FakeUIntReader { Ok(BR_ON_CAST) }

        val labelIndex = labelIndex()
        val labelIndexDecoder: LabelIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(labelIndex)
        }

        val castFlags = CastFlags(Nullability.NULL, Nullability.NON_NULL)
        val castFlagsDecoder: CastFlagsDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(castFlags)
        }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ControlInstruction.BrOnCast(
            labelIndex,
            ReferenceType.RefNull(heapType),
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixedControlInstructionDecoder(
            reader = reader,
            labelIndexDecoder = labelIndexDecoder,
            castFlagsDecoder = castFlagsDecoder,
            heapTypeDecoder = heapTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded br on cast fail instruction`() {

        val reader = FakeUIntReader { Ok(BR_ON_CAST_FAIL) }

        val labelIndex = labelIndex()
        val labelIndexDecoder: LabelIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(labelIndex)
        }

        val castFlags = CastFlags(Nullability.NULL, Nullability.NON_NULL)
        val castFlagsDecoder: CastFlagsDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(castFlags)
        }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ControlInstruction.BrOnCastFail(
            labelIndex,
            ReferenceType.RefNull(heapType),
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixedControlInstructionDecoder(
            reader = reader,
            labelIndexDecoder = labelIndexDecoder,
            castFlagsDecoder = castFlagsDecoder,
            heapTypeDecoder = heapTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }
}
