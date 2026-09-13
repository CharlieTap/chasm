package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.NumericInstructionDispatcher
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
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
