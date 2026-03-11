@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.controlfused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.ir.instruction.emptyBlockType
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ControlSuperInstructionPredecoderTest {

    @Test
    fun `predecodes br_if with an immediate condition without using caches`() {
        val context = predecodingContext()
        val instruction = ControlSuperInstruction.BrIf(
            operand = FusedOperand.I32Const(0),
            labelIndex = labelIndex(0),
            takenInstructions = listOf(
                AdminInstruction.CopySlots(
                    sourceSlots = listOf(0),
                    destinationSlots = listOf(1),
                ),
            ),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 41L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(0L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes br_table from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ControlSuperInstruction.BrTable(
            operand = FusedOperand.FrameSlot(0),
            labelIndices = listOf(labelIndex(0)),
            defaultLabelIndex = labelIndex(0),
            takenInstructions = listOf(
                listOf(
                    AdminInstruction.CopySlots(
                        sourceSlots = listOf(3),
                        destinationSlots = listOf(1),
                    ),
                ),
            ),
            defaultTakenInstructions = listOf(
                AdminInstruction.CopySlots(
                    sourceSlots = listOf(3),
                    destinationSlots = listOf(2),
                ),
            ),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(4)
            setFrameSlot(0, 0L)
            setFrameSlot(3, 41L)
        }
        val controlStack = cstack(
            frames = listOf(frame(frameSlotMode = true)),
            labels = listOf(label(arity = 1, depths = stackDepths(values = 0))),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(41L, vstack.getFrameSlot(1))
        assertEquals(0, controlStack.labelsDepth())
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes br_on_null from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ControlSuperInstruction.BrOnNull(
            operand = FusedOperand.FrameSlot(0),
            labelIndex = labelIndex(0),
            takenInstructions = listOf(
                AdminInstruction.CopySlots(
                    sourceSlots = listOf(1),
                    destinationSlots = listOf(2),
                ),
            ),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(0, functionReferenceValue().toLong())
            setFrameSlot(1, 41L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(0L, vstack.getFrameSlot(2))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes if from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ControlSuperInstruction.If(
            operand = FusedOperand.FrameSlot(0),
            blockType = emptyBlockType(),
            thenInstructions = emptyList(),
            elseInstructions = emptyList(),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
            setFrameSlot(0, 1L)
        }
        val controlStack = cstack()

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(1, controlStack.labelsDepth())
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes host calls into direct slot writes without using caches`() {
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    hostFunctionInstance(
                        functionType = functionType,
                        function = { listOf(i32(77)) },
                    ),
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.Call(
            operands = emptyList(),
            functionIndex = functionIndex(0),
            resultSlots = listOf(1),
            callFrameSlot = 1,
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 41L)
            setFrameSlot(1, 41L)
        }
        val controlStack = cstack(
            frames = listOf(frame(instance = context.instance, frameSlotMode = true)),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(41L, vstack.getFrameSlot(0))
        assertEquals(77L, vstack.getFrameSlot(1))
        assertEquals(2, vstack.depth())
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes wasm calls into direct frame slot setup without using caches`() {
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = runtimeFunction(
                frameSlots = 3,
                frameSlotMode = true,
                returnSlots = listOf(0),
                body = runtimeExpression(
                    arrayOf(
                        dispatchableInstruction(),
                    ),
                ),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    functionInstance,
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.Call(
            operands = emptyList(),
            functionIndex = functionIndex(0),
            resultSlots = listOf(2),
            callFrameSlot = 2,
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(0, 41L)
            setFrameSlot(1, 99L)
            setFrameSlot(2, 41L)
        }
        val controlStack = cstack(
            frames = listOf(frame(instance = context.instance, frameSlotMode = true)),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(2, vstack.framePointer)
        assertEquals(5, vstack.depth())
        assertEquals(41L, vstack.getFrameSlot(2, 0))
        assertEquals(1, controlStack.labelsDepth())
        assertEquals(2, controlStack.peekFrame().visibleResultBase)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes call_indirect into direct host slot writes without using caches`() {
        val expectedType = rtt()
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                runtimeTypes = listOf(expectedType),
                functionAddresses = mutableListOf(functionAddress(0)),
                tableAddresses = mutableListOf(tableAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    hostFunctionInstance(
                        rtt = expectedType,
                        functionType = functionType,
                        function = { listOf(i32(77)) },
                    ),
                ),
                tables = mutableListOf(
                    tableInstance(
                        elements = longArrayOf(functionReferenceValue(functionAddress(0)).toLong()),
                    ),
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.CallIndirect(
            elementIndex = FusedOperand.I32Const(0),
            operands = emptyList(),
            typeIndex = io.github.charlietap.chasm.ir.module.Index.TypeIndex(0),
            tableIndex = io.github.charlietap.chasm.ir.module.Index.TableIndex(0),
            resultSlots = listOf(1),
            callFrameSlot = 1,
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 41L)
            setFrameSlot(1, 41L)
        }
        val controlStack = cstack(
            frames = listOf(frame(instance = context.instance, frameSlotMode = true)),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(41L, vstack.getFrameSlot(0))
        assertEquals(77L, vstack.getFrameSlot(1))
        assertEquals(2, vstack.depth())
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes call_ref into direct wasm frame slot setup without using caches`() {
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = runtimeFunction(
                frameSlots = 3,
                frameSlotMode = true,
                returnSlots = listOf(0),
                body = runtimeExpression(
                    arrayOf(
                        dispatchableInstruction(),
                    ),
                ),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    functionInstance,
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.CallRef(
            functionReference = FusedOperand.FrameSlot(1),
            operands = emptyList(),
            typeIndex = io.github.charlietap.chasm.ir.module.Index.TypeIndex(0),
            resultSlots = listOf(2),
            callFrameSlot = 2,
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(1, functionReferenceValue(functionAddress(0)).toLong())
            setFrameSlot(2, 41L)
        }
        val controlStack = cstack(
            frames = listOf(frame(instance = context.instance, frameSlotMode = true)),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(2, vstack.framePointer)
        assertEquals(5, vstack.depth())
        assertEquals(41L, vstack.getFrameSlot(2, 0))
        assertEquals(2, controlStack.peekFrame().visibleResultBase)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes return host calls from frame slots without using caches`() {
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    hostFunctionInstance(
                        functionType = functionType,
                        function = { listOf(i32(77)) },
                    ),
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.ReturnCall(
            operands = listOf(FusedOperand.FrameSlot(0)),
            functionIndex = functionIndex(0),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            framePointer = 2
            reserveFrame(1)
            setFrameSlot(0, 41L)
        }
        val controlStack = cstack(
            frames = listOf(
                frame(
                    instance = context.instance,
                    previousFramePointer = 0,
                    frameSlotMode = true,
                    visibleResultBase = 1,
                    depths = stackDepths(values = 2),
                ),
            ),
            labels = listOf(label()),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(0, controlStack.framesDepth())
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(77L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes return_call_indirect from frame slots without using caches`() {
        val expectedType = rtt()
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                runtimeTypes = listOf(expectedType),
                functionAddresses = mutableListOf(functionAddress(0)),
                tableAddresses = mutableListOf(tableAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    hostFunctionInstance(
                        rtt = expectedType,
                        functionType = functionType,
                        function = { listOf(i32(77)) },
                    ),
                ),
                tables = mutableListOf(
                    tableInstance(
                        elements = longArrayOf(functionReferenceValue(functionAddress(0)).toLong()),
                    ),
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.ReturnCallIndirect(
            elementIndex = FusedOperand.FrameSlot(1),
            operands = listOf(FusedOperand.FrameSlot(0)),
            typeIndex = io.github.charlietap.chasm.ir.module.Index.TypeIndex(0),
            tableIndex = io.github.charlietap.chasm.ir.module.Index.TableIndex(0),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            framePointer = 2
            reserveFrame(2)
            setFrameSlot(0, 41L)
            setFrameSlot(1, 0L)
        }
        val controlStack = cstack(
            frames = listOf(
                frame(
                    instance = context.instance,
                    previousFramePointer = 0,
                    frameSlotMode = true,
                    visibleResultBase = 1,
                    depths = stackDepths(values = 2),
                ),
            ),
            labels = listOf(label()),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(0, controlStack.framesDepth())
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(77L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes return_call_ref into direct wasm frame slot setup without using caches`() {
        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = runtimeFunction(
                frameSlots = 3,
                frameSlotMode = true,
                returnSlots = listOf(0),
                body = runtimeExpression(
                    arrayOf(
                        dispatchableInstruction(),
                    ),
                ),
            ),
        )
        val context = PredecodingContext(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress(0)),
            ),
            store = store(
                functions = mutableListOf(
                    functionInstance,
                ),
            ),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ControlSuperInstruction.ReturnCallRef(
            functionReference = FusedOperand.FrameSlot(1),
            operands = listOf(FusedOperand.FrameSlot(0)),
            typeIndex = io.github.charlietap.chasm.ir.module.Index.TypeIndex(0),
        )

        val dispatchable = ControlSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            framePointer = 2
            reserveFrame(2)
            setFrameSlot(0, 41L)
            setFrameSlot(1, functionReferenceValue(functionAddress(0)).toLong())
        }
        val controlStack = cstack(
            frames = listOf(
                frame(
                    instance = context.instance,
                    previousFramePointer = 0,
                    frameSlotMode = true,
                    visibleResultBase = 1,
                    depths = stackDepths(values = 2),
                ),
            ),
            labels = listOf(label()),
        )

        execute(dispatchable, context, vstack, controlStack)

        assertEquals(1, controlStack.framesDepth())
        assertEquals(1, controlStack.labelsDepth())
        assertEquals(2, vstack.framePointer)
        assertEquals(5, vstack.depth())
        assertEquals(41L, vstack.getFrameSlot(2, 0))
        assertEquals(1, controlStack.peekFrame().visibleResultBase)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(): PredecodingContext = PredecodingContext(
        instance = moduleInstance(),
        store = store(),
        instructionCache = hashMapOf(),
        types = mutableListOf(),
    )

    private fun execute(
        dispatchable: DispatchableInstruction,
        context: PredecodingContext,
        vstack: ValueStack,
        cstack: ControlStack = cstack(),
    ) {
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        dispatchable(vstack, cstack, context.store, executionContext)
    }
}
