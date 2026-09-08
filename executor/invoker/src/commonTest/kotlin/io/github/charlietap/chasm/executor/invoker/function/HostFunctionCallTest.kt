package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
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
import io.github.charlietap.chasm.runtime.stack.activationHeader
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionCallTest {

    @Test
    fun `zero parameter and result call invokes the host directly`() {
        val caller = moduleInstance()
        val store = store()
        val vstack = vstack()
        val context = executionContext(store = store, vstack = vstack)
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

        HostFunctionCall(vstack, context, caller, function, 0, 0)

        assertEquals(1, invocations)
        assertEquals(0, vstack.sp)
    }

    @Test
    fun `nested host callbacks own independent reference scopes`() {
        val caller = moduleInstance()
        val store = store()
        val vstack = vstack()
        val context = executionContext(store = store, vstack = vstack)
        val nestedFunction = hostFunctionInstance(
            function = HostFunction { _, _ ->
                val references = contextOf<HostResources>().references
                references.rootScoped(2L)
                val marker = references.beginScope()
                assertEquals(2, marker)
                references.endScope(marker)
            },
        )
        val function = hostFunctionInstance(
            function = HostFunction { _, _ ->
                val references = contextOf<HostResources>().references
                references.rootScoped(1L)
                HostFunctionCall(vstack, context, caller, nestedFunction, 0, 0)
                val marker = references.beginScope()
                assertEquals(1, marker)
                references.endScope(marker)
            },
        )

        HostFunctionCall(vstack, context, caller, function, 0, 0)

        val marker = store.heap.beginScope()
        assertEquals(0, marker)
        store.heap.endScope(marker)
    }

    @Test
    fun `strict call exposes raw parameter and result slots`() {
        val caller = moduleInstance()
        val store = store()
        val vstack = vstack().apply {
            reserveDepth(4)
            setFrameSlot(1, 41L)
            setFrameSlot(2, 1L)
        }
        val context = executionContext(store = store, vstack = vstack)
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
        assertEquals(4, vstack.sp)
    }

    @Test
    fun `strict call preserves all numeric encodings and multiple results`() {
        val caller = moduleInstance()
        val store = store()
        val int32 = -123456789
        val int64 = Long.MIN_VALUE + 17
        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678)
        val vstack = vstack().apply {
            reserveDepth(10)
            setFrameSlot(1, int32.toLong())
            setFrameSlot(2, int64)
            setFrameSlot(3, float.toRawBits().toLong())
            setFrameSlot(4, double.toRawBits())
        }
        val context = executionContext(store = store, vstack = vstack)
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
    fun `tail call writes raw results into the caller frame`() {
        val store = store()
        val vstack = vstack().apply {
            reserveDepth(6)
            writeActivationHeader(
                calleeFp = 1,
                activationHeaderSlot = 1,
                activationHeader = activationHeader(19, 1),
            )
            activateFrame(fp = 1, frameSlots = 5)
            setFrameSlot(4, 41L)
        }
        val context = executionContext(store = store, vstack = vstack)
        val function = hostFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = HostFunction { parameters, results ->
                results.writeI32(0, parameters.readI32(0) + 1)
            },
        )

        HostFunctionCall(
            vstack = vstack,
            context = context,
            caller = context.instance,
            function = function,
            parameterSlotBase = 4,
            resultSlotBase = 0,
        )
        val returnIp = ReturnExecutor(vstack, context, resultCount = 1, activationHeaderSlot = 1)

        assertEquals(19, returnIp)
        assertEquals(0, vstack.fp)
        assertEquals(2, vstack.sp)
        assertEquals(42L, vstack.getFrameSlot(1, 0))
    }
}
