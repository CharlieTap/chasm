package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.OperandCopyOrder
import io.github.charlietap.chasm.runtime.instruction.OperandCopyPlan
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals

class StrictControlDispatchersTest {

    @Test
    fun `invokes a host function through a function reference`() {
        val module = moduleInstance()
        val function = hostFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        ) { parameters, results ->
            val caller = contextOf<HostModuleInstance>()
            assertEquals(module, caller)
            results.writeI32(0, parameters.readI32(0) + 1)
        }
        val store = store(functions = mutableListOf(function))
        val vstack = vstack().apply {
            reserveFrame(3)
            setFrameSlot(0, 41)
            setFrameSlot(1, ReferenceValue.Function(functionAddress()).toLong())
        }
        val cstack = cstack(frames = listOf(frame(instance = module)))
        val instruction = ControlSuperInstruction.CallRefS(
            functionSlot = 1,
            operands = OperandCopyPlan(
                operands = arrayOf(CopyOperand.Slot(0)),
                order = OperandCopyOrder.Forward,
            ),
            resultSlotBase = 0,
            callFrameSlot = 2,
        )

        val nextIp = CallDispatcher(instruction)(
            vstack,
            cstack,
            store,
            executionContext(cstack, vstack, store, module),
            11,
        )

        assertEquals(11, nextIp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }

    @Test
    fun `invokes a host function through a table`() {
        val module = moduleInstance()
        val functionType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        val store = store()
        val runtimeType = store.heap.registerRuntimeType(definedType(functionRecursiveType(functionType)))
        val function = hostFunctionInstance(
            rtt = runtimeType,
            functionType = functionType,
        ) { parameters, results ->
            val caller = contextOf<HostModuleInstance>()
            assertEquals(module, caller)
            results.writeI32(0, parameters.readI32(0) + 1)
        }
        store.functions += function
        val table = tableInstance(
            elements = longArrayOf(ReferenceValue.Function(functionAddress()).toLong()),
        )
        val vstack = vstack().apply {
            reserveFrame(2)
            setFrameSlot(0, 41)
        }
        val cstack = cstack(frames = listOf(frame(instance = module)))
        val instruction = ControlSuperInstruction.CallIndirectI(
            elementIndex = 0,
            operands = OperandCopyPlan(
                operands = arrayOf(CopyOperand.Slot(0)),
                order = OperandCopyOrder.Forward,
            ),
            type = runtimeType,
            table = table,
            resultSlotBase = 0,
            callFrameSlot = 1,
        )

        val nextIp = CallDispatcher(instruction)(
            vstack,
            cstack,
            store,
            executionContext(cstack, vstack, store, module),
            11,
        )

        assertEquals(11, nextIp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }

    @Test
    fun `resolves a reference call before copying over its function slot`() {
        val module = moduleInstance()
        val function = wasmFunctionInstance(
            module = module,
            functionType = functionType(params = resultType(listOf(i32ValueType()))),
            function = runtimeFunction(
                body = runtimeExpression(entryIp = 37),
                frameSlots = 1,
            ),
        )
        val store = store(functions = mutableListOf(function))
        val vstack = vstack().apply {
            reserveFrame(2)
            setFrameSlot(0, 42)
            setFrameSlot(1, ReferenceValue.Function(functionAddress()).toLong())
        }
        val cstack = cstack()
        val instruction = ControlSuperInstruction.CallRefS(
            functionSlot = 1,
            operands = OperandCopyPlan(
                operands = arrayOf(CopyOperand.Slot(0)),
                order = OperandCopyOrder.Forward,
            ),
            resultSlotBase = 1,
            callFrameSlot = 1,
        )

        val nextIp = CallDispatcher(instruction)(
            vstack,
            cstack,
            store,
            executionContext(cstack, vstack, store, module),
            11,
        )

        assertEquals(37, nextIp)
        assertEquals(1, vstack.framePointer)
        assertEquals(42L, vstack.getFrameSlot(0))
    }
}
