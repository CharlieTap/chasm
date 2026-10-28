package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrapError
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.fail

class ParallelModuleCompilerTest {

    private val module = module(
        definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
        functions = List(4) { index -> function(idx = Index.FunctionIndex(index.toUInt()), body = Expression(ControlInstruction.Nop)) },
    )

    @Test
    fun `compilation stops at the first function that fails and reports its error`() = runTest {
        val attempted = ArrayList<Int>()

        val result = compile(module) { context, function, workspace ->
            val index = function.idx.idx.toInt()
            attempted += index
            when (index) {
                1 -> Err(CompilationError.UnsupportedSimd)
                2 -> Err(CompilationError.UnsupportedThreads)
                else -> FunctionCompiler(context, function, workspace)
            }
        }

        assertEquals(CompilationError.UnsupportedSimd, result.unwrapError())
        assertEquals(listOf(0, 1), attempted)
    }

    @Test
    fun `an exception thrown while compiling a function reaches the caller`() = runTest {
        val failure = IllegalStateException("compiler bug")

        val thrown = assertFailsWith<IllegalStateException> {
            compile(module) { _, _, _ -> throw failure }
        }

        assertSame(failure, thrown)
    }

    private suspend fun compile(
        module: Module,
        functionCompiler: FunctionCompiler,
    ) = ParallelModuleCompiler(
        module = module,
        types = module.typeResolver(),
        taskExecutor = SequentialTaskExecutor(),
        mode = CompilationMode.PARALLEL,
        availableProcessors = 2,
        policy = ParallelismPolicy(minimumWork = 0, workPerWorker = 1),
        functionCompiler = functionCompiler,
        moduleCompiler = { _, _ -> fail("the parallel path compiles the module") },
    )

    private class SequentialTaskExecutor : ParallelTaskExecutor {

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {
            val scope = ParallelTaskScope {}
            return tasks.map { task -> task(scope) }
        }
    }
}
