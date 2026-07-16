package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import io.github.charlietap.chasm.executor.instantiator.allocation.function.StackFunctionAllocator
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.stackFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.type.factory.RTTFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class StackFunctionAllocatorTest {

    @Test
    fun `can allocate a stack function`() {
        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )
        val functionType = functionType()
        val body = StackFunctionBody { _, _, _, _ -> }
        val functionInstruction = dispatchableInstruction()
        val callDispatcher: Dispatcher<ControlInstruction.StackFunctionCall> = {
            functionInstruction
        }
        val rtt = rtt()
        val rttFactory: RTTFactory = { _, _ ->
            rtt
        }
        val expectedInstance = stackFunctionInstance(
            rtt = rtt,
            functionType = functionType,
            body = body,
        )
        val expected = functionExternalValue(
            address = functionAddress(0),
        )

        val actual = StackFunctionAllocator(
            store = store,
            functionType = functionType,
            body = body,
            callDispatcher = callDispatcher,
            rttFactory = rttFactory,
        )

        assertEquals(expected, actual)
        assertEquals(expectedInstance, functions[0])
        assertEquals(functionInstruction, store.instructions[0])
    }

    @Test
    fun `does not allocate a stack function when dispatch creation fails`() {
        val store = store()
        val functionType = functionType()
        val body = StackFunctionBody { _, _, _, _ -> }
        val callDispatcher: Dispatcher<ControlInstruction.StackFunctionCall> = {
            error("dispatch creation failed")
        }
        val rtt = rtt()
        val rttFactory: RTTFactory = { _, _ ->
            rtt
        }

        assertFails {
            StackFunctionAllocator(
                store = store,
                functionType = functionType,
                body = body,
                callDispatcher = callDispatcher,
                rttFactory = rttFactory,
            )
        }

        assertEquals(emptyList(), store.functions)
        assertEquals(emptyList(), store.instructions)
    }
}
