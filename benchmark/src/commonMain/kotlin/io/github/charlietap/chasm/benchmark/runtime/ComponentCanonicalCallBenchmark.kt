package io.github.charlietap.chasm.benchmark.runtime

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.StackFunctionAllocator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.FusedComponentFunctionBody
import io.github.charlietap.chasm.executor.invoker.component.LoweredComponentFunctionBody
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalValueTupleLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLiftPlan
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLowerPlan
import io.github.charlietap.chasm.fixture.runtime.component.function.hostImportComponentFunction
import io.github.charlietap.chasm.fixture.runtime.component.function.liftedCoreComponentFunction
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.component.value.u32ComponentValue
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class ComponentCanonicalCallBenchmark {

    private lateinit var store: Store

    private lateinit var preparedVstack: ValueStack
    private lateinit var preparedCstack: ControlStack
    private lateinit var preparedExecution: ExecutionContext
    private lateinit var preparedBody: StackFunctionBody

    private lateinit var dynamicVstack: ValueStack
    private lateinit var dynamicCstack: ControlStack
    private lateinit var dynamicExecution: ExecutionContext
    private lateinit var dynamicBody: StackFunctionBody

    private lateinit var guestVstack: ValueStack
    private lateinit var guestCstack: ControlStack
    private lateinit var guestExecution: ExecutionContext
    private lateinit var guestBody: StackFunctionBody

    private lateinit var fusedGuestVstack: ValueStack
    private lateinit var fusedGuestCstack: ControlStack
    private lateinit var fusedGuestExecution: ExecutionContext
    private lateinit var fusedGuestBody: StackFunctionBody

    private lateinit var nonFusedCaller: ModuleInstance
    private var nonFusedCallerFunction = Address.Function(0)
    private lateinit var fusedCaller: ModuleInstance
    private var fusedCallerFunction = Address.Function(0)

    @Setup
    fun setup() {
        val valueTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 4u,
            alignment32 = 4u,
            flatCount = 1,
        )
        val plan = linearMemoryLowerPlan(
            parameterTuple = valueTuple,
            resultTuple = valueTuple,
        )
        val function = hostImportComponentFunction(
            parameterTuple = valueTuple,
            resultTuple = valueTuple,
        )
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
            callPlans = listOf(plan),
        )
        val preparedHostFunction = RuntimeComponentHostFunction.Prepared { _, arguments, _, results ->
            results[0] = arguments[0] + 1L
            Ok(1)
        }
        val preparedState = componentRuntimeState(
            hostFunctions = arrayOf(preparedHostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val preparedComponentStore = componentStore()
        val preparedRoot = preparedComponentStore.reserveRoot(preparedState)

        val dynamicHostFunction = RuntimeComponentHostFunction.Dynamic { _, arguments ->
            Ok(listOf(u32ComponentValue(arguments.single().u32() + 1u)))
        }
        val dynamicState = componentRuntimeState(
            hostFunctions = arrayOf(dynamicHostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val dynamicComponentStore = componentStore()
        val dynamicRoot = dynamicComponentStore.reserveRoot(dynamicState)

        store = store()

        preparedVstack = vstack().apply { reserveFrame(1) }
        preparedCstack = cstack()
        preparedExecution = executionContext(store = store, vstack = preparedVstack, cstack = preparedCstack)
        preparedBody = LoweredComponentFunctionBody(
            componentStore = preparedComponentStore,
            root = preparedRoot,
            runtimeInfo = runtimeInfo,
            plan = plan,
        )

        dynamicVstack = vstack().apply { reserveFrame(1) }
        dynamicCstack = cstack()
        dynamicExecution = executionContext(store = store, vstack = dynamicVstack, cstack = dynamicCstack)
        dynamicBody = LoweredComponentFunctionBody(
            componentStore = dynamicComponentStore,
            root = dynamicRoot,
            runtimeInfo = runtimeInfo,
            plan = plan,
        )

        val coreModule = WasmModuleDecoder(
            ModuleConfig(),
            Resource(CORE_TARGET_PATH).readBytes(),
        ).success()
        val coreInstance = ModuleInstantiator(RuntimeConfig(), store, coreModule, emptyList()).success()
        val coreFunction = coreInstance.functionAddresses.single()
        val liftPlan = linearMemoryLiftPlan(
            coreFunctionSlot = 0,
            parameterTuple = valueTuple,
            resultTuple = valueTuple,
        )
        val guestRuntimeInfo = componentRuntimeInfo(
            functions = listOf(liftedCoreComponentFunction(liftPlan = liftPlan)),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
            callPlans = listOf(plan),
        )
        val guestState = componentRuntimeState(
            coreFunctions = intArrayOf(coreFunction.address),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val guestComponentStore = componentStore()
        val guestRoot = guestComponentStore.reserveRoot(guestState)
        guestVstack = vstack().apply { reserveFrame(1) }
        guestCstack = cstack()
        guestExecution = executionContext(store = store, vstack = guestVstack, cstack = guestCstack)
        guestBody = LoweredComponentFunctionBody(
            componentStore = guestComponentStore,
            root = guestRoot,
            runtimeInfo = guestRuntimeInfo,
            plan = plan,
        )

        val fusedPlan = plan.copy(fusedTarget = liftPlan)
        val fusedGuestState = componentRuntimeState(
            coreFunctions = intArrayOf(coreFunction.address),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val fusedGuestComponentStore = componentStore()
        val fusedGuestRoot = fusedGuestComponentStore.reserveRoot(fusedGuestState)
        fusedGuestVstack = vstack().apply { reserveFrame(1) }
        fusedGuestCstack = cstack()
        fusedGuestExecution = executionContext(
            store = store,
            vstack = fusedGuestVstack,
            cstack = fusedGuestCstack,
        )
        fusedGuestBody = FusedComponentFunctionBody(
            componentStore = fusedGuestComponentStore,
            root = fusedGuestRoot,
            runtimeInfo = guestRuntimeInfo,
            plan = fusedPlan,
        )

        val targetType = store.functions[coreFunction.address].functionType
        val callerModule = WasmModuleDecoder(
            ModuleConfig(),
            Resource(CORE_CALLER_PATH).readBytes(),
        ).success()
        val nonFusedImport = StackFunctionAllocator(store, targetType, guestBody)
        nonFusedCaller = ModuleInstantiator(
            RuntimeConfig(),
            store,
            callerModule,
            listOf(Import(ADAPTER_MODULE_NAME, ADAPTER_FUNCTION_NAME, nonFusedImport)),
        ).success()
        nonFusedCallerFunction = (nonFusedCaller.exports.single().value as ExternalValue.Function).address
        val fusedImport = StackFunctionAllocator(store, targetType, fusedGuestBody)
        fusedCaller = ModuleInstantiator(
            RuntimeConfig(),
            store,
            callerModule,
            listOf(Import(ADAPTER_MODULE_NAME, ADAPTER_FUNCTION_NAME, fusedImport)),
        ).success()
        fusedCallerFunction = (fusedCaller.exports.single().value as ExternalValue.Function).address
    }

    @Benchmark
    fun preparedCoreToHost(blackhole: Blackhole) {
        preparedVstack.setFrameSlot(0, INPUT_VALUE)
        preparedBody(preparedVstack, preparedCstack, store, preparedExecution)
        blackhole.consume(preparedVstack.getFrameSlot(0))
    }

    @Benchmark
    fun dynamicCoreToHost(blackhole: Blackhole) {
        dynamicVstack.setFrameSlot(0, INPUT_VALUE)
        dynamicBody(dynamicVstack, dynamicCstack, store, dynamicExecution)
        blackhole.consume(dynamicVstack.getFrameSlot(0))
    }

    @Benchmark
    fun nonFusedAdapterBody(blackhole: Blackhole) {
        guestVstack.setFrameSlot(0, INPUT_VALUE)
        guestBody(guestVstack, guestCstack, store, guestExecution)
        blackhole.consume(guestVstack.getFrameSlot(0))
    }

    @Benchmark
    fun fusedAdapterBody(blackhole: Blackhole) {
        fusedGuestVstack.setFrameSlot(0, INPUT_VALUE)
        fusedGuestBody(fusedGuestVstack, fusedGuestCstack, store, fusedGuestExecution)
        blackhole.consume(fusedGuestVstack.getFrameSlot(0))
    }

    @Benchmark
    fun nonFusedGuestToGuestCall(blackhole: Blackhole) {
        val result = FunctionInvoker(
            RuntimeConfig(),
            store,
            nonFusedCaller,
            nonFusedCallerFunction,
            listOf(NumberValue.I32(INPUT_VALUE.toInt())),
        )
        blackhole.consume(result)
    }

    @Benchmark
    fun fusedGuestToGuestCall(blackhole: Blackhole) {
        val result = FunctionInvoker(
            RuntimeConfig(),
            store,
            fusedCaller,
            fusedCallerFunction,
            listOf(NumberValue.I32(INPUT_VALUE.toInt())),
        )
        blackhole.consume(result)
    }
}

private fun ComponentValue.u32(): UInt = (this as ComponentValue.U32).value

private fun <T, E> Result<T, E>.success(): T = fold(
    success = { it },
    failure = { error("Benchmark setup failed: $it") },
)

private const val INPUT_VALUE = 41L
private const val CORE_TARGET_PATH = "benchmark/component-adapter-target.wasm"
private const val CORE_CALLER_PATH = "benchmark/component-adapter-caller.wasm"
private const val ADAPTER_MODULE_NAME = "adapter"
private const val ADAPTER_FUNCTION_NAME = "call"
