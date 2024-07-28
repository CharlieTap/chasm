package io.github.charlietap.chasm.decoder.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.numeric.NumericInstructionDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ReferenceInstructionDecoderTest {

    @Test
    fun `can decode the REF_NULL instruction`() {

        val opcode = REF_NULL
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)
        val heapTypeDecoder: Decoder<HeapType> = { _context ->
            assertEquals(context, _context)
            Ok(AbstractHeapType.Func)
        }
        val expected = Ok(ReferenceInstruction.RefNull(AbstractHeapType.Func))

        val actual = ReferenceInstructionDecoder(
            context,
            heapTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_ISNULL instruction`() {

        val opcode = REF_ISNULL
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)
        val expected = Ok(ReferenceInstruction.RefIsNull)

        val actual = ReferenceInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_FUNC instruction`() {

        val opcode = REF_FUNC
        val opcodeReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }
        val funcIndexReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(117u)
        }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = opcodeReader,
            fakeUIntReader = funcIndexReader,
        )
        val context = decoderContext(reader)
        val expected = Ok(ReferenceInstruction.RefFunc(Index.FunctionIndex(117u)))

        val actual = ReferenceInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_EQ instruction`() {

        val opcode = REF_EQ
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)
        val expected = Ok(ReferenceInstruction.RefEq)

        val actual = ReferenceInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_AS_NOT_NULL instruction`() {

        val opcode = REF_AS_NON_NULL
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)
        val expected = Ok(ReferenceInstruction.RefAsNonNull)

        val actual = ReferenceInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidNumericInstruction(opcode))

        val actual = NumericInstructionDecoder(context)

        assertEquals(expected, actual)
    }
}
