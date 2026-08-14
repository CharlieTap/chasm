package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import io.github.charlietap.chasm.embedding.instance as synchronousInstance
import io.github.charlietap.chasm.embedding.internal._instance as parallelInstance
import io.github.charlietap.chasm.fixture.ast.module.module as astModule

@OptIn(InternalChasmApi::class)
class ParallelInstantiationTest {

    @Test
    fun `parallel compilation matches serial compilation`() = runTest {
        val module = module(Resource(FIXTURE).readBytes())
            .expect("expected fixture to decode")
            .let(::validate)
            .expect("expected fixture to validate")
            .withParallelCompilationWork()
        val serialStore = Store()
        val parallelStore = Store()
        val taskExecutor = TestParallelTaskExecutor()

        val serialInstance = synchronousInstance(
            serialStore,
            module,
            emptyList(),
            RuntimeConfig(),
        ).expect("expected fixture to instantiate serially")
        val parallelInstance = parallelInstance(
            parallelStore,
            module,
            emptyList(),
            RuntimeConfig(),
            taskExecutor,
        ).expect("expected fixture to instantiate in parallel")

        assertEquals(1, taskExecutor.executionCount)
        assertEquals(serialStore.store.program.size, parallelStore.store.program.size)
        assertEquals(serialStore.functionPlans(), parallelStore.functionPlans())

        invoke(serialStore, serialInstance, "allocate")
            .expect("expected serial allocation function to execute")
        invoke(parallelStore, parallelInstance, "allocate")
            .expect("expected parallel allocation function to execute")
        val serialResult = invoke(serialStore, serialInstance, "nested-array-length")
            .expect("expected serial function to execute")
        val parallelResult = invoke(parallelStore, parallelInstance, "nested-array-length")
            .expect("expected parallel function to execute")

        assertEquals(listOf(NumberValue.I32(2)), serialResult)
        assertEquals(serialResult, parallelResult)
    }

    @Test
    fun `parallel compilation is repeatable across independent stores`() = runTest {
        val module = module(Resource(FIXTURE).readBytes())
            .expect("expected fixture to decode")
            .let(::validate)
            .expect("expected fixture to validate")
            .withParallelCompilationWork()

        val compilations = coroutineScope {
            List(CONCURRENT_COMPILATIONS) {
                async(Dispatchers.Default) {
                    val store = Store()
                    val taskExecutor = TestParallelTaskExecutor()
                    val instance = parallelInstance(
                        store,
                        module,
                        emptyList(),
                        RuntimeConfig(),
                        taskExecutor,
                    ).expect("expected fixture to instantiate in parallel")
                    assertEquals(1, taskExecutor.executionCount)
                    invoke(store, instance, "allocate")
                        .expect("expected parallel allocation function to execute")
                    val result = invoke(store, instance, "nested-array-length")
                        .expect("expected parallel function to execute")
                    CompilationResult(store.store.program.size, store.functionPlans(), result)
                }
            }.awaitAll()
        }

        val expected = compilations.first()
        compilations.forEach { compilation ->
            assertEquals(expected, compilation)
        }
    }

    @Test
    fun `parallel compilation reports the first error without linking functions`() = runTest {
        val module = unsupportedInstructionModule()
        val store = Store()
        val taskExecutor = TestParallelTaskExecutor()

        val result = parallelInstance(
            store,
            module,
            emptyList(),
            RuntimeConfig(),
            taskExecutor,
        )

        assertEquals(1, taskExecutor.executionCount)
        val error = assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result)
        assertEquals(
            ChasmError.ExecutionError(InstantiationError.UnsupportedThreadsModule.toString()),
            error.error,
        )
        assertEquals(0, store.store.program.size)
        assertTrue(
            store.store.functions
                .filterIsInstance<FunctionInstance.WasmFunction>()
                .none { it.callPlan.isInstalled },
        )
    }

    private fun Store.functionPlans(): List<FunctionPlan> = store.functions
        .filterIsInstance<FunctionInstance.WasmFunction>()
        .map { function ->
            FunctionPlan(
                entryIp = function.callPlan.entryIp,
                frameSlots = function.callPlan.frameSlots,
                returnSlots = function.function.returnSlots.toList(),
            )
        }

    private fun Module.withParallelCompilationWork(): Module {
        val nextFunctionIndex = module.functions.maxOf { function -> function.idx.idx } + 1u
        val typeIndex = module.functions.first().typeIndex
        val functions = List(PARALLEL_FUNCTION_COUNT) { index ->
            function(
                idx = Index.FunctionIndex(nextFunctionIndex + index.toUInt()),
                typeIndex = typeIndex,
                body = Expression(List(PARALLEL_INSTRUCTION_COUNT) { ControlInstruction.Nop }),
            )
        }
        return Module(config, module.copy(functions = module.functions + functions))
    }

    private fun unsupportedInstructionModule(): Module {
        val functions = buildList {
            add(
                function(
                    idx = Index.FunctionIndex(0u),
                    body = Expression(AtomicMemoryInstruction.Fence),
                ),
            )
            add(
                function(
                    idx = Index.FunctionIndex(1u),
                    body = Expression(VectorInstruction.V128Const(ByteArray(16))),
                ),
            )
            repeat(PARALLEL_FUNCTION_COUNT - 2) { index ->
                add(
                    function(
                        idx = Index.FunctionIndex((index + 2).toUInt()),
                        body = Expression(List(PARALLEL_INSTRUCTION_COUNT) { ControlInstruction.Nop }),
                    ),
                )
            }
        }
        return Module(
            config = ModuleConfig(),
            module = astModule(
                definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
                functions = functions,
            ),
        )
    }

    private data class CompilationResult(
        val programSize: Int,
        val functionPlans: List<FunctionPlan>,
        val result: List<ExecutionValue>,
    )

    private data class FunctionPlan(
        val entryIp: Int,
        val frameSlots: Int,
        val returnSlots: List<Int>,
    )

    private companion object {
        const val FIXTURE = "integration/gc.wasm"
        const val CONCURRENT_COMPILATIONS = 8
        const val PARALLEL_FUNCTION_COUNT = 32
        const val PARALLEL_INSTRUCTION_COUNT = 256
    }

    private class TestParallelTaskExecutor : ParallelTaskExecutor {

        var executionCount = 0
            private set

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> = coroutineScope {
            executionCount++
            tasks.map { task ->
                async(Dispatchers.Default) {
                    val context = coroutineContext
                    task(ParallelTaskScope(context::ensureActive))
                }
            }.awaitAll()
        }
    }
}
