package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.stack.vstack
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
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )
        val frame = frame(
            instance = moduleInstance(),
        )

        cstack.push(frame)

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
            functionType = functionType,
            function = hostFunction,
        )

        val params = listOf(
            i32(115),
            i64(116),
        )

        params.forEach { value ->
            vstack.push(value)
        }

        val actual = HostFunctionCall(
            context = context,
            function = functionInstance,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
        assertEquals(2, vstack.depth())
        assertEquals(i64(118), vstack.pop())
        assertEquals(i32(117), vstack.pop())
    }
}
