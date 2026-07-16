package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.StackFunctionCallDispatcher
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.stackFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instruction.callIndirectRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.instruction.callRefRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.instruction.returnCallIndirectRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.instruction.returnCallRefRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.instruction.stackFunctionCallRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.runtime.ext.pushReference
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import kotlin.test.Test
import kotlin.test.assertEquals

class StackFunctionDispatchTest {

    @Test
    fun `call indirect dispatches a stack function without removing the caller frame`() {
        val functionType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        val rtt = rtt()
        var bodyFrames = -1
        val body = StackFunctionBody { stack, control, _, _ ->
            bodyFrames = control.framesDepth()
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(rtt = rtt, functionType = functionType, body = body)
        val table = tableInstance(elements = longArrayOf(functionReferenceValue().toLong()))
        val store = store(functions = mutableListOf(function))
        val cstack = cstack(frames = listOf(frame(instance = moduleInstance())))
        val vstack = vstack()
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val instruction = callIndirectRuntimeInstruction(type = rtt, table = table)
        vstack.pushI32(41)
        vstack.pushI32(0)

        CallIndirectExecutor(vstack, cstack, store, context, instruction)
        val actual = Triple(bodyFrames, cstack.framesDepth(), vstack.pop())

        val expected = Triple(1, 1, 42L)
        assertEquals(expected, actual)
    }

    @Test
    fun `return call indirect dispatches a stack function after removing the caller frame`() {
        val functionType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        val rtt = rtt()
        var bodyFrames = -1
        val body = StackFunctionBody { stack, control, _, _ ->
            bodyFrames = control.framesDepth()
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(rtt = rtt, functionType = functionType, body = body)
        val table = tableInstance(elements = longArrayOf(functionReferenceValue().toLong()))
        val store = store(functions = mutableListOf(function))
        val cstack = cstack(
            frames = listOf(frame(depths = stackDepths(values = 1), instance = moduleInstance())),
            labels = listOf(label()),
        )
        val vstack = vstack()
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val instruction = returnCallIndirectRuntimeInstruction(type = rtt, table = table)
        vstack.pushI32(99)
        vstack.pushI32(41)
        vstack.pushI32(0)

        ReturnCallIndirectExecutor(vstack, cstack, store, context, instruction)
        val actual = listOf(bodyFrames.toLong(), cstack.framesDepth().toLong(), vstack.pop(), vstack.pop())

        val expected = listOf(0L, 0L, 42L, 99L)
        assertEquals(expected, actual)
    }

    @Test
    fun `call ref dispatches a stack function without removing the caller frame`() {
        val functionType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        var bodyFrames = -1
        val body = StackFunctionBody { stack, control, _, _ ->
            bodyFrames = control.framesDepth()
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(functionType = functionType, body = body)
        val dispatchable = StackFunctionCallDispatcher(stackFunctionCallRuntimeInstruction(function))
        val store = store(
            functions = mutableListOf(function),
            instructions = mutableListOf(dispatchable),
        )
        val cstack = cstack(frames = listOf(frame(instance = moduleInstance())))
        val vstack = vstack()
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val instruction = callRefRuntimeInstruction(typeIndex())
        vstack.pushI32(41)
        vstack.pushReference(functionReferenceValue(functionAddress()))

        CallRefExecutor(vstack, cstack, store, context, instruction)
        val actual = Triple(bodyFrames, cstack.framesDepth(), vstack.pop())

        val expected = Triple(1, 1, 42L)
        assertEquals(expected, actual)
    }

    @Test
    fun `return call ref dispatches a stack function after removing the caller frame`() {
        val functionType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        var bodyFrames = -1
        val body = StackFunctionBody { stack, control, _, _ ->
            bodyFrames = control.framesDepth()
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(functionType = functionType, body = body)
        val store = store(functions = mutableListOf(function))
        val cstack = cstack(
            frames = listOf(frame(depths = stackDepths(values = 1), instance = moduleInstance())),
            labels = listOf(label()),
        )
        val vstack = vstack()
        val context = executionContext(store = store, cstack = cstack, vstack = vstack)
        val instruction = returnCallRefRuntimeInstruction(typeIndex())
        vstack.pushI32(99)
        vstack.pushI32(41)
        vstack.pushReference(functionReferenceValue(functionAddress()))

        ReturnCallRefExecutor(vstack, cstack, store, context, instruction)
        val actual = listOf(bodyFrames.toLong(), cstack.framesDepth().toLong(), vstack.pop(), vstack.pop())

        val expected = listOf(0L, 0L, 42L, 99L)
        assertEquals(expected, actual)
    }
}
