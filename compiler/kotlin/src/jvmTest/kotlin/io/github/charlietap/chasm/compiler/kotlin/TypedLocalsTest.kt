package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.NumericInstructionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.ReferenceInstructionDispatcher
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypedLocalsTest {
    @Test
    fun `skipped numeric writes preserve complete incoming words in typed slots`() {
        val instructions = listOf(
            AdminInstruction.JumpIfS(1, 3),
            NumericInstruction.I32ConstS(7, 0),
            AdminInstruction.Jump(4),
            AdminInstruction.CopySlot(0, 2),
            AdminInstruction.CopySlot(0, 3),
        )
        assertEquals(1, KotlinSourceGenerator().generate(0, instructions, intArrayOf(0)).nativeI32SlotCount)
        val directory = Files.createTempDirectory("chasm-typed-shadow").toFile()
        try {
            for (mode in listOf(null, KotlinCompilationMode.PREPARE, KotlinCompilationMode.CACHED)) {
                for (bits in listOf(1.0.toRawBits(), 0x7ff8000000012345L, Long.MIN_VALUE + 17)) {
                    for (skip in listOf(0L, 1L)) {
                        val result = execute(instructions, mode, directory, longArrayOf(bits, skip, -999, 0))
                        val expected = if (skip == 0L) 7L else bits
                        assertEquals(expected, result[0], "$mode entry word")
                        assertEquals(if (skip == 0L) -999L else bits, result[2], "$mode conditional copy")
                        assertEquals(expected, result[3], "$mode exit copy")
                    }
                }
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `native floats preserve signed zero and NaN words while mixed slots stay raw`() {
        val f32Nan = 0x7f812345
        val f64Nan = 0x7ff0000000012345L
        val instructions = listOf(
            NumericInstruction.F32ConstS(Int.MIN_VALUE, 0),
            NumericInstruction.F64ConstS(Long.MIN_VALUE, 1),
            NumericInstruction.F32AddSs(0, 0, 2),
            NumericInstruction.F64AddSs(1, 1, 3),
            AdminInstruction.CopySlot(0, 4),
            AdminInstruction.CopySlot(1, 5),
            NumericInstruction.F32ConstS(f32Nan, 6),
            AdminInstruction.CopySlot(6, 7),
            NumericInstruction.F64ConstS(f64Nan, 8),
            AdminInstruction.CopySlot(8, 9),
            NumericInstruction.F64ConstS(1.25.toRawBits(), 10),
            NumericInstruction.F64AddSs(10, 10, 11),
            NumericInstruction.I32ConstS(7, 10),
        )
        val source = KotlinSourceGenerator().generate(0, instructions, intArrayOf(0))
        assertTrue(source.nativeF32SlotCount > 0)
        assertTrue(source.nativeF64SlotCount > 0)
        assertEquals(1, source.mixedSlotCount)
        val directory = Files.createTempDirectory("chasm-native-floats").toFile()
        try {
            for (mode in listOf(null, KotlinCompilationMode.PREPARE, KotlinCompilationMode.CACHED)) {
                val result = execute(instructions, mode, directory, LongArray(12))
                assertEquals(Int.MIN_VALUE.toLong(), result[2], "$mode f32 addition")
                assertEquals(Long.MIN_VALUE, result[3], "$mode f64 addition")
                assertEquals(Int.MIN_VALUE.toLong(), result[4], "$mode f32 zero copy")
                assertEquals(Long.MIN_VALUE, result[5], "$mode f64 zero copy")
                assertEquals(f32Nan.toLong(), result[7], "$mode f32 NaN copy")
                assertEquals(f64Nan, result[9], "$mode f64 NaN copy")
                assertEquals(7L, result[10], "$mode mixed slot")
                assertEquals(2.5.toRawBits(), result[11], "$mode mixed operand")
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `frame helpers can replace an integer local with a complete reference word`() {
        val reference = ReferenceValue.I31(0x12345678u).toLong()
        assertTrue(reference != reference.toInt().toLong())
        val instructions = listOf(
            NumericInstruction.I32ConstS(7, 0),
            ReferenceInstruction.RefAsNonNullS(1, 0),
            AdminInstruction.CopySlot(0, 2),
        )
        assertEquals(1, KotlinSourceGenerator().generate(0, instructions, intArrayOf(0)).nativeI32SlotCount)
        val directory = Files.createTempDirectory("chasm-native-reference").toFile()
        try {
            for (mode in listOf(null, KotlinCompilationMode.PREPARE, KotlinCompilationMode.CACHED)) {
                val result = execute(instructions, mode, directory, longArrayOf(0, reference, 0))
                assertEquals(reference, result[0], "$mode helper result")
                assertEquals(reference, result[2], "$mode reference copy")
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    private fun execute(instructions: List<LinkedInstruction>, mode: KotlinCompilationMode?, directory: File, initial: LongArray): LongArray {
        val compiler = mode?.let { JvmKotlinProgramCompiler(directory, it) }
        try {
            val program = Program(compiler = compiler)
            instructions.forEach { instruction ->
                program.append(
                    when (instruction) {
                        is NumericInstruction -> NumericInstructionDispatcher(instruction)
                        is ReferenceInstruction -> ReferenceInstructionDispatcher(instruction)
                        is AdminInstruction.Jump -> JumpDispatcher(instruction)
                        is AdminInstruction.JumpIfS -> JumpDispatcher(instruction)
                        is AdminInstruction.CopySlot -> CopySlotDispatcher(instruction.sourceSlot, instruction.destinationSlot)
                        else -> error("Unexpected fixture instruction")
                    },
                )
            }
            if (compiler != null) assertNull(compiler.compile(program, 0, instructions, intArrayOf(0)))
            val stack = ValueStack(initial.size)
            stack.activateFrame(0, initial.size)
            initial.forEachIndexed { index, value -> stack.setFrameSlot(index, value) }
            val context = ExecutionContext(stack, Store(program = program), ModuleInstance(RuntimeTypeMap.Empty), RuntimeConfig())
            var ip = 0
            while (ip < instructions.size) ip = program.instructions[ip](stack, context, ip + 1)
            assertEquals(instructions.size, ip)
            return LongArray(initial.size) { stack.getFrameSlot(it) }
        } finally {
            compiler?.close()
        }
    }
}
