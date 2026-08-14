package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.instruction.memoryFillInstruction
import io.github.charlietap.chasm.fixture.ast.instruction.memorySizeInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionDecoderTest {

    @Test
    fun `can decode a numeric instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val numericInstructionDecoder: CodeBodyDecoder<NumericInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(NumericInstruction.I32Eq)
        }

        NUMERIC_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    numericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    neverTableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(NumericInstruction.I32Eq), actual)
            }
        }
    }

    @Test
    fun `can decode a reference instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val referenceInstructionDecoder: CodeBodyDecoder<ReferenceInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(ReferenceInstruction.RefIsNull)
        }

        REFERENCE_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    referenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    neverTableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(ReferenceInstruction.RefIsNull), actual)
            }
        }
    }

    @Test
    fun `can decode a parametric instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val parametricInstructionDecoder: CodeBodyDecoder<ParametricInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(ParametricInstruction.Drop)
        }

        PARAMETRIC_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    parametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    neverTableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(ParametricInstruction.Drop), actual)
            }
        }
    }

    @Test
    fun `can decode a variable instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val expected = VariableInstruction.LocalGet(Index.LocalIndex(117u))
        val variableInstructionDecoder: CodeBodyDecoder<VariableInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(expected)
        }

        VARIABLE_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    variableInstructionDecoder,
                    neverTableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a table instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val expected = TableInstruction.TableGet(Index.TableIndex(117u))
        val tableInstructionDecoder: CodeBodyDecoder<TableInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(expected)
        }

        TABLE_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    tableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a memory instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val expected = memorySizeInstruction()
        val memoryInstructionDecoder: CodeBodyDecoder<MemoryInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(expected)
        }

        MEMORY_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    neverTableInstructionDecoder,
                    memoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a control instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val expected = ControlInstruction.Nop
        val controlInstructionDecoder: CodeBodyDecoder<ControlInstruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(expected)
        }

        CONTROL_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    neverTableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    controlInstructionDecoder,
                    neverPrefixInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a prefixed instruction`() {

        var opcode: UByte = 0u
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val expected = memoryFillInstruction()
        val prefixInstructionDecoder: CodeBodyDecoder<Instruction> = { ctx ->
            assertEquals(context, ctx)
            Ok(expected)
        }

        PREFIXED_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = InstructionDecoder(
                    context,
                    neverNumericInstructionDecoder,
                    neverReferenceInstructionDecoder,
                    neverParametricInstructionDecoder,
                    neverVariableInstructionDecoder,
                    neverTableInstructionDecoder,
                    neverMemoryInstructionDecoder,
                    neverControlInstructionDecoder,
                    prefixInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `returns an unknown instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()
        val context = decoderContext(
            reader = FakeWasmBinaryReader(
                fakePeekReader = {
                    FakeUByteReader {
                        Ok(opcode)
                    }
                },
            ),
        )

        val expected = Err(InstructionDecodeError.UnknownInstruction(opcode))

        val actual = InstructionDecoder(
            context,
            neverNumericInstructionDecoder,
            neverReferenceInstructionDecoder,
            neverParametricInstructionDecoder,
            neverVariableInstructionDecoder,
            neverTableInstructionDecoder,
            neverMemoryInstructionDecoder,
            neverControlInstructionDecoder,
            neverPrefixInstructionDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `does not decode structural markers as control instructions`() {
        listOf(ELSE, END).forEach { opcode ->
            val context = decoderContext(
                reader = FakeWasmBinaryReader(
                    fakePeekReader = {
                        FakeUByteReader {
                            Ok(opcode)
                        }
                    },
                ),
            )

            val actual = InstructionDecoder(
                context,
                neverNumericInstructionDecoder,
                neverReferenceInstructionDecoder,
                neverParametricInstructionDecoder,
                neverVariableInstructionDecoder,
                neverTableInstructionDecoder,
                neverMemoryInstructionDecoder,
                neverControlInstructionDecoder,
                neverPrefixInstructionDecoder,
            )

            assertEquals(Err(InstructionDecodeError.UnknownInstruction(opcode)), actual)
        }
    }

    companion object {
        private val neverNumericInstructionDecoder: CodeBodyDecoder<NumericInstruction> = {
            error("NumericInstructionDecoder should not be called in this scenario")
        }

        private val neverReferenceInstructionDecoder: CodeBodyDecoder<ReferenceInstruction> = {
            error("ReferenceInstructionDecoder should not be called in this scenario")
        }

        private val neverParametricInstructionDecoder: CodeBodyDecoder<ParametricInstruction> = {
            error("ParametricInstructionDecoder should not be called in this scenario")
        }

        private val neverVariableInstructionDecoder: CodeBodyDecoder<VariableInstruction> = {
            error("VariableInstructionDecoder should not be called in this scenario")
        }

        private val neverTableInstructionDecoder: CodeBodyDecoder<TableInstruction> = {
            error("TableInstructionDecoder should not be called in this scenario")
        }

        private val neverMemoryInstructionDecoder: CodeBodyDecoder<MemoryInstruction> = {
            error("MemoryInstructionDecoder should not be called in this scenario")
        }

        private val neverControlInstructionDecoder: CodeBodyDecoder<ControlInstruction> = {
            error("ControlInstructionDecoder should not be called in this scenario")
        }

        private val neverPrefixInstructionDecoder: CodeBodyDecoder<Instruction> = {
            error("PrefixInstructionDecoder should not be called in this scenario")
        }
    }
}
