package io.github.charlietap.chasm.executor.invoker.component

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalValueTupleLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLowerPlan
import io.github.charlietap.chasm.fixture.runtime.component.function.hostImportComponentFunction
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.component.value.u32ComponentValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LowerResultPassing
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import kotlin.test.Test
import kotlin.test.assertEquals

class LoweredComponentFunctionBodyTest {

    @Test
    fun `reads an indirect parameter tuple and writes into the caller result area`() {
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
            parameterPassing = LowerParameterPassing.IndirectPointer,
            resultPassing = LowerResultPassing.IndirectPointer,
            memorySlot = 0,
        )
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(hostImportComponentFunction()),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32, size32 = 4u, alignment32 = 4u)),
            callPlans = listOf(plan),
        )
        val hostFunction = RuntimeComponentHostFunction.Dynamic { _, arguments ->
            Ok(listOf(u32ComponentValue(arguments.single().u32() + 1u)))
        }
        val state = componentRuntimeState(
            memories = intArrayOf(0),
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        val memory = memoryInstance(
            type = memoryType(limits = limits(min = 1u)),
            data = LinearMemoryFactory(LinearMemory.Pages(1u)),
        )
        val store = store(memories = mutableListOf(memory))
        val vstack = vstack()
        val cstack = cstack()
        val execution = executionContext(store = store, vstack = vstack, cstack = cstack)
        val body = LoweredComponentFunctionBody(
            componentStore = componentStore,
            root = root,
            runtimeInfo = runtimeInfo,
            plan = plan,
        )
        I32Writer(memory.data, PARAMETER_POINTER, 41)
        vstack.reserveFrame(2)
        vstack.setFrameSlot(0, PARAMETER_POINTER.toLong())
        vstack.setFrameSlot(1, RESULT_POINTER.toLong())

        body(vstack, cstack, store, execution)
        val actual = I32Reader(memory.data, RESULT_POINTER)

        val expected = 42
        assertEquals(expected, actual)
    }

    @Test
    fun `lifts core parameters into a host callback and lowers its result to the frame`() {
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
        val runtimeInfo = componentRuntimeInfo(
            functions = listOf(hostImportComponentFunction()),
            linearMemoryLayouts = listOf(linearMemoryLayout(kind = CanonicalLayoutKind.U32)),
            callPlans = listOf(plan),
        )
        var callbackArguments = emptyList<ComponentValue>()
        val hostFunction = RuntimeComponentHostFunction.Dynamic { _, arguments ->
            callbackArguments = arguments
            Ok(listOf(u32ComponentValue(arguments.single().u32() + 1u)))
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        val store = store()
        val vstack = vstack()
        val cstack = cstack()
        val execution = executionContext(store = store, vstack = vstack, cstack = cstack)
        val body = LoweredComponentFunctionBody(
            componentStore = componentStore,
            root = root,
            runtimeInfo = runtimeInfo,
            plan = plan,
        )
        vstack.reserveFrame(1)
        vstack.setFrameSlot(0, 41L)

        body(vstack, cstack, store, execution)
        val actual = LoweredCallObservation(
            arguments = callbackArguments,
            result = vstack.getFrameSlot(0),
            mayLeave = state.states.mayLeave[0],
        )

        val expected = LoweredCallObservation(
            arguments = listOf(u32ComponentValue(41u)),
            result = 42L,
            mayLeave = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `prepared host imports receive flat values and the logical caller without materializing component values`() {
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
            linearMemoryLayouts = listOf(
                linearMemoryLayout(
                    kind = CanonicalLayoutKind.Flags,
                    elementCount = 3,
                ),
            ),
            callPlans = listOf(plan),
        )
        var callbackArguments = emptyList<Long>()
        var callerInstanceIndex = -1
        val hostFunction = RuntimeComponentHostFunction.Prepared { context, arguments, argumentCount, results ->
            callbackArguments = arguments.take(argumentCount)
            callerInstanceIndex = context.scope.callerInstanceIndex
            results[0] = arguments[0]
            Ok(1)
        }
        val state = componentRuntimeState(
            hostFunctions = arrayOf(hostFunction),
            states = componentInstanceStates(parents = intArrayOf(-1)),
        )
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        val store = store()
        val vstack = vstack()
        val cstack = cstack()
        val execution = executionContext(store = store, vstack = vstack, cstack = cstack)
        val body = LoweredComponentFunctionBody(
            componentStore = componentStore,
            root = root,
            runtimeInfo = runtimeInfo,
            plan = plan,
        )
        vstack.reserveFrame(1)
        vstack.setFrameSlot(0, FLAGS_WITH_UNDECLARED_BITS)

        body(vstack, cstack, store, execution)
        val actual = PreparedHostCallObservation(
            arguments = callbackArguments,
            callerInstanceIndex = callerInstanceIndex,
            result = vstack.getFrameSlot(0),
            mayLeave = state.states.mayLeave[0],
        )

        val expected = PreparedHostCallObservation(
            arguments = listOf(DECLARED_FLAG_BITS),
            callerInstanceIndex = 0,
            result = DECLARED_FLAG_BITS,
            mayLeave = true,
        )
        assertEquals(expected, actual)
    }
}

private fun ComponentValue.u32(): UInt = (this as ComponentValue.U32).value

private data class LoweredCallObservation(
    val arguments: List<ComponentValue>,
    val result: Long,
    val mayLeave: Boolean,
)

private data class PreparedHostCallObservation(
    val arguments: List<Long>,
    val callerInstanceIndex: Int,
    val result: Long,
    val mayLeave: Boolean,
)

private const val PARAMETER_POINTER = 8
private const val RESULT_POINTER = 16
private const val FLAGS_WITH_UNDECLARED_BITS = 0b1001L
private const val DECLARED_FLAG_BITS = 0b0001L
