package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.executor.runtime.value.i64
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionCallTest {

    @Test
    fun `can execute a host function call and return a result`() {

        val store = store()
        val stack = stack()
        val context = executionContext(
            store = store,
            stack = stack,
        )
        val frame = frame(
            instance = moduleInstance(),
        )

        stack.pushFrame(frame)

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val hostFunction: HostFunction = {
            listOf(
                i32(117),
                i64(118),
            )
        }

        val functionInstance = FunctionInstance.HostFunction(
            type = definedType,
            function = hostFunction,
        )

        val params = listOf(
            i32(115),
            i64(116),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val actual = HostFunctionCall(
            context = context,
            function = functionInstance,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(2, stack.valuesDepth())
        assertEquals(i64(118), stack.popValueOrNull()?.value)
        assertEquals(i32(117), stack.popValueOrNull()?.value)
    }

    @Test
    fun `returns an error if the host function does not return values matching its type`() {

        val store = store()
        val stack = stack()
        val context = executionContext(
            store = store,
            stack = stack,
        )
        val frame = frame(
            instance = moduleInstance(),
        )

        stack.pushFrame(frame)
        stack.push(
            Instruction(
                instruction = dispatchableInstruction(),
                tag = Stack.Entry.InstructionTag.FRAME,
            ),
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val hostFunction: HostFunction = {
            listOf(
                i32(117),
                i32(118),
            )
        }

        val functionInstance = FunctionInstance.HostFunction(
            type = definedType,
            function = hostFunction,
        )

        val params = listOf(
            i32(115),
            i64(116),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val expected = Err(
            InvocationError.HostFunctionInconsistentWithType(
                i64ValueType(),
                i32(118),
            ),
        )

        val actual = HostFunctionCall(
            context = context,
            function = functionInstance,
        )

        assertEquals(expected, actual)
    }
}
