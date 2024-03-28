package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control.ControlInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory.MemoryInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.numeric.NumericInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.parametric.ParametricInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix.PrefixInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference.ReferenceInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.table.TableInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.variable.VariableInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.vector.VectorInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryInstructionDecoderTest {

    @Test
    fun `can decode a numeric instruction`() {

        var opcode: UByte? = null

        val numericInstructionDecoder: NumericInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(NumericInstruction.I32Eq)
        }

        NUMERIC_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    numericInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(NumericInstruction.I32Eq), actual)
            }
        }
    }

    @Test
    fun `can decode a reference instruction`() {

        var opcode: UByte? = null

        val referenceInstructionDecoder: ReferenceInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(ReferenceInstruction.RefIsNull)
        }

        REFERENCE_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    referenceInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(ReferenceInstruction.RefIsNull), actual)
            }
        }
    }

    @Test
    fun `can decode a parametric instruction`() {

        var opcode: UByte? = null

        val parametricInstructionDecoder: ParametricInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(ParametricInstruction.Drop)
        }

        PARAMETRIC_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    parametricInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(ParametricInstruction.Drop), actual)
            }
        }
    }

    @Test
    fun `can decode a variable instruction`() {

        var opcode: UByte? = null

        val expected = VariableInstruction.LocalGet(Index.LocalIndex(117u))
        val variableInstructionDecoder: VariableInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(expected)
        }

        VARIABLE_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    variableInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a table instruction`() {

        var opcode: UByte? = null

        val expected = TableInstruction.TableGet(Index.TableIndex(117u))
        val tableInstructionDecoder: TableInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(expected)
        }

        TABLE_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    tableInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a memory instruction`() {

        var opcode: UByte? = null

        val expected = MemoryInstruction.MemorySize
        val memoryInstructionDecoder: MemoryInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(expected)
        }

        MEMORY_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    memoryInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a control instruction`() {

        var opcode: UByte? = null

        val expected = ControlInstruction.Nop
        val controlInstructionDecoder: ControlInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(expected)
        }

        CONTROL_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    controlInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a prefixed instruction`() {

        var opcode: UByte? = null

        val expected = MemoryInstruction.MemoryFill
        val prefixInstructionDecoder: PrefixInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(expected)
        }

        PREFIXED_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    prefixInstructionDecoder,
                    neverInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `can decode a vector instruction`() {

        var opcode: UByte? = null

        val expected = VectorInstruction.V128Or
        val vectorInstructionDecoder: VectorInstructionDecoder = { _, byte ->
            assertEquals(opcode, byte)
            Ok(expected)
        }

        VECTOR_OPCODES.forEach { range ->

            range.forEach { uint ->

                val byte = uint.toUByte()
                opcode = byte

                val actual = BinaryInstructionDecoder(
                    FakeWasmBinaryReader(),
                    byte,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    neverInstructionDecoder,
                    vectorInstructionDecoder,
                )

                assertEquals(Ok(expected), actual)
            }
        }
    }

    @Test
    fun `returns an unknown instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.UnknownInstruction(opcode))

        val actual = BinaryInstructionDecoder(
            FakeWasmBinaryReader(),
            opcode,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
            neverInstructionDecoder,
        )

        assertEquals(expected, actual)
    }

    companion object {
        private val neverInstructionDecoder: InstructionDecoder = { _, _ ->
            fail("This should never be called")
        }
    }
}
