@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration

import com.github.michaelbull.result.Result
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.fixture.publicModule
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.internal.InterpreterModuleInstaller
import io.github.charlietap.chasm.embedding.internal.ModuleInstaller
import io.github.charlietap.chasm.embedding.internal.ParallelModuleCodeInstaller
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleInstantiator
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
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
import io.github.charlietap.chasm.fixture.ast.module.module as astModule
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class ParallelInstantiationTest {

    @Test
    fun `parallel compilation matches serial compilation`() = runTest {
        val module = module(Resource(FIXTURE).readBytes())
            .expect("expected fixture to validate")
            .withParallelCompilationWork()
        val serialExecutor = TestParallelTaskExecutor(concurrent = false)
        val parallelExecutor = TestParallelTaskExecutor(concurrent = true)
        val serialStore = TestStore(serialExecutor)
        val parallelStore = TestStore(parallelExecutor)

        val serialInstance = instance(
            serialStore.store,
            module,
            emptyList(),
        ).expect("expected fixture to instantiate serially")
        val parallelInstance = instance(
            parallelStore.store,
            module,
            emptyList(),
        ).expect("expected fixture to instantiate in parallel")

        assertEquals(1, parallelExecutor.executionCount)
        assertEquals(serialStore.runtime.program.size, parallelStore.runtime.program.size)
        assertEquals(serialStore.compiledFunctions(), parallelStore.compiledFunctions())

        invoke(serialInstance, "allocate")
            .expect("expected serial allocation function to execute")
        invoke(parallelInstance, "allocate")
            .expect("expected parallel allocation function to execute")
        val serialResult = invoke(serialInstance, "nested-array-length")
            .expect("expected serial function to execute")
        val parallelResult = invoke(parallelInstance, "nested-array-length")
            .expect("expected parallel function to execute")

        assertEquals(listOf(NumberValue.I32(2)), serialResult)
        assertEquals(serialResult, parallelResult)
    }

    @Test
    fun `parallel compilation is repeatable across independent stores`() = runTest {
        val module = module(Resource(FIXTURE).readBytes())
            .expect("expected fixture to validate")
            .withParallelCompilationWork()

        val compilations = coroutineScope {
            List(CONCURRENT_COMPILATIONS) {
                async(Dispatchers.Default) {
                    val taskExecutor = TestParallelTaskExecutor(concurrent = true)
                    val store = TestStore(taskExecutor)
                    val instance = instance(
                        store.store,
                        module,
                        emptyList(),
                    ).expect("expected fixture to instantiate in parallel")
                    assertEquals(1, taskExecutor.executionCount)
                    invoke(instance, "allocate")
                        .expect("expected parallel allocation function to execute")
                    val result = invoke(instance, "nested-array-length")
                        .expect("expected parallel function to execute")
                    CompilationResult(store.runtime.program.size, store.compiledFunctions(), result)
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
        val taskExecutor = TestParallelTaskExecutor(concurrent = true)
        val store = TestStore(taskExecutor)

        val result = instance(
            store.store,
            module,
            emptyList(),
        )

        assertEquals(1, taskExecutor.executionCount)
        val error = assertIs<ChasmResult.Error<ExecutionError.Unsupported>>(result)
        assertEquals(
            ExecutionError.Unsupported(InstantiationError.UnsupportedThreadsModule.toString()),
            error.error,
        )
        assertEquals(0, store.runtime.program.size)
        assertTrue(
            store.runtime.functions
                .filterIsInstance<FunctionInstance.WasmFunction>()
                .none { function -> function.callStrategy.isInstalled },
        )
    }

    private fun Module.withParallelCompilationWork(): Module {
        val decoded = _payload() as DecodedModule
        val module = decoded.module
        val nextFunctionIndex = module.functions.maxOf { function -> function.idx.idx } + 1u
        val typeIndex = module.functions.first().typeIndex
        val functions = List(PARALLEL_FUNCTION_COUNT) { index ->
            function(
                idx = Index.FunctionIndex(nextFunctionIndex + index.toUInt()),
                typeIndex = typeIndex,
                body = Expression(List(PARALLEL_INSTRUCTION_COUNT) { ControlInstruction.Nop }),
            )
        }
        return publicModule(
            config = decoded.config,
            module = module.copy(functions = module.functions + functions),
            validated = true,
        )
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
        return publicModule(
            config = ModuleConfig(),
            module = astModule(
                definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
                functions = functions,
            ),
            validated = true,
        )
    }

    private class TestStore(
        taskExecutor: ParallelTaskExecutor,
    ) {

        val runtime = RuntimeStore()
        val store: Store = interpreterEngine(StoreRuntime(runtime), TestModuleInstaller(taskExecutor)).store

        fun compiledFunctions(): List<CompiledFunction> = runtime.functions
            .filterIsInstance<FunctionInstance.WasmFunction>()
            .map { function ->
                CompiledFunction(
                    entryIp = function.callStrategy.entryIp,
                    frameSlots = function.callStrategy.frameSlots,
                    returnSlots = function.functionType.results.types.indices.toList(),
                )
            }
    }

    private class TestModuleInstaller(
        private val taskExecutor: ParallelTaskExecutor,
        private val installer: ModuleInstaller = InterpreterModuleInstaller(),
    ) : ModuleInstaller by installer {

        override suspend fun instantiate(
            module: Module,
            config: RuntimeConfig,
            runtime: RuntimeStore,
            imports: List<RuntimeImport>,
        ): Result<ModuleInstance, ModuleTrapError> {

            val decoded = module._payload() as DecodedModule

            return ParallelModuleInstantiator(
                config = config,
                store = runtime,
                module = decoded.declarations,
                imports = imports,
                taskExecutor = taskExecutor,
                codeInstaller = ParallelModuleCodeInstaller(decoded.module),
            )
        }
    }

    private data class CompilationResult(
        val programSize: Int,
        val compiledFunctions: List<CompiledFunction>,
        val result: List<ExecutionValue>,
    )

    private data class CompiledFunction(
        val entryIp: Int,
        val frameSlots: Int,
        val returnSlots: List<Int>,
    )

    private companion object {
        const val FIXTURE = "integration/gc.wasm"
        const val CONCURRENT_COMPILATIONS = 8
        const val PARALLEL_FUNCTION_COUNT = 32
        const val PARALLEL_INSTRUCTION_COUNT = 2_304
    }

    private class TestParallelTaskExecutor(
        private val concurrent: Boolean,
    ) : ParallelTaskExecutor {

        var executionCount = 0
            private set

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> = coroutineScope {
            executionCount++
            if (concurrent) {
                tasks.map { task ->
                    async(Dispatchers.Default) {
                        val context = coroutineContext
                        task(ParallelTaskScope(context::ensureActive))
                    }
                }.awaitAll()
            } else {
                tasks.map { task -> task(ParallelTaskScope {}) }
            }
        }
    }
}
