package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.REF_AS_NON_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.REF_FUNC
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.REF_ISNULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.REF_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.numeric.BinaryNumericInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryReferenceInstructionDecoderTest {

    @Test
    fun `can decode the REF_NULL instruction`() {

        val opcode = REF_NULL
        val reader = FakeUByteReader {
            Ok(117u)
        }
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(HeapType.Func)
        }
        val expected = Ok(ReferenceInstruction.RefNull(HeapType.Func))

        val actual = BinaryReferenceInstructionDecoder(reader, opcode, heapTypeDecoder)

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
    fun `can decode the REF_AS_NOT_NULL instruction`() {

        val opcode = REF_AS_NON_NULL
        val expected = Ok(ReferenceInstruction.RefAsNonNull)

        val actual = BinaryReferenceInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidNumericInstruction(opcode))

        val actual = BinaryNumericInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
