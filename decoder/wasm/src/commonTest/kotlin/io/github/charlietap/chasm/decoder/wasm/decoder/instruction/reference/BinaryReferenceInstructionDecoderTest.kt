package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.numeric.BinaryNumericInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.prefixedReferenceInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryReferenceInstructionDecoderTest {

    @Test
    fun `can decode the REF_NULL instruction`() {

        val opcode = REF_NULL
        val reader = FakeUByteReader {
            Ok(117u)
        }
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(AbstractHeapType.Func)
        }
        val expected = Ok(ReferenceInstruction.RefNull(AbstractHeapType.Func))

        val actual = BinaryReferenceInstructionDecoder(
            reader,
            opcode,
            heapTypeDecoder,
            prefixedReferenceInstructionDecoder(),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_ISNULL instruction`() {

        val opcode = REF_ISNULL
        val expected = Ok(ReferenceInstruction.RefIsNull)

        val actual = BinaryReferenceInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_FUNC instruction`() {

        val opcode = REF_FUNC
        val reader = FakeUIntReader {
            Ok(117u)
        }
        val expected = Ok(ReferenceInstruction.RefFunc(Index.FunctionIndex(117u)))

        val actual = BinaryReferenceInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_EQ instruction`() {

        val opcode = REF_EQ
        val expected = Ok(ReferenceInstruction.RefEq)

        val actual = BinaryReferenceInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode the REF_AS_NOT_NULL instruction`() {

        val opcode = REF_AS_NON_NULL
        val expected = Ok(ReferenceInstruction.RefAsNonNull)

        val actual = BinaryReferenceInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `delegates prefixed instructions to the prefixed decoder`() {

        val opcode = PREFIXED_REFERENCE_INSTRUCTION

        val reader = FakeWasmBinaryReader()

        val prefixedReferenceInstruction = prefixedReferenceInstruction()
        val prefixedDecoder: PrefixedReferenceInstructionDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(prefixedReferenceInstruction)
        }
        val expected = Ok(prefixedReferenceInstruction)

        val actual = BinaryReferenceInstructionDecoder(reader, opcode, heapTypeDecoder(), prefixedDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidNumericInstruction(opcode))

        val actual = BinaryNumericInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    companion object {
        private fun heapTypeDecoder(): HeapTypeDecoder = { _ ->
            fail("HeapTypeDecoder should not be called in this scenario")
        }

        private fun prefixedReferenceInstructionDecoder(): PrefixedReferenceInstructionDecoder = { _ ->
            fail("PrefixedReferenceInstruction should not be called in this scenario")
        }
    }
}
