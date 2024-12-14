package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.instruction.memoryCopyInstruction
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PrefixFCInstructionDecoderTest {

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

        var opcode = 0u
        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        opcodeMap.forEach { (_opcode, _instruction) ->

            opcode = _opcode

            val actual = PrefixFCInstructionDecoder(
                context = context,
                dataIndexDecoder = neverDataIndexDecoder,
                elementIndexDecoder = neverElementIndexDecoder,
                memoryIndexDecoder = neverMemoryIndexDecoder,
                tableIndexDecoder = neverTableIndexDecoder,
            )

            assertEquals(Ok(_instruction), actual)
        }
    }

    @Test
    fun `can decode prefixed MEMORY_INIT instruction`() {

        val opcode = MEMORY_INIT

        val expectedDataIndex = Index.DataIndex(117u)
        val dataIndexDecoder: Decoder<Index.DataIndex> = { _ ->
            Ok(expectedDataIndex)
        }
        val expectedMemoryIndex = memoryIndex()
        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = {
            Ok(expectedMemoryIndex)
        }
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )

        val context = decoderContext(reader)

        val expected = Ok(MemoryInstruction.MemoryInit(expectedMemoryIndex, expectedDataIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = memoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed DATA_DROP instruction`() {

        val opcode = DATA_DROP

        val expectedDataIndex = Index.DataIndex(117u)

        val dataIndexDecoder: Decoder<Index.DataIndex> = { _ ->
            Ok(expectedDataIndex)
        }

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expected = Ok(MemoryInstruction.DataDrop(expectedDataIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed MEMORY_COPY instruction`() {

        val opcode = MEMORY_COPY

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )
        val srcMemoryIndex = memoryIndex(117u)
        val dstMemoryIndex = memoryIndex(118u)
        val indices = sequenceOf(dstMemoryIndex, srcMemoryIndex).iterator()
        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = {
            Ok(indices.next())
        }
        val context = decoderContext(reader)

        val expected = Ok(memoryCopyInstruction(srcMemoryIndex, dstMemoryIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = memoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed MEMORY_FILL instruction`() {

        val opcode = MEMORY_FILL

        val expectedMemoryIndex = memoryIndex()
        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = {
            Ok(expectedMemoryIndex)
        }

        val reader = FakeWasmBinaryReader(
            fakeUIntReader = {
                Ok(opcode)
            },
        )
        val context = decoderContext(reader)

        val expected = Ok(MemoryInstruction.MemoryFill(expectedMemoryIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            memoryIndexDecoder = memoryIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_INIT instruction`() {

        val opcode = TABLE_INIT

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expectedElementIndex = Index.ElementIndex(117u)
        val elementIndexDecoder: Decoder<Index.ElementIndex> = { _ ->
            Ok(expectedElementIndex)
        }

        val expectedTableIndex = Index.TableIndex(118u)
        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableInit(expectedElementIndex, expectedTableIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed ELEM_DROP instruction`() {

        val opcode = ELEM_DROP

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expectedElementIndex = Index.ElementIndex(117u)
        val elementIndexDecoder: Decoder<Index.ElementIndex> = { _ ->
            Ok(expectedElementIndex)
        }

        val expected = Ok(TableInstruction.ElemDrop(expectedElementIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_COPY instruction`() {

        val opcode = TABLE_COPY

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expectedSrcTableIndex = Index.TableIndex(117u)
        val expectedDestTableIndex = Index.TableIndex(118u)
        val tableIndexIter = sequenceOf(expectedDestTableIndex, expectedSrcTableIndex).iterator()
        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(tableIndexIter.next())
        }

        val expected = Ok(TableInstruction.TableCopy(expectedSrcTableIndex, expectedDestTableIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_GROW instruction`() {

        val opcode = TABLE_GROW

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expectedTableIndex = Index.TableIndex(117u)
        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableGrow(expectedTableIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_SIZE instruction`() {

        val opcode = TABLE_SIZE

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expectedTableIndex = Index.TableIndex(117u)
        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableSize(expectedTableIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode prefixed TABLE_FILL instruction`() {

        val opcode = TABLE_FILL

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)

        val expectedTableIndex = Index.TableIndex(117u)
        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(expectedTableIndex)
        }

        val expected = Ok(TableInstruction.TableFill(expectedTableIndex))

        val actual = PrefixFCInstructionDecoder(
            context = context,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown prefix instruction error when the opcode doesn't match`() {

        val prefix = PREFIX_FC
        val opcode = 117u

        val expected = Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode))

        val reader = FakeUIntReader { Ok(opcode) }
        val context = decoderContext(reader)
        val actual = PrefixFCInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    private companion object {
        private val neverDataIndexDecoder: Decoder<Index.DataIndex> = { _ ->
            fail("data index decoder should not run in this scenario")
        }
        private val neverElementIndexDecoder: Decoder<Index.ElementIndex> = { _ ->
            fail("element index decoder should not run in this scenario")
        }
        private val neverMemoryIndexDecoder: Decoder<Index.MemoryIndex> = { _ ->
            fail("memory index decoder should not run in this scenario")
        }
        private val neverTableIndexDecoder: Decoder<Index.TableIndex> = { _ ->
            fail("table index decoder should not run in this scenario")
        }
    }
}
