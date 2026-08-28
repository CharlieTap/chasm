package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
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
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals

class StrictControlDispatchersTest {

    @Test
    fun `returns a direct Wasm result to its compiled caller slot`() {
        val type = functionType(results = resultType(listOf(i32ValueType())))
        val function = wasmFunctionInstance(
            functionType = type,
            function = runtimeFunction(
                body = runtimeExpression(entryIp = 2),
                frameSlots = 2,
            ),
        )
        val call = ControlInstruction.WasmCall(
            strategy = function.callStrategy,
            operands = OperandTransfer(emptyArray(), destinationSlotBase = 2),
            callFrameOffset = 2,
        )
        val returnInstruction = ControlInstruction.FunctionReturn(
            results = OperandTransfer(
                sources = arrayOf(TransferSource.Slot(0)),
                destinationSlotBase = 0,
            ),
            activationHeaderSlot = 1,
        )
        val program = Program().apply {
            append(CallDispatcher(call, resultDestinationSlot = 0))
            append(DispatchableInstruction { _, _, nextIp -> nextIp })
            append(FunctionReturnDispatcher(returnInstruction))
        }
        LinkWasmCallDispatchers(program, firstIp = 0)
        val store = store(program = program)
        val vstack = vstack().apply { reserveDepth(4) }
        val cstack = cstack()
        val context = executionContext(cstack, vstack, store)

        val calleeEntryIp = program.instructions[0](vstack, context, 1)
        vstack.setFrameSlot(0, 42)
        val continuationIp = program.instructions[calleeEntryIp](vstack, context, calleeEntryIp + 1)

        assertEquals(1, continuationIp)
        assertEquals(0, vstack.fp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }

    @Test
    fun `links normal and tail direct calls for every local initialization shape`() {
        val localInitialValues = listOf(
            longArrayOf(),
            longArrayOf(0),
            longArrayOf(0, 0),
            longArrayOf(0, 0, 0),
            longArrayOf(0, 0, 0, 0),
            longArrayOf(0, 0, 0, 0, 0),
            longArrayOf(0, 1),
        )
        val program = Program()
        val linkedSources = mutableListOf<ControlInstruction>()

        for ((index, locals) in localInitialValues.withIndex()) {
            val function = wasmFunctionInstance(
                function = runtimeFunction(
                    locals = locals,
                    body = runtimeExpression(entryIp = 100 + index),
                    frameSlots = locals.size + 1,
                ),
            )
            val operands = OperandTransfer(emptyArray(), destinationSlotBase = 0)
            val call = ControlInstruction.WasmCall(function.callStrategy, operands, callFrameOffset = 2)
            val tailCall = ControlInstruction.ReturnWasmCall(
                function.callStrategy,
                operands,
                callerActivationHeaderSlot = 0,
            )
            program.append(CallDispatcher(call))
            program.append(ReturnCallDispatcher(tailCall))
        }

        assertEquals(
            localInitialValues.size * 2,
            LinkWasmCallDispatchers(program, firstIp = 0) { _, source ->
                linkedSources += source as ControlInstruction
            },
        )
        assertEquals(localInitialValues.size * 2, linkedSources.size)
        assertEquals(0, LinkWasmCallDispatchers(program, firstIp = 0))
    }

    @Test
    fun `selects local initialization after the callee strategy is installed`() {
        val strategy = WasmFunctionCallStrategy(interfaceSlotCount = 0)
        val call = ControlInstruction.WasmCall(
            strategy = strategy,
            operands = OperandTransfer(emptyArray(), destinationSlotBase = 2),
            callFrameOffset = 2,
        )
        val program = Program().apply {
            append(CallDispatcher(call))
            append(DispatchableInstruction { _, _, nextIp -> nextIp })
        }

        strategy.entryIp = 1
        strategy.frameSlots = 2
        strategy.localInitialization = LocalInitialization.Zero1
        LinkWasmCallDispatchers(program, firstIp = 0)

        val store = store(program = program)
        val vstack = vstack().apply {
            reserveDepth(4)
            setFrameSlot(3, 42)
        }
        val cstack = cstack()
        val context = executionContext(cstack, vstack, store)

        assertEquals(1, program.instructions[0](vstack, context, 1))
        assertEquals(2, vstack.fp)
        assertEquals(0, vstack.getFrameSlot(1))
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
            setFrameSlot(1, ReferenceValue.Function(functionAddress()).toLong())
        }
        val cstack = cstack()
        val instruction = ControlInstruction.CallRefS(
            functionSlot = 1,
            operands = OperandTransfer(
                sources = arrayOf(TransferSource.Slot(0)),
                destinationSlotBase = 2,
            ),
            callFrameOffset = 2,
            caller = module,
        )

        val nextIp = CallDispatcher(instruction, callSiteIp = 10)(
            vstack,
            executionContext(cstack, vstack, store, module),
            11,
        )

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
            elements = longArrayOf(ReferenceValue.Function(functionAddress()).toLong()),
        )
        val vstack = vstack().apply {
            reserveDepth(2)
            setFrameSlot(0, 41)
        }
        val cstack = cstack()
        val instruction = ControlInstruction.CallIndirectI(
            elementIndex = 0,
            operands = OperandTransfer(
                sources = arrayOf(TransferSource.Slot(0)),
                destinationSlotBase = 1,
            ),
            type = runtimeType,
            table = table,
            callFrameOffset = 1,
            caller = module,
        )

        val nextIp = CallDispatcher(instruction, callSiteIp = 10)(
            vstack,
            executionContext(cstack, vstack, store, module),
            11,
        )

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
            elements = longArrayOf(ReferenceValue.Function(functionAddress()).toLong()),
        )
        val vstack = vstack().apply {
            reserveDepth(2)
            setFrameSlot(0, 41)
        }
        val cstack = cstack()
        val instruction = ControlInstruction.CallIndirectI(
            elementIndex = 0,
            operands = OperandTransfer(
                sources = arrayOf(TransferSource.Slot(0)),
                destinationSlotBase = 1,
            ),
            type = runtimeType,
            table = table,
            callFrameOffset = 1,
            caller = module,
        )

        val nextIp = CallDispatcher(instruction, callSiteIp = 10, resultDestinationSlot = 0)(
            vstack,
            executionContext(cstack, vstack, store, module),
            11,
        )

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
            elements = longArrayOf(ReferenceValue.Function(functionAddress()).toLong()),
        )
        val call = ControlInstruction.CallIndirectI(
            elementIndex = 0,
            operands = OperandTransfer(emptyArray(), destinationSlotBase = 2),
            type = runtimeType,
            table = table,
            callFrameOffset = 2,
            caller = module,
        )
        val returnInstruction = ControlInstruction.FunctionReturn(
            results = OperandTransfer(
                sources = arrayOf(TransferSource.Slot(0)),
                destinationSlotBase = 0,
            ),
            activationHeaderSlot = 1,
        )
        program.append(DispatchableInstruction { _, _, nextIp -> nextIp })
        program.append(DispatchableInstruction { _, _, nextIp -> nextIp })
        program.append(CallDispatcher(call, resultDestinationSlot = 0))
        program.append(DispatchableInstruction { _, _, nextIp -> nextIp })
        program.append(FunctionReturnDispatcher(returnInstruction))
        LinkWasmCallDispatchers(program, firstIp = 2)
        val vstack = vstack().apply { reserveDepth(4) }
        val cstack = cstack()
        val context = executionContext(cstack, vstack, store, module)

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
            setFrameSlot(1, ReferenceValue.Function(functionAddress()).toLong())
        }
        val cstack = cstack()
        val instruction = ControlInstruction.CallRefS(
            functionSlot = 1,
            operands = OperandTransfer(
                sources = arrayOf(TransferSource.Slot(0)),
                destinationSlotBase = 2,
            ),
            callFrameOffset = 2,
            caller = module,
        )

        val nextIp = CallDispatcher(instruction, callSiteIp = 10)(
            vstack,
            executionContext(cstack, vstack, store, module),
            11,
        )

        assertEquals(37, nextIp)
        assertEquals(2, vstack.fp)
        assertEquals(42L, vstack.getFrameSlot(0))
    }
}
