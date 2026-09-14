package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.NumericInstructionDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class JvmKotlinProgramCompilerTest {
    @Test
    fun `region keeps a loop in one invocation and counts every internal block`() {
        val directory = Files.createTempDirectory("chasm-kotlin-region").toFile()
        val instructions = listOf<LinkedInstruction>(
            NumericInstruction.I32ConstS(3, 0),
            NumericInstruction.I32ConstS(0, 1),
            NumericInstruction.I32AddSs(1, 0, 1),
            NumericInstruction.I32SubSi(0, 1, 0),
            AdminInstruction.JumpIfS(0, 102),
        )
        try {
            JvmKotlinProgramCompiler(directory, KotlinCompilationMode.PREPARE, countExecutions = true).use { compiler ->
                val program = Program(compiler = compiler)
                repeat(100) { program.append(DispatchableInstruction { _, _, nextIp -> nextIp }) }
                instructions.forEach { instruction ->
                    program.append(
                        when (instruction) {
                            is NumericInstruction -> NumericInstructionDispatcher(instruction)
                            is AdminInstruction.JumpIfS -> JumpDispatcher(instruction)
                            else -> error("Unexpected fixture instruction")
                        },
                    )
                }
                assertNull(compiler.compile(program, 100, instructions, intArrayOf(100)))
                val stack = ValueStack(2)
                stack.activateFrame(0, 2)
                val context = ExecutionContext(stack, Store(program = program), ModuleInstance(RuntimeTypeMap.Empty), RuntimeConfig())
                assertEquals(105, program.instructions[100](stack, context, 101))
                assertEquals(6, stack.getFrameSlot(1))
                assertEquals(0, stack.getFrameSlot(0))
                assertEquals(4, compiler.generatedBlockExecutions)
                assertEquals(11, compiler.generatedInstructionExecutions)
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `structured loop preserves early exit copies and internal block counts`() {
        val directory = Files.createTempDirectory("chasm-structured-loop").toFile()
        val instructions = listOf<LinkedInstruction>(
            NumericInstruction.I32ConstS(3, 0),
            NumericInstruction.I32AddSi(1, 2, 1),
            AdminInstruction.JumpIfCopyS(2, 1, 3, 105),
            NumericInstruction.I32SubSi(0, 1, 0),
            AdminInstruction.JumpIfS(0, 101),
            NumericInstruction.I32AddSi(1, 10, 4),
        )
        val source = KotlinSourceGenerator().generate(100, instructions, intArrayOf(100))
        assertEquals(1, source.structuredLoopCount)
        assertEquals(2, source.structuredBlockCount)
        assertEquals(1, source.linearBodyCount)
        assertEquals(listOf(0), source.groups.single().entryOffsets)
        assertTrue("loop1@ while" in source.groups.single().source)
        assertTrue("when (pc)" !in source.groups.single().source)
        try {
            JvmKotlinProgramCompiler(directory, KotlinCompilationMode.PREPARE, countExecutions = true).use { compiler ->
                val program = Program(compiler = compiler)
                repeat(100) { program.append(DispatchableInstruction { _, _, nextIp -> nextIp }) }
                instructions.forEach { instruction ->
                    program.append(
                        when (instruction) {
                            is NumericInstruction -> NumericInstructionDispatcher(instruction)
                            is AdminInstruction.JumpIfCopyS -> JumpDispatcher(instruction)
                            is AdminInstruction.JumpIfS -> JumpDispatcher(instruction)
                            else -> error("Unexpected fixture instruction")
                        },
                    )
                }
                val interior = program.instructions[103]
                assertNull(compiler.compile(program, 100, instructions, intArrayOf(100)))
                assertSame(interior, program.instructions[103])
                for (early in listOf(0L, 1L)) {
                    val stack = ValueStack(5)
                    stack.activateFrame(0, 5)
                    stack.setFrameSlot(2, early)
                    stack.setFrameSlot(3, -1L)
                    val context = ExecutionContext(stack, Store(program = program), ModuleInstance(RuntimeTypeMap.Empty), RuntimeConfig())
                    val beforeBlocks = compiler.generatedBlockExecutions
                    val beforeInstructions = compiler.generatedInstructionExecutions
                    assertEquals(106, program.instructions[100](stack, context, 101))
                    assertEquals(if (early == 0L) 16L else 12L, stack.getFrameSlot(4))
                    assertEquals(if (early == 0L) -1L else 2L, stack.getFrameSlot(3))
                    assertEquals(if (early == 0L) 8L else 3L, compiler.generatedBlockExecutions - beforeBlocks)
                    assertEquals(if (early == 0L) 14L else 4L, compiler.generatedInstructionExecutions - beforeInstructions)
                }
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `cache miss leaves original program intact`() {
        val directory = Files.createTempDirectory("chasm-cache-miss").toFile()
        try {
            JvmKotlinProgramCompiler(directory).use { compiler ->
                val instruction = NumericInstruction.I32ConstS(5, 0)
                val original = NumericInstructionDispatcher(instruction)
                val program = Program(compiler = compiler)
                program.append(original)
                assertIs<InstantiationError.ProgramCompilationFailed>(compiler.compile(program, 0, listOf(instruction), intArrayOf(0)))
                assertSame(original, program.instructions[0])
                assertEquals(1, program.size)
                assertTrue(directory.listFiles().orEmpty().isEmpty())
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `compiled inline bodies bind fresh operands on cache reuse`() {
        val directory = Files.createTempDirectory("chasm-kotlin-test").toFile()
        val reports = mutableListOf<KotlinCompilationReport>()

        fun execute(compiler: JvmKotlinProgramCompiler, value: Int): Long {
            val instructions = listOf(
                NumericInstruction.I32ConstS(value, 0),
                NumericInstruction.I32ConstS(7, 1),
                NumericInstruction.I32AddSs(0, 1, 2),
            )
            val program = Program(compiler = compiler)
            instructions.forEach { program.append(NumericInstructionDispatcher(it)) }
            assertNull(compiler.compile(program, 0, instructions, intArrayOf(0)))
            val stack = ValueStack(4)
            stack.activateFrame(0, 4)
            val store = Store(program = program)
            val context = ExecutionContext(stack, store, ModuleInstance(RuntimeTypeMap.Empty), RuntimeConfig())
            assertEquals(3, program.instructions[0](stack, context, 1))
            return stack.getFrameSlot(2)
        }
        try {
            JvmKotlinProgramCompiler(directory, KotlinCompilationMode.PREPARE, countExecutions = true, onCompilation = reports::add).use { compiler ->
                assertEquals(12, execute(compiler, 5))
                assertEquals(3, compiler.generatedInstructionExecutions)
            }
            JvmKotlinProgramCompiler(directory, KotlinCompilationMode.CACHED, onCompilation = reports::add).use { compiler ->
                assertEquals(107, execute(compiler, 100))
            }
            assertTrue(reports.last().cacheHit)
            assertEquals(3, reports.last().generatedInstructionCount)
            assertEquals(1, reports.last().blockCount)
        } finally {
            directory.deleteRecursively()
        }
    }
}
