package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.TABLE_GET
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.TABLE_SET
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTableInstructionDecoderTest {

    @Test
    fun `can decode TABLE_GET instruction`() {

        val opcode = TABLE_GET

        val expectedIndex = Index.TableIndex(117u)
        val expected = Ok(TableInstruction.TableGet(expectedIndex))

        val tableIndexDecoder: TableIndexDecoder = {
            Ok(expectedIndex)
        }

        val actual = BinaryTableInstructionDecoder(
            FakeWasmBinaryReader(),
            opcode,
            tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode TABLE_SET instruction`() {

        val opcode = TABLE_SET

        val expectedIndex = Index.TableIndex(117u)
        val expected = Ok(TableInstruction.TableSet(expectedIndex))

        val tableIndexDecoder: TableIndexDecoder = {
            Ok(expectedIndex)
        }

        val actual = BinaryTableInstructionDecoder(
            FakeWasmBinaryReader(),
            opcode,
            tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown table instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidTableInstruction(opcode))

        val actual = BinaryTableInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
