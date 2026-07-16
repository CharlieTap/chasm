package io.github.charlietap.chasm.executor.invoker.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalValueTupleLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLiftPlan
import io.github.charlietap.chasm.fixture.runtime.component.function.hostImportComponentFunction
import io.github.charlietap.chasm.fixture.runtime.component.function.liftedCoreComponentFunction
import io.github.charlietap.chasm.fixture.runtime.component.index.preparedComponentFunctionIndex
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.canonicalHandleTable
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.component.value.flagsComponentValue
import io.github.charlietap.chasm.fixture.runtime.component.value.u32ComponentValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentFunctionInvokerTest {

    @Test
    fun `direct host imports reject arguments that do not match the prepared function type`() {
        val parameterTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 4u,
            alignment32 = 4u,
            flatCount = 1,
        )
        val function = hostImportComponentFunction(parameterTuple = parameterTuple)
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
        )
        var hostInvoked = false
        val hostFunction = RuntimeComponentHostFunction.Dynamic { _, _ ->
            hostInvoked = true
            Ok(emptyList())
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val result = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = listOf(flagsComponentValue()),
            scope = scope,
            coreInvoker = ::RawFunctionInvoker,
        )
        componentStore.exitCall()
        val actual = HostCallObservation(result, hostInvoked, state.states.poisoned.single())

        val expected = HostCallObservation(
            result = Err(ComponentInvocationError.InvalidCanonicalValue("component value does not match its function type")),
            hostInvoked = false,
            poisoned = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `direct host imports reject results that do not match the prepared function type`() {
        val resultTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 4u,
            alignment32 = 4u,
            flatCount = 1,
        )
        val function = hostImportComponentFunction(resultTuple = resultTuple)
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
        )
        val hostFunction = RuntimeComponentHostFunction.Dynamic { _, _ ->
            Ok(listOf(flagsComponentValue()))
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val result = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
            scope = scope,
            coreInvoker = ::RawFunctionInvoker,
        )
        componentStore.exitCall()
        val actual = HostResultObservation(result, state.states.poisoned.single())

        val expected = HostResultObservation(
            result = Err(ComponentInvocationError.InvalidCanonicalValue("component value does not match its function type")),
            poisoned = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a rejected public host argument does not poison later invocations`() {
        val parameterTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 4u,
            alignment32 = 4u,
            flatCount = 1,
        )
        val function = hostImportComponentFunction(parameterTuple = parameterTuple)
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
        )
        var hostCallCount = 0
        val hostFunction = RuntimeComponentHostFunction.Dynamic { _, _ ->
            hostCallCount += 1
            Ok(emptyList())
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val coreStore = store()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(
            root,
            runtimeComponentInstance(runtimeInfo = runtimeInfo, state = state),
        )

        val rejected = ComponentFunctionInvoker(
            config = runtimeConfig(),
            store = coreStore,
            componentStore = componentStore,
            root = root,
            function = preparedComponentFunctionIndex(),
            arguments = listOf(flagsComponentValue()),
        )
        val poisonedAfterRejection = state.states.poisoned.single()
        val accepted = ComponentFunctionInvoker(
            config = runtimeConfig(),
            store = coreStore,
            componentStore = componentStore,
            root = root,
            function = preparedComponentFunctionIndex(),
            arguments = listOf(u32ComponentValue()),
        )
        val actual = PublicHostRetryObservation(
            rejected = rejected,
            accepted = accepted,
            poisonedAfterRejection = poisonedAfterRejection,
            hostCallCount = hostCallCount,
        )

        val expected = PublicHostRetryObservation(
            rejected = Err(ComponentInvocationError.InvalidCanonicalValue("component value does not match its function type")),
            accepted = Ok(emptyList()),
            poisonedAfterRejection = false,
            hostCallCount = 1,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a rejected lifted argument restores permission to leave the instance`() {
        val parameterTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 4u,
            alignment32 = 4u,
            flatCount = 1,
        )
        val plan = linearMemoryLiftPlan(parameterTuple = parameterTuple)
        val function = liftedCoreComponentFunction(liftPlan = plan)
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
        )
        var coreInvoked = false
        val state = componentRuntimeState(
            coreFunctions = intArrayOf(CORE_FUNCTION_ADDRESS),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val coreInvoker: RawFunctionInvoker = { _, _, _, _, _, _, _ ->
            coreInvoked = true
            Ok(0)
        }
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val result = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = listOf(flagsComponentValue()),
            scope = scope,
            coreInvoker = coreInvoker,
        )
        componentStore.exitCall()
        val actual = LiftFailureObservation(
            result = result,
            coreInvoked = coreInvoked,
            mayLeave = state.states.mayLeave[0],
            poisoned = state.states.poisoned[0],
        )

        val expected = LiftFailureObservation(
            result = Err(ComponentInvocationError.InvalidCanonicalValue("component u32 value expected")),
            coreInvoked = false,
            mayLeave = true,
            poisoned = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a core trap poisons a lifted component invocation`() {
        val function = liftedCoreComponentFunction()
        val runtimeInfo = componentRuntimeInfo(functions = listOf(function))
        val state = componentRuntimeState(
            coreFunctions = intArrayOf(CORE_FUNCTION_ADDRESS),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val coreInvoker: RawFunctionInvoker = { _, _, _, _, _, _, _ ->
            Err(InvocationError.Unreachable)
        }
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val result = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
            scope = scope,
            coreInvoker = coreInvoker,
        )
        componentStore.exitCall()
        val actual = LiftPoisonObservation(result, state.states.poisoned.single())

        val expected = LiftPoisonObservation(
            result = Err(ComponentInvocationError.CoreTrap(InvocationError.Unreachable)),
            poisoned = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a failed post return poisons a lifted component invocation`() {
        val plan = linearMemoryLiftPlan(postReturnSlot = 0)
        val function = liftedCoreComponentFunction(liftPlan = plan)
        val runtimeInfo = componentRuntimeInfo(functions = listOf(function))
        val state = componentRuntimeState(
            coreFunctions = intArrayOf(CORE_FUNCTION_ADDRESS),
            postReturns = intArrayOf(POST_RETURN_ADDRESS),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val coreInvoker: RawFunctionInvoker = { _, _, _, address, _, _, _ ->
            Ok(if (address.address == CORE_FUNCTION_ADDRESS) 0 else 1)
        }
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val result = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
            scope = scope,
            coreInvoker = coreInvoker,
        )
        componentStore.exitCall()
        val actual = LiftPoisonObservation(result, state.states.poisoned.single())

        val expected = LiftPoisonObservation(
            result = Err(ComponentInvocationError.InvalidCanonicalValue("canonical post-return must not return values")),
            poisoned = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `failed borrow cleanup poisons the public component invocation`() {
        val function = hostImportComponentFunction()
        val runtimeInfo = componentRuntimeInfo(functions = listOf(function))
        val table = canonicalHandleTable()
        val type = runtimeResourceTypeAddress()
        val hostFunction = RuntimeComponentHostFunction.Dynamic { context, _ ->
            val handle = table.insertBorrow(type, representation = 42, callToken = context.scope.callToken)
            context.scope.recordGuestBorrow(table, handle)
            Ok(emptyList())
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(
            root,
            runtimeComponentInstance(runtimeInfo = runtimeInfo, state = state),
        )

        val result = ComponentFunctionInvoker(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = root,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
        )
        val actual = CleanupFailureObservation(
            result = result,
            poisoned = state.states.poisoned.single(),
            tableSize = table.size,
        )

        val expected = CleanupFailureObservation(
            result = Err(ComponentInvocationError.InvalidCanonicalValue("borrow handles remain at the end of the call")),
            poisoned = true,
            tableSize = 0,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `lifted flags ignore bits beyond the declared labels`() {
        val resultTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 1u,
            alignment32 = 1u,
            flatCount = 1,
        )
        val plan = linearMemoryLiftPlan(
            resultTuple = resultTuple,
        )
        val function = liftedCoreComponentFunction(liftPlan = plan)
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(
                linearMemoryLayout(
                    kind = CanonicalLayoutKind.Flags,
                    elementCount = 3,
                ),
            ),
        )
        val state = componentRuntimeState(
            coreFunctions = intArrayOf(CORE_FUNCTION_ADDRESS),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val coreInvoker: RawFunctionInvoker = { _, _, _, _, _, _, results ->
            results[0] = FLAGS_WITH_UNDECLARED_BITS
            Ok(1)
        }
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val actual = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
            scope = scope,
            coreInvoker = coreInvoker,
        )
        componentStore.exitCall()

        val expected = Ok(listOf(flagsComponentValue(DECLARED_FLAG_BITS)))
        assertEquals(expected, actual)
    }

    @Test
    fun `post return receives the original core result while the instance cannot leave`() {
        val resultTuple = canonicalValueTupleLayout(
            layouts = intArrayOf(0),
            offsets32 = uintArrayOf(0u),
            size32 = 4u,
            alignment32 = 4u,
            flatCount = 1,
        )
        val plan = linearMemoryLiftPlan(
            resultTuple = resultTuple,
            postReturnSlot = 0,
        )
        val function = liftedCoreComponentFunction(liftPlan = plan)
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(function),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
        )
        val states = componentInstanceStates(parents = intArrayOf(-1))
        val state = componentRuntimeState(
            coreFunctions = intArrayOf(CORE_FUNCTION_ADDRESS),
            postReturns = intArrayOf(POST_RETURN_ADDRESS),
            states = states,
        )
        val calls = mutableListOf<CoreCallObservation>()
        val coreInvoker: RawFunctionInvoker = { _, _, _, address, arguments, argumentCount, results ->
            calls += CoreCallObservation(
                address = address.address,
                arguments = arguments.take(argumentCount),
                mayLeave = state.states.mayLeave[0],
            )
            if (address.address == CORE_FUNCTION_ADDRESS) {
                results[0] = RESULT_VALUE
                Ok(1)
            } else {
                Ok(0)
            }
        }
        val componentStore = componentStore()
        val scope = componentStore.enterCall()

        val result = invokePreparedFunction(
            config = runtimeConfig(),
            store = store(),
            componentStore = componentStore,
            root = componentRootAddress(),
            runtimeInfo = runtimeInfo,
            state = state,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
            scope = scope,
            coreInvoker = coreInvoker,
        )
        componentStore.exitCall()
        val actual = PostReturnObservation(
            result = result,
            calls = calls,
            mayLeave = state.states.mayLeave[0],
        )

        val expected = PostReturnObservation(
            result = Ok(listOf(u32ComponentValue(RESULT_VALUE.toUInt()))),
            calls = listOf(
                CoreCallObservation(CORE_FUNCTION_ADDRESS, emptyList(), mayLeave = true),
                CoreCallObservation(POST_RETURN_ADDRESS, listOf(RESULT_VALUE), mayLeave = false),
            ),
            mayLeave = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a public invocation from a host callback retains host origin without poisoning`() {
        val function = hostImportComponentFunction()
        val runtimeInfo = componentRuntimeInfo(functions = listOf(function))
        val componentStore = componentStore()
        val coreStore = store()
        var root = componentRootAddress()
        var callCount = 0
        val hostCallers = mutableListOf<Boolean>()
        val hostFunction = RuntimeComponentHostFunction.Dynamic { context, _ ->
            callCount += 1
            hostCallers += context.scope.isHostCaller
            if (callCount == 1) {
                ComponentFunctionInvoker(
                    config = runtimeConfig(),
                    store = coreStore,
                    componentStore = componentStore,
                    root = root,
                    function = preparedComponentFunctionIndex(),
                    arguments = emptyList(),
                )
            } else {
                Ok(emptyList())
            }
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        root = componentStore.reserveRoot(state)
        componentStore.publishRoot(
            root,
            runtimeComponentInstance(runtimeInfo = runtimeInfo, state = state),
        )

        val firstResult = ComponentFunctionInvoker(
            config = runtimeConfig(),
            store = coreStore,
            componentStore = componentStore,
            root = root,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
        )
        val poisonedAfterRejectedCall = state.states.poisoned.single()
        val secondResult = ComponentFunctionInvoker(
            config = runtimeConfig(),
            store = coreStore,
            componentStore = componentStore,
            root = root,
            function = preparedComponentFunctionIndex(),
            arguments = emptyList(),
        )
        val actual = ReentrantCallObservation(
            firstResult = firstResult,
            secondResult = secondResult,
            callCount = callCount,
            hostCallers = hostCallers,
            poisonedAfterRejectedCall = poisonedAfterRejectedCall,
        )

        val expected = ReentrantCallObservation(
            firstResult = Err(ComponentInvocationError.CannotEnterComponentInstance),
            secondResult = Ok(emptyList()),
            callCount = 2,
            hostCallers = listOf(true, true),
            poisonedAfterRejectedCall = false,
        )
        assertEquals(expected, actual)
    }
}

private data class PostReturnObservation(
    val result: Result<List<ComponentValue>, *>,
    val calls: List<CoreCallObservation>,
    val mayLeave: Boolean,
)

private data class HostCallObservation(
    val result: Result<List<ComponentValue>, ComponentInvocationError>,
    val hostInvoked: Boolean,
    val poisoned: Boolean,
)

private data class HostResultObservation(
    val result: Result<List<ComponentValue>, ComponentInvocationError>,
    val poisoned: Boolean,
)

private data class LiftFailureObservation(
    val result: Result<List<ComponentValue>, ComponentInvocationError>,
    val coreInvoked: Boolean,
    val mayLeave: Boolean,
    val poisoned: Boolean,
)

private data class LiftPoisonObservation(
    val result: Result<List<ComponentValue>, ComponentInvocationError>,
    val poisoned: Boolean,
)

private data class CoreCallObservation(
    val address: Int,
    val arguments: List<Long>,
    val mayLeave: Boolean,
)

private data class ReentrantCallObservation(
    val firstResult: Result<List<ComponentValue>, ComponentInvocationError>,
    val secondResult: Result<List<ComponentValue>, ComponentInvocationError>,
    val callCount: Int,
    val hostCallers: List<Boolean>,
    val poisonedAfterRejectedCall: Boolean,
)

private data class PublicHostRetryObservation(
    val rejected: Result<List<ComponentValue>, ComponentInvocationError>,
    val accepted: Result<List<ComponentValue>, ComponentInvocationError>,
    val poisonedAfterRejection: Boolean,
    val hostCallCount: Int,
)

private data class CleanupFailureObservation(
    val result: Result<List<ComponentValue>, ComponentInvocationError>,
    val poisoned: Boolean,
    val tableSize: Int,
)

private const val CORE_FUNCTION_ADDRESS = 3
private const val POST_RETURN_ADDRESS = 4
private const val RESULT_VALUE = 42L
private const val FLAGS_WITH_UNDECLARED_BITS = 0b1001L
private const val DECLARED_FLAG_BITS = 0b0001u
