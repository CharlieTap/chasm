package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_GET
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_SET
import io.github.charlietap.chasm.decoder.decoder.instruction.table.TableInstructionDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class TableInstructionDecoderTest {

    @Test
    fun `can decode TABLE_GET instruction`() {

        val opcode = TABLE_GET
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.TableIndex(117u)
        val expected = Ok(TableInstruction.TableGet(expectedIndex))

        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(expectedIndex)
        }

        val actual = TableInstructionDecoder(
            context,
            tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode TABLE_SET instruction`() {

        val opcode = TABLE_SET
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expectedIndex = Index.TableIndex(117u)
        val expected = Ok(TableInstruction.TableSet(expectedIndex))

        val tableIndexDecoder: Decoder<Index.TableIndex> = {
            Ok(expectedIndex)
        }

        val actual = TableInstructionDecoder(
            context,
            tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown table instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val reader = FakeUByteReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidTableInstruction(opcode))

        val actual = TableInstructionDecoder(context)

        assertEquals(expected, actual)
    }
}
