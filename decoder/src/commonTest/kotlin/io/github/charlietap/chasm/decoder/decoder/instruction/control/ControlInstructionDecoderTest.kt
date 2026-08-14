package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.BLOCK
import io.github.charlietap.chasm.decoder.decoder.instruction.BR
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_IF
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_ON_NON_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_ON_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_TABLE
import io.github.charlietap.chasm.decoder.decoder.instruction.CALL
import io.github.charlietap.chasm.decoder.decoder.instruction.CALL_INDIRECT
import io.github.charlietap.chasm.decoder.decoder.instruction.CALL_REF
import io.github.charlietap.chasm.decoder.decoder.instruction.IF
import io.github.charlietap.chasm.decoder.decoder.instruction.LOOP
import io.github.charlietap.chasm.decoder.decoder.instruction.NOP
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN_CALL
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN_CALL_INDIRECT
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN_CALL_REF
import io.github.charlietap.chasm.decoder.decoder.instruction.THROW
import io.github.charlietap.chasm.decoder.decoder.instruction.THROW_REF
import io.github.charlietap.chasm.decoder.decoder.instruction.TRY_TABLE
import io.github.charlietap.chasm.decoder.decoder.instruction.UNREACHABLE
import io.github.charlietap.chasm.decoder.decoder.vector.CodeBodyVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.ast.instruction.blockType
import io.github.charlietap.chasm.fixture.ast.instruction.catchHandler
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ControlInstructionDecoderTest {

    @Test
    fun `can decode an UNREACHABLE instruction`() {

        val opcode = UNREACHABLE
        val context = decoderContext(FakeUByteReader { Ok(opcode) })
        val expected = Ok(ControlInstruction.Unreachable)

        val actual = ControlInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a NOP instruction`() {

        val opcode = NOP
        val context = decoderContext(FakeUByteReader { Ok(opcode) })
        val expected = Ok(ControlInstruction.Nop)

        val actual = ControlInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BLOCK instruction`() {

        val opcode = BLOCK
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedBlockType = BlockType.Empty
        val expected = Ok(ControlInstruction.Block(expectedBlockType))

        val blockTypeDecoder: CodeBodyDecoder<BlockType> = { _ ->
            Ok(expectedBlockType)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = blockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a LOOP instruction`() {

        val opcode = LOOP
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedBlockType = BlockType.Empty
        val expected = Ok(ControlInstruction.Loop(expectedBlockType))

        val blockTypeDecoder: CodeBodyDecoder<BlockType> = { _ ->
            Ok(expectedBlockType)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = blockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a IF instruction`() {

        val opcode = IF
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedBlockType = BlockType.Empty
        val expected = Ok(ControlInstruction.If(expectedBlockType))

        val blockTypeDecoder: CodeBodyDecoder<BlockType> = { _ ->
            Ok(expectedBlockType)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = blockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR instruction`() {

        val opcode = BR
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedLabelIndex = Index.LabelIndex(117u)
        val expected = Ok(ControlInstruction.Br(expectedLabelIndex))

        val labelIndexDecoder: CodeBodyDecoder<Index.LabelIndex> = { _ ->
            Ok(expectedLabelIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR_IF instruction`() {

        val opcode = BR_IF
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedLabelIndex = Index.LabelIndex(117u)
        val expected = Ok(ControlInstruction.BrIf(expectedLabelIndex))

        val labelIndexDecoder: CodeBodyDecoder<Index.LabelIndex> = { _ ->
            Ok(expectedLabelIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR_TABLE instruction`() {

        val opcode = BR_TABLE
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedLabelVec = emptyList<Index.LabelIndex>()
        val expectedDefaultLabelIndex = Index.LabelIndex(117u)
        val expected = Ok(ControlInstruction.BrTable(expectedLabelVec, expectedDefaultLabelIndex))

        val labelIndexDecoder: CodeBodyDecoder<Index.LabelIndex> = { _ ->
            Ok(expectedDefaultLabelIndex)
        }

        val vectorDecoder: CodeBodyVectorDecoder<Index.LabelIndex> = { _, _ ->
            Ok(Vector(expectedLabelVec))
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = vectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an RETURN instruction`() {

        val opcode = RETURN
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expected = Ok(ControlInstruction.Return)

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an CALL instruction`() {

        val opcode = CALL
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedFunctionIndex = Index.FunctionIndex(117u)
        val expected = Ok(ControlInstruction.Call(expectedFunctionIndex))

        val functionIndexDecoder: CodeBodyDecoder<Index.FunctionIndex> = { _ ->
            Ok(expectedFunctionIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = functionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an CALL_INDIRECT instruction`() {

        val opcode = CALL_INDIRECT
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedTypeIndex = Index.TypeIndex(117u)
        val expectedTableIndex = Index.TableIndex(118u)
        val expected = Ok(ControlInstruction.CallIndirect(expectedTypeIndex, expectedTableIndex))

        val typeIndexDecoder: CodeBodyDecoder<Index.TypeIndex> = { _ ->
            Ok(expectedTypeIndex)
        }

        val tableIndexDecoder: CodeBodyDecoder<Index.TableIndex> = { _ ->
            Ok(expectedTableIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an RETURN_CALL instruction`() {

        val opcode = RETURN_CALL
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedFunctionIndex = Index.FunctionIndex(117u)
        val expected = Ok(ControlInstruction.ReturnCall(expectedFunctionIndex))

        val functionIndexDecoder: CodeBodyDecoder<Index.FunctionIndex> = { _ ->
            Ok(expectedFunctionIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = functionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a RETURN_CALL_INDIRECT instruction`() {

        val opcode = RETURN_CALL_INDIRECT
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedTypeIndex = Index.TypeIndex(117u)
        val expectedTableIndex = Index.TableIndex(118u)
        val expected = Ok(ControlInstruction.ReturnCallIndirect(expectedTypeIndex, expectedTableIndex))

        val typeIndexDecoder: CodeBodyDecoder<Index.TypeIndex> = { _ ->
            Ok(expectedTypeIndex)
        }

        val tableIndexDecoder: CodeBodyDecoder<Index.TableIndex> = { _ ->
            Ok(expectedTableIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a CALL_REF instruction`() {

        val opcode = CALL_REF
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedTypeIndex = typeIndex()
        val expected = Ok(ControlInstruction.CallRef(expectedTypeIndex))

        val typeIndexDecoder: CodeBodyDecoder<Index.TypeIndex> = { _ ->
            Ok(expectedTypeIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a RETURN_CALL_REF instruction`() {

        val opcode = RETURN_CALL_REF
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedTypeIndex = typeIndex()
        val expected = Ok(ControlInstruction.ReturnCallRef(expectedTypeIndex))

        val typeIndexDecoder: CodeBodyDecoder<Index.TypeIndex> = { _ ->
            Ok(expectedTypeIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR_ON_NULL instruction`() {

        val opcode = BR_ON_NULL
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedLabelIndex = labelIndex()
        val expected = Ok(ControlInstruction.BrOnNull(expectedLabelIndex))

        val labelIndexDecoder: CodeBodyDecoder<Index.LabelIndex> = { _ ->
            Ok(expectedLabelIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR_ON_NON_NULL instruction`() {

        val opcode = BR_ON_NON_NULL
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )
        val expectedLabelIndex = labelIndex()
        val expected = Ok(ControlInstruction.BrOnNonNull(expectedLabelIndex))

        val labelIndexDecoder: CodeBodyDecoder<Index.LabelIndex> = { _ ->
            Ok(expectedLabelIndex)
        }

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a THROW instruction`() {

        val opcode = THROW
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )

        val tagIndex = tagIndex()
        val tagIndexDecoder: CodeBodyDecoder<Index.TagIndex> = { _ ->
            Ok(tagIndex)
        }

        val expected = Ok(ControlInstruction.Throw(tagIndex))

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = tagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a THROW_REF instruction`() {

        val opcode = THROW_REF
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )

        val expected = Ok(ControlInstruction.ThrowRef)

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = neverBlockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = neverHandlerVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a TRY_TABLE instruction`() {

        val opcode = TRY_TABLE
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )

        val blockType = blockType()
        val blockTypeDecoder: CodeBodyDecoder<BlockType> = { _ ->
            Ok(blockType)
        }

        val handlers = listOf(catchHandler())
        val handlersDecoder: CodeBodyVectorDecoder<ControlInstruction.CatchHandler> = { _, _ ->
            Ok(Vector(handlers))
        }

        val expected = Ok(
            ControlInstruction.TryTable(
                blockType = blockType,
                handlers = handlers,
            ),
        )

        val actual = ControlInstructionDecoder(
            context = context,
            blockTypeDecoder = blockTypeDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            handlerDecoder = neverHandlerDecoder,
            tagIndexDecoder = neverTagIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            labelVectorDecoder = neverLabelVectorDecoder,
            handlerVectorDecoder = handlersDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown control instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val context = decoderContext(
            reader = FakeUByteReader { Ok(opcode) },
        )

        val expected = Err(InstructionDecodeError.InvalidControlInstruction(opcode))

        val actual = ControlInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    private companion object {
        private val neverBlockTypeDecoder: CodeBodyDecoder<BlockType> = { _ ->
            fail("block type decoder should not run in this scenario")
        }
        private val neverHandlerDecoder: CodeBodyDecoder<ControlInstruction.CatchHandler> = { _ ->
            fail("catch handler decoder should not run in this scenario")
        }
        private val neverLabelIndexDecoder: CodeBodyDecoder<Index.LabelIndex> = { _ ->
            fail("label index decoder should not run in this scenario")
        }
        private val neverFunctionIndexDecoder: CodeBodyDecoder<Index.FunctionIndex> = { _ ->
            fail("function index decoder should not run in this scenario")
        }
        private val neverTypeIndexDecoder: CodeBodyDecoder<Index.TypeIndex> = { _ ->
            fail("type index decoder should not run in this scenario")
        }
        private val neverTableIndexDecoder: CodeBodyDecoder<Index.TableIndex> = { _ ->
            fail("table index decoder should not run in this scenario")
        }
        private val neverTagIndexDecoder: CodeBodyDecoder<Index.TagIndex> = { _ ->
            fail("tag index decoder should not run in this scenario")
        }
        private val neverLabelVectorDecoder: CodeBodyVectorDecoder<Index.LabelIndex> = { _, _ ->
            fail("label vector decoder should not run in this scenario")
        }
        private val neverHandlerVectorDecoder: CodeBodyVectorDecoder<ControlInstruction.CatchHandler> = { _, _ ->
            fail("catch handler vector decoder should not run in this scenario")
        }
    }
}
