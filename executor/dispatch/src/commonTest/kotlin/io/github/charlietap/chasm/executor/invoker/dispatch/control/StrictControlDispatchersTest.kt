package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instruction.operandTransfer
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.function.classifyLocalInitialization
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.program.Program
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals

class StrictControlDispatchersTest {

    @Test
    fun `returns a direct Wasm result to its compiled caller slot`() {
        val program = Program().apply {
            append(
                WasmCallDispatcher(
                    operands = operandTransfer(emptyArray(), destinationSlotBase = 2),
                    callFrameOffset = 2,
                    frameSlots = 2,
                    entryIp = 2,
                    activationHeaderSlot = 1,
                    localInitialization = LocalInitialization.None,
                    callSiteIp = 0,
                    resultDestinationSlot = 0,
                ),
            )
            append(dispatchableInstruction())
            append(FunctionReturnDispatcher(operandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 0), activationHeaderSlot = 1))
        }
        val store = store(program = program)
        val vstack = vstack().apply { reserveDepth(4) }
        val context = executionContext(vstack, store)

        val calleeEntryIp = program.instructions[0](vstack, context, 1)
        vstack.setFrameSlot(0, 42)
        val continuationIp = program.instructions[calleeEntryIp](vstack, context, calleeEntryIp + 1)

        assertEquals(1, continuationIp)
        assertEquals(0, vstack.fp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }

    @Test
    fun `initialises the callee's locals for every local initialization shape`() {
        val localInitialValues = listOf(
            longArrayOf(),
            longArrayOf(0),
            longArrayOf(0, 0),
            longArrayOf(0, 0, 0),
            longArrayOf(0, 0, 0, 0),
            longArrayOf(0, 0, 0, 0, 0),
            longArrayOf(0, 1),
        )

        for (locals in localInitialValues) {
            val localInitialization = classifyLocalInitialization(locals)
            val frameSlots = locals.size + 1
            val call = WasmCallDispatcher(
                operands = operandTransfer(emptyArray(), destinationSlotBase = 0),
                callFrameOffset = 2,
                frameSlots = frameSlots,
                entryIp = 100,
                activationHeaderSlot = 0,
                localInitialization = localInitialization,
                callSiteIp = 0,
                resultDestinationSlot = null,
            )
            val tailCall = ReturnWasmCallDispatcher(
                frameSlots = frameSlots,
                entryIp = 100,
                activationHeaderSlot = 0,
                callerActivationHeaderSlot = 0,
                operands = operandTransfer(emptyArray(), destinationSlotBase = 0),
                localInitialization = localInitialization,
            )

            for (dispatcher in listOf(call, tailCall)) {
                val vstack = vstack().apply {
                    reserveDepth(2 + frameSlots)
                    for (slot in 0 until 2 + frameSlots) setFrameSlot(slot, -1)
                }
                val context = executionContext(vstack, store())

                assertEquals(100, dispatcher(vstack, context, 1))
                for (index in locals.indices) assertEquals(locals[index], vstack.getFrameSlot(1 + index))
            }
        }
    }

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
            reserveDepth(3)
            setFrameSlot(0, 41)
            setFrameSlot(1, functionReferenceValue().toLong())
        }

        val nextIp = CallRefSDispatcher(
            functionSlot = 1,
            operands = operandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 2),
            caller = module,
            callFrameOffset = 2,
            callSiteIp = 10,
            resultDestinationSlot = null,
        )(vstack, executionContext(vstack, store, module), 11)

        assertEquals(11, nextIp)
        assertEquals(42L, vstack.getFrameSlot(2))
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
            elements = longArrayOf(functionReferenceValue().toLong()),
        )
        val vstack = vstack().apply {
            reserveDepth(2)
            setFrameSlot(0, 41)
        }

        val nextIp = CallIndirectIDispatcher(
            elementIndex = 0,
            operands = operandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 1),
            type = runtimeType,
            table = table,
            caller = module,
            callFrameOffset = 1,
            callSiteIp = 10,
            resultDestinationSlot = null,
        )(vstack, executionContext(vstack, store, module), 11)

        assertEquals(11, nextIp)
        assertEquals(42L, vstack.getFrameSlot(1))
    }

    @Test
    fun `writes an indirect host result directly to its compiled caller slot`() {
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
            results.writeI32(0, parameters.readI32(0) + 1)
        }
        store.functions += function
        val table = tableInstance(
            elements = longArrayOf(functionReferenceValue().toLong()),
        )
        val vstack = vstack().apply {
            reserveDepth(2)
            setFrameSlot(0, 41)
        }

        val nextIp = CallIndirectIDispatcher(
            elementIndex = 0,
            operands = operandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 1),
            type = runtimeType,
            table = table,
            caller = module,
            callFrameOffset = 1,
            callSiteIp = 10,
            resultDestinationSlot = 0,
        )(vstack, executionContext(vstack, store, module), 11)

        assertEquals(11, nextIp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }

    @Test
    fun `writes an indirect Wasm result directly to its compiled caller slot`() {
        val module = moduleInstance()
        val program = Program()
        val store = store(program = program)
        val type = functionType(results = resultType(listOf(i32ValueType())))
        val runtimeType = store.heap.registerRuntimeType(definedType(functionRecursiveType(type)))
        val function = wasmFunctionInstance(
            module = module,
            rtt = runtimeType,
            functionType = type,
            function = runtimeFunction(
                body = runtimeExpression(entryIp = 4),
                frameSlots = 2,
            ),
        )
        store.functions += function
        val table = tableInstance(
            elements = longArrayOf(functionReferenceValue().toLong()),
        )
        program.append(dispatchableInstruction())
        program.append(dispatchableInstruction())
        program.append(
            CallIndirectIDispatcher(
                elementIndex = 0,
                operands = operandTransfer(emptyArray(), destinationSlotBase = 2),
                type = runtimeType,
                table = table,
                caller = module,
                callFrameOffset = 2,
                callSiteIp = 2,
                resultDestinationSlot = 0,
            ),
        )
        program.append(dispatchableInstruction())
        program.append(FunctionReturnDispatcher(operandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 0), activationHeaderSlot = 1))
        val vstack = vstack().apply { reserveDepth(4) }
        val context = executionContext(vstack, store, module)

        val calleeEntryIp = program.instructions[2](vstack, context, 3)
        vstack.setFrameSlot(0, 42)
        val continuationIp = program.instructions[calleeEntryIp](vstack, context, calleeEntryIp + 1)

        assertEquals(3, continuationIp)
        assertEquals(0, vstack.fp)
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
            reserveDepth(2)
            setFrameSlot(0, 42)
            setFrameSlot(1, functionReferenceValue().toLong())
        }

        val nextIp = CallRefSDispatcher(
            functionSlot = 1,
            operands = operandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 2),
            caller = module,
            callFrameOffset = 2,
            callSiteIp = 10,
            resultDestinationSlot = null,
        )(vstack, executionContext(vstack, store, module), 11)

        assertEquals(37, nextIp)
        assertEquals(2, vstack.fp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }
}
