package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.ir.type.functionType
import io.github.charlietap.chasm.type.ir.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionAllocatorTest {

    @Test
    fun `can allocate a host function`() {

        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val functionType = functionType()
        val hostFunction: HostFunction = { emptyList() }

        val functionInstruction = dispatchableInstruction()
        val callDispatcher: Dispatcher<ControlInstruction.HostFunctionCall> = {
            functionInstruction
        }

        val expectedInstance = FunctionInstance.HostFunction(
            type = functionType.definedType(),
            functionType = functionType,
            function = hostFunction,
        )
        val expected = functionExternalValue(
            address = functionAddress(0),
        )

        val actual = HostFunctionAllocator(
            store = store,
            functionType = functionType,
            function = hostFunction,
            callDispatcher = callDispatcher,
        )

        assertEquals(expected, actual)
        assertEquals(expectedInstance, functions[0])
        assertEquals(functionInstruction, store.instructions[0])
    }
}
