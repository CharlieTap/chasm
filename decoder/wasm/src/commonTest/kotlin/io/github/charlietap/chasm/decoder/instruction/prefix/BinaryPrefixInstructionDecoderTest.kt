package io.github.charlietap.chasm.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.decoder.instruction.DATA_DROP
import io.github.charlietap.chasm.decoder.instruction.ELEM_DROP
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F32_S
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F64_S
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F32_S
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F64_S
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.decoder.instruction.MEMORY_COPY
import io.github.charlietap.chasm.decoder.instruction.MEMORY_FILL
import io.github.charlietap.chasm.decoder.instruction.MEMORY_INIT
import io.github.charlietap.chasm.decoder.instruction.PREFIX_MISC
import io.github.charlietap.chasm.decoder.instruction.TABLE_COPY
import io.github.charlietap.chasm.decoder.instruction.TABLE_FILL
import io.github.charlietap.chasm.decoder.instruction.TABLE_GROW
import io.github.charlietap.chasm.decoder.instruction.TABLE_INIT
import io.github.charlietap.chasm.decoder.instruction.TABLE_SIZE
import io.github.charlietap.chasm.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.reader.FakeUIntReader
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryPrefixInstructionDecoderTest {

    @Test
    fun `can decode prefixed numeric instructions`() {

        val opcodeMap = mapOf(
            I32_TRUNC_SAT_F32_S to NumericInstruction.I32TruncSatF32S,
            I32_TRUNC_SAT_F32_U to NumericInstruction.I32TruncSatF32U,
            I32_TRUNC_SAT_F64_S to NumericInstruction.I32TruncSatF64S,
            I32_TRUNC_SAT_F64_U to NumericInstruction.I32TruncSatF64U,
            I64_TRUNC_SAT_F32_S to NumericInstruction.I64TruncSatF32S,
            I64_TRUNC_SAT_F32_U to NumericInstruction.I64TruncSatF32U,
            I64_TRUNC_SAT_F64_S to NumericInstruction.I64TruncSatF64S,
            I64_TRUNC_SAT_F64_U to NumericInstruction.I64TruncSatF64U,
        )

        val opcodeIter = opcodeMap.keys.iterator()
        val reader = FakeUIntReader {
            Ok(opcodeIter.next())
        }

        opcodeMap.values.forEach { expected ->
            val actual = BinaryPrefixInstructionDecoder(reader, PREFIX_MISC)

            assertEquals(Ok(expected), actual)
        }
    }

    @Test
    fun `can decode prefixed MEMORY_INIT instruction`() {

        val opcode = MEMORY_INIT

        val expectedDataIndex = Index.DataIndex(117u)

        val dataIndexDecoder: DataIndexDecoder = { _ ->
            Ok(expectedDataIndex)
        }

        var consumedEmptyByte = false
        val reader = FakeWasmBinaryReader(
            fakeByteReader = {
                consumedEmptyByte = true
                Ok(0x00)
            },
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expected = Ok(MemoryInstruction.MemoryInit(expectedDataIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
        assertEquals(true, consumedEmptyByte)
    }

    @Test
    fun `can decode prefixed DATA_DROP instruction`() {

        val opcode = DATA_DROP

        val expectedDataIndex = Index.DataIndex(117u)

        val dataIndexDecoder: DataIndexDecoder = { _ ->
            Ok(expectedDataIndex)
        }

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expected = Ok(MemoryInstruction.DataDrop(expectedDataIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed MEMORY_COPY instruction`() {

        val opcode = MEMORY_COPY

        var consumedEmptyBytes = false
        val reader = FakeWasmBinaryReader(
            fakeBytesReader = { amount ->
                consumedEmptyBytes = true
                assertEquals(2, amount)
                Ok(byteArrayOf(0x00, 0x00))
            },
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expected = Ok(MemoryInstruction.MemoryCopy)

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
        assertEquals(true, consumedEmptyBytes)
    }

    @Test
    fun `can decode prefixed MEMORY_FILL instruction`() {

        val opcode = MEMORY_FILL

        var consumedEmptyByte = false
        val reader = FakeWasmBinaryReader(
            fakeByteReader = {
                consumedEmptyByte = true
                Ok(0x00)
            },
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expected = Ok(MemoryInstruction.MemoryFill)

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
        assertEquals(true, consumedEmptyByte)
    }

    @Test
    fun `can decode prefixed TABLE_INIT instruction`() {

        val opcode = TABLE_INIT

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expectedElementIndex = Index.ElementIndex(117u)
        val elementIndexDecoder: ElementIndexDecoder = { _ ->
            Ok(expectedElementIndex)
        }

        val expectedTableIndex = Index.TableIndex(118u)
        val tableIndexDecoder: TableIndexDecoder = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableInit(expectedElementIndex, expectedTableIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed ELEM_DROP instruction`() {

        val opcode = ELEM_DROP

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expectedElementIndex = Index.ElementIndex(117u)
        val elementIndexDecoder: ElementIndexDecoder = { _ ->
            Ok(expectedElementIndex)
        }

        val expected = Ok(TableInstruction.ElemDrop(expectedElementIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_COPY instruction`() {

        val opcode = TABLE_COPY

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expectedSrcTableIndex = Index.TableIndex(117u)
        val expectedDestTableIndex = Index.TableIndex(118u)
        val tableIndexIter = sequenceOf(expectedDestTableIndex, expectedSrcTableIndex).iterator()
        val tableIndexDecoder: TableIndexDecoder = {
            Ok(tableIndexIter.next())
        }

        val expected = Ok(TableInstruction.TableCopy(expectedSrcTableIndex, expectedDestTableIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_GROW instruction`() {

        val opcode = TABLE_GROW

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expectedTableIndex = Index.TableIndex(117u)
        val tableIndexDecoder: TableIndexDecoder = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableGrow(expectedTableIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_SIZE instruction`() {

        val opcode = TABLE_SIZE

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expectedTableIndex = Index.TableIndex(117u)
        val tableIndexDecoder: TableIndexDecoder = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableSize(expectedTableIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_FILL instruction`() {

        val opcode = TABLE_FILL

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val expectedTableIndex = Index.TableIndex(117u)
        val tableIndexDecoder: TableIndexDecoder = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableFill(expectedTableIndex))

        val actual = BinaryPrefixInstructionDecoder(
            reader = reader,
            prefix = PREFIX_MISC,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown prefix instruction error when the opcode doesn't match`() {

        val prefix = PREFIX_MISC
        val opcode = 117u

        val expected = Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode))

        val reader = FakeUIntReader {
            Ok(opcode)
        }
        val actual = BinaryPrefixInstructionDecoder(reader, prefix)

        assertEquals(expected, actual)
    }

    private companion object {
        private val neverDataIndexDecoder: DataIndexDecoder = { _ ->
            fail("data index decoder should not run in this scenario")
        }
        private val neverElementIndexDecoder: ElementIndexDecoder = { _ ->
            fail("element index decoder should not run in this scenario")
        }
        private val neverTableIndexDecoder: TableIndexDecoder = { _ ->
            fail("table index decoder should not run in this scenario")
        }
    }
}
