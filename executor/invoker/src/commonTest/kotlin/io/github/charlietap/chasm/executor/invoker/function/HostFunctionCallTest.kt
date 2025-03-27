package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.i64
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.instance.HostFunction
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

        val hostFunction: HostFunction = {
            listOf(
                i32(117),
                i64(118),
            )
        }

        val functionInstance = hostFunctionInstance(
            functionType = functionType,
            function = hostFunction,
        )

        vstack.pushI32(115)
        vstack.pushI64(116)

        val actual = HostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
        assertEquals(2, vstack.depth())
        assertEquals(118, vstack.pop())
        assertEquals(117, vstack.pop())
    }
}
