package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.f64ValueType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.readF32
import io.github.charlietap.chasm.host.readF64
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.readI64
import io.github.charlietap.chasm.host.writeF32
import io.github.charlietap.chasm.host.writeF64
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.host.writeI64
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionCallTest {

    @Test
    fun `zero parameter and result call invokes the host directly`() {
        val caller = moduleInstance()
        val store = store()
        val cstack = cstack(frames = listOf(frame(instance = caller)))
        val vstack = vstack()
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        var invocations = 0
        val function = hostFunctionInstance(
            function = HostFunction { _, _ ->
                val actualCaller = contextOf<HostModuleInstance>()
                val actualContext = contextOf<HostResources>()
                assertEquals(caller, actualCaller)
                assertEquals(context, actualContext)
                invocations++
            },
        )

        HostFunctionCall(vstack, context, caller, function)

        assertEquals(1, invocations)
        assertEquals(0, vstack.depth())
    }

    @Test
    fun `strict call exposes raw parameter and result slots`() {
        val caller = moduleInstance()
        val store = store()
        val cstack = cstack(frames = listOf(frame(instance = caller)))
        val vstack = vstack().apply {
            reserveFrame(4)
            setFrameSlot(1, 41L)
            setFrameSlot(2, 1L)
        }
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val function = hostFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = HostFunction { parameters, results ->
                val actualCaller = contextOf<HostModuleInstance>()
                val actualContext = contextOf<HostResources>()
                assertEquals(caller, actualCaller)
                assertEquals(context, actualContext)
                results.writeI32(0, parameters.readI32(0) + parameters.readI32(1))
            },
        )

        HostFunctionCall(
            vstack = vstack,
            context = context,
            caller = caller,
            function = function,
            parameterSlotBase = 1,
            resultSlotBase = 3,
        )

        assertEquals(42L, vstack.getFrameSlot(3))
        assertEquals(4, vstack.depth())
    }

    @Test
    fun `strict call preserves all numeric encodings and multiple results`() {
        val caller = moduleInstance()
        val store = store()
        val cstack = cstack(frames = listOf(frame(instance = caller)))
        val int32 = -123456789
        val int64 = Long.MIN_VALUE + 17
        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678)
        val vstack = vstack().apply {
            reserveFrame(10)
            setFrameSlot(1, int32.toLong())
            setFrameSlot(2, int64)
            setFrameSlot(3, float.toRawBits().toLong())
            setFrameSlot(4, double.toRawBits())
        }
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val numericTypes = listOf(i32ValueType(), i64ValueType(), f32ValueType(), f64ValueType())
        val function = hostFunctionInstance(
            functionType = functionType(
                params = resultType(numericTypes),
                results = resultType(numericTypes),
            ),
            function = HostFunction { parameters, results ->
                results.writeI32(0, parameters.readI32(0))
                results.writeI64(1, parameters.readI64(1))
                results.writeF32(2, parameters.readF32(2))
                results.writeF64(3, parameters.readF64(3))
            },
        )

        HostFunctionCall(
            vstack = vstack,
            context = context,
            caller = caller,
            function = function,
            parameterSlotBase = 1,
            resultSlotBase = 6,
        )

        assertEquals(int32.toLong(), vstack.getFrameSlot(6))
        assertEquals(int64, vstack.getFrameSlot(7))
        assertEquals(float.toRawBits().toLong(), vstack.getFrameSlot(8))
        assertEquals(double.toRawBits(), vstack.getFrameSlot(9))
    }

    @Test
    fun `stack call replaces parameters with raw results`() {
        val caller = moduleInstance()
        val store = store()
        val cstack = cstack(frames = listOf(frame(instance = caller)))
        val vstack = vstack().apply {
            pushI32(20)
            pushI32(22)
        }
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val function = hostFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = HostFunction { parameters, results ->
                results.writeI32(0, parameters.readI32(0) + parameters.readI32(1))
            },
        )

        HostFunctionCall(vstack, context, caller, function)

        assertEquals(1, vstack.depth())
        assertEquals(42, vstack.popI32())
    }

    @Test
    fun `tail call writes raw results into the caller frame`() {
        val caller = moduleInstance()
        val store = store()
        val cstack = cstack(
            frames = listOf(
                frame(
                    arity = 1,
                    valueDepth = 2,
                    previousFramePointer = 0,
                    instance = caller,
                    resultSlotBase = 1,
                    returnIp = 19,
                ),
            ),
        )
        val vstack = vstack().apply {
            reserveFrame(5)
            setFrameSlot(4, 123L)
            pushI32(41)
        }
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val function = hostFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = HostFunction { parameters, results ->
                results.writeI32(0, parameters.readI32(0) + 1)
            },
        )

        val returnIp = ReturnHostFunctionCall(vstack, cstack, context, function)

        assertEquals(19, returnIp)
        assertEquals(0, cstack.framesDepth())
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(42L, vstack.getFrameSlot(1))
        assertEquals(123L, vstack.getFrameSlot(4))
    }
}
