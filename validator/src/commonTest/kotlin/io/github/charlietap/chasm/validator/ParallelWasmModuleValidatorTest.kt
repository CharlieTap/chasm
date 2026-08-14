package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParallelWasmModuleValidatorTest {

    @Test
    fun `automatic parallel validation produces the serial result`() = runTest {
        val module = validModule(functionCount = 8, instructionCount = 10_000)
        val executor = TestParallelTaskExecutor()

        val serial = WasmModuleValidator(ModuleConfig(), module)
        val parallel = ParallelWasmModuleValidator(
            config = ModuleConfig(),
            module = module,
            taskExecutor = executor,
            mode = ValidationMode.AUTO,
            availableProcessors = 4,
        )

        assertEquals(serial, parallel)
        assertEquals(3, executor.taskCount)
    }

    @Test
    fun `function errors are independent of task completion order`() = runTest {
        val module = validModule(functionCount = 4, instructionCount = 128).let { valid ->
            valid.copy(
                functions = valid.functions.mapIndexed { index, function ->
                    when (index) {
                        0 -> function.copy(
                            body = Expression(VariableInstruction.LocalGet(Index.LocalIndex(UInt.MAX_VALUE))),
                        )
                        1 -> function.copy(body = Expression(ParametricInstruction.Drop))
                        else -> function
                    }
                },
            )
        }
        val executor = TestParallelTaskExecutor(reverse = true)

        val serial = WasmModuleValidator(ModuleConfig(), module)
        val parallel = ParallelWasmModuleValidator(
            config = ModuleConfig(),
            module = module,
            taskExecutor = executor,
            mode = ValidationMode.PARALLEL,
            availableProcessors = 3,
        )

        assertEquals(serial, parallel)
    }

    @Test
    fun `serial strategies do not invoke the task executor`() = runTest {
        val module = validModule(functionCount = 1, instructionCount = 1)
        val executor = TestParallelTaskExecutor()

        val serial = WasmModuleValidator(ModuleConfig(), module)
        val parallel = ParallelWasmModuleValidator(
            config = ModuleConfig(),
            module = module,
            taskExecutor = executor,
            mode = ValidationMode.AUTO,
            availableProcessors = 8,
        )

        assertEquals(serial, parallel)
        assertEquals(0, executor.taskCount)
    }

    @Test
    fun `cancellation is observed between functions`() = runTest {
        val module = validModule(functionCount = 4, instructionCount = 128)
        val executor = CancellingTaskExecutor()

        assertFailsWith<TestCancellation> {
            ParallelWasmModuleValidator(
                config = ModuleConfig(),
                module = module,
                taskExecutor = executor,
                mode = ValidationMode.PARALLEL,
                availableProcessors = 3,
            )
        }
    }

    private fun validModule(
        functionCount: Int,
        instructionCount: Int,
    ): Module {
        val recursiveType = functionRecursiveType()
        return module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = List(functionCount) { functionIndex ->
                function(
                    idx = Index.FunctionIndex(functionIndex.toUInt()),
                    body = Expression(List(instructionCount) { ControlInstruction.Nop }),
                )
            },
        )
    }

    private class TestParallelTaskExecutor(
        private val reverse: Boolean = false,
    ) : ParallelTaskExecutor {

        var taskCount = 0
            private set

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {
            taskCount = tasks.size
            val scope = ParallelTaskScope {}
            val indices = if (reverse) tasks.indices.reversed() else tasks.indices
            val results = arrayOfNulls<Any?>(tasks.size)
            for (index in indices) results[index] = tasks[index](scope)
            @Suppress("UNCHECKED_CAST")
            return results.asList() as List<T>
        }
    }

    private class CancellingTaskExecutor : ParallelTaskExecutor {

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {
            val scope = ParallelTaskScope { throw TestCancellation() }
            return tasks.map { task -> task(scope) }
        }
    }

    private class TestCancellation : Exception()
}
