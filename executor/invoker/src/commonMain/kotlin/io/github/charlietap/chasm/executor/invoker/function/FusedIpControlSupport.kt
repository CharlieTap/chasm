package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.executor.invoker.thread.EXIT_IP
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.exception
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExceptionAddress
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.RTT

fun FusedDirectCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    nextIp: Int,
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> {
        HostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
            resultSlots = resultSlots,
            callFrameSlot = callFrameSlot,
        )
        nextIp
    }

    is FunctionInstance.WasmFunction -> {
        val body = functionInstance.function.body
        if (body.interpretationStyle == io.github.charlietap.chasm.runtime.execution.InterpretationStyle.INSTRUCTION_POINTER && body.fusedIpBody != null) {
            enterFusedWasmCall(
                vstack = vstack,
                cstack = cstack,
                instance = functionInstance,
                resultSlots = resultSlots,
                callFrameSlot = callFrameSlot,
                returnIp = nextIp,
            )
        } else {
            bridgeCallIntoSlots(
                vstack = vstack,
                cstack = cstack,
                store = store,
                context = context,
                instance = functionInstance,
                resultSlots = resultSlots,
                callFrameSlot = callFrameSlot,
            )
            nextIp
        }
    }
}

fun FusedIndirectCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    elementIndex: Int,
    type: RTT,
    table: TableInstance,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    nextIp: Int,
): Int = FusedDirectCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionInstance = resolveIndirectFunction(store, table, type, elementIndex),
    resultSlots = resultSlots,
    callFrameSlot = callFrameSlot,
    nextIp = nextIp,
)

fun FusedReferenceCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionSlot: Int,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    nextIp: Int,
): Int = FusedDirectCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionInstance = store.function(vstack.getFrameSlot(functionSlot).toFunctionAddress()),
    resultSlots = resultSlots,
    callFrameSlot = callFrameSlot,
    nextIp = nextIp,
)

fun FusedDirectReturnCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    operands: List<ControlSuperInstruction.CallOperand>,
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> {
        val currentFrame = cstack.peekFrame()
        val args = tailCallArguments(
            vstack = vstack,
            operandTypes = functionInstance.functionType.params.types,
            operands = operands,
        )
        val frame = unwindFusedFrame(vstack, cstack)
        val results = invokeHostFunction(
            config = context.config,
            store = store,
            callingInstance = currentFrame.instance,
            function = functionInstance,
            params = args,
        )
        writeTailCallResults(vstack, frame, results)
        frame.returnIp
    }

    is FunctionInstance.WasmFunction -> {
        val body = functionInstance.function.body
        if (body.interpretationStyle == io.github.charlietap.chasm.runtime.execution.InterpretationStyle.INSTRUCTION_POINTER && body.fusedIpBody != null) {
            replaceWithFusedTailCall(
                vstack = vstack,
                cstack = cstack,
                instance = functionInstance,
                operands = operands,
            )
        } else {
            val args = tailCallArguments(
                vstack = vstack,
                operandTypes = functionInstance.functionType.params.types,
                operands = operands,
            )
            val frame = unwindFusedFrame(vstack, cstack)
            val results = bridgeInvokeWasmFunction(
                config = context.config,
                store = store,
                instance = functionInstance,
                args = args,
            )
            writeTailCallResults(vstack, frame, results)
            frame.returnIp
        }
    }
}

fun FusedIndirectReturnCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    elementIndex: Int,
    operands: List<ControlSuperInstruction.CallOperand>,
    type: RTT,
    table: TableInstance,
): Int = FusedDirectReturnCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionInstance = resolveIndirectFunction(store, table, type, elementIndex),
    operands = operands,
)

fun FusedReferenceReturnCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionSlot: Int,
    operands: List<ControlSuperInstruction.CallOperand>,
): Int = FusedDirectReturnCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionInstance = store.function(vstack.getFrameSlot(functionSlot).toFunctionAddress()),
    operands = operands,
)

fun FusedThrow(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    tagIndex: io.github.charlietap.chasm.ir.module.Index.TagIndex,
    payloadSlots: List<Int>,
): Int {
    val frame = cstack.peekFrame()
    val address = frame.instance.tagAddress(tagIndex)
    val params = LongArray(payloadSlots.size) { index ->
        val sourceIndex = payloadSlots.lastIndex - index
        vstack.getFrameSlot(payloadSlots[sourceIndex])
    }
    val exceptionInstance = ExceptionInstance(
        tagAddress = address,
        fields = params,
    )

    store.exceptions.add(exceptionInstance)
    val exceptionAddress = io.github.charlietap.chasm.runtime.address.Address.Exception(store.exceptions.size - 1)

    return FusedThrowRefValue(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        ref = ReferenceValue.Exception(exceptionAddress).toLong(),
    )
}

fun FusedThrowRef(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    ref: Long,
): Int = FusedThrowRefValue(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    ref = ref,
)

internal fun FusedThrowRefValue(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    ref: Long,
): Int {
    val exceptionAddress = if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    } else {
        ref.toExceptionAddress()
    }

    val instance = store.exception(exceptionAddress)
    val address = instance.tagAddress

    while (true) {
        val handler = popFusedHandler(cstack, vstack)
        if (handler.instructions.isEmpty()) continue

        val frame = cstack.peekFrame()
        val catchHandler = handler.instructions.first()
        val otherHandlers = handler.instructions.drop(1)
        val payloadDestinationSlots = handler.payloadDestinationSlots.firstOrNull() ?: emptyList()
        val continuationIp = handler.continuationIps.firstOrNull()

        val tagMatches = when (catchHandler) {
            is io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler.Catch -> {
                address == frame.instance.tagAddress(catchHandler.tagIndex)
            }

            is io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler.CatchRef -> {
                address == frame.instance.tagAddress(catchHandler.tagIndex)
            }

            else -> false
        }

        when {
            catchHandler is io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler.Catch && tagMatches -> {
                instance.fields.reverse()
                if (frame.frameSlotMode) {
                    writeCatchPayloadToSlots(vstack, instance.fields, payloadDestinationSlots)
                } else {
                    vstack.push(instance.fields)
                }
                return continuationIp ?: breakToLabel(cstack, vstack, catchHandler.labelIndex)
            }

            catchHandler is io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler.CatchRef && tagMatches -> {
                instance.fields.reverse()
                if (frame.frameSlotMode) {
                    writeCatchRefPayloadToSlots(vstack, instance.fields, ref, payloadDestinationSlots)
                } else {
                    vstack.push(instance.fields)
                    vstack.push(ref)
                }
                return continuationIp ?: breakToLabel(cstack, vstack, catchHandler.labelIndex)
            }

            catchHandler is io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler.CatchAll -> {
                return continuationIp ?: breakToLabel(cstack, vstack, catchHandler.labelIndex)
            }

            catchHandler is io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler.CatchAllRef -> {
                if (frame.frameSlotMode) {
                    vstack.setFrameSlot(payloadDestinationSlots.single(), ref)
                } else {
                    vstack.push(ref)
                }
                return continuationIp ?: breakToLabel(cstack, vstack, catchHandler.labelIndex)
            }

            else -> {
                cstack.push(
                    handler.copy(
                        instructions = otherHandlers,
                        payloadDestinationSlots = handler.payloadDestinationSlots.drop(1),
                        continuations = handler.continuations.drop(1),
                        continuationIps = handler.continuationIps.drop(1),
                    ),
                )
            }
        }
    }
}

private fun enterFusedWasmCall(
    vstack: ValueStack,
    cstack: ControlStack,
    instance: FunctionInstance.WasmFunction,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    returnIp: Int,
): Int {
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val callerFramePointer = vstack.framePointer
    val valuesDepth = vstack.depth()
    val calleeFramePointer = callerFramePointer + callFrameSlot

    vstack.reserveDepth(calleeFramePointer + instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, interfaceSlots + index, value)
    }

    val frame = ActivationFrame(
        arity = results,
        depths = StackDepths(
            handlers = cstack.handlersDepth(),
            instructions = 0,
            labels = cstack.labelsDepth(),
            values = valuesDepth,
        ),
        instance = instance.module,
        previousFramePointer = callerFramePointer,
        frameSlotMode = true,
        visibleResultBase = StrictVisibleResultBase(resultSlots),
        returnIp = returnIp,
    )

    cstack.push(frame)
    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(instance.function.frameSlots)
    return instance.function.body.fusedIpBody?.arenaEntryIp ?: EXIT_IP
}

private fun replaceWithFusedTailCall(
    vstack: ValueStack,
    cstack: ControlStack,
    instance: FunctionInstance.WasmFunction,
    operands: List<ControlSuperInstruction.CallOperand>,
): Int {
    val operandValues = tailCallOperandValues(vstack, operands)
    val frame = cstack.popFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val depths = frame.depths

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels)
    vstack.shrink(0, depths.values)

    val calleeFramePointer = depths.values
    vstack.reserveDepth(calleeFramePointer + instance.function.frameSlots)
    operandValues.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, index, value)
    }
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, interfaceSlots + index, value)
    }

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(instance.function.frameSlots)
    cstack.push(
        frame.copy(
            instance = instance.module,
            frameSlotMode = true,
            visibleResultBase = FrameSlotVisibleResultBase(frame),
        ),
    )
    return instance.function.body.fusedIpBody?.arenaEntryIp ?: EXIT_IP
}

private fun bridgeCallIntoSlots(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    resultSlots: List<Int>,
    callFrameSlot: Int,
) {
    val args = callArguments(
        vstack = vstack,
        operandTypes = instance.functionType.params.types,
        callFrameSlot = callFrameSlot,
    )
    val results = bridgeInvokeWasmFunction(
        config = context.config,
        store = store,
        instance = instance,
        args = args,
    )
    resultSlots.forEachIndexed { index, slot ->
        vstack.setFrameSlot(slot, results[index])
    }
}

private fun bridgeInvokeWasmFunction(
    config: io.github.charlietap.chasm.config.RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    args: List<ExecutionValue>,
): List<Long> = InvokeWasmFunctionInstance(config, store, instance, args).fold(
    success = { results -> results },
    failure = { error -> throw InvocationException(error) },
)

private fun callArguments(
    vstack: ValueStack,
    operandTypes: List<io.github.charlietap.chasm.type.ValueType>,
    callFrameSlot: Int,
): List<ExecutionValue> {
    val framePointer = vstack.framePointer
    return operandTypes.mapIndexed { index, type ->
        vstack.getFrameSlot(framePointer + callFrameSlot, index).toExecutionValue(type)
    }
}

private fun tailCallArguments(
    vstack: ValueStack,
    operandTypes: List<io.github.charlietap.chasm.type.ValueType>,
    operands: List<ControlSuperInstruction.CallOperand>,
): List<ExecutionValue> {
    val values = tailCallOperandValues(vstack, operands)
    return operandTypes.mapIndexed { index, type ->
        values[index].toExecutionValue(type)
    }
}

private fun tailCallOperandValues(
    vstack: ValueStack,
    operands: List<ControlSuperInstruction.CallOperand>,
): LongArray {
    val currentFramePointer = vstack.framePointer
    return LongArray(operands.size) { index ->
        when (val operand = operands[index]) {
            is ControlSuperInstruction.CallOperand.Immediate -> operand.value
            is ControlSuperInstruction.CallOperand.Slot -> vstack.getFrameSlot(currentFramePointer, operand.slot)
        }
    }
}

private fun unwindFusedFrame(
    vstack: ValueStack,
    cstack: ControlStack,
): ActivationFrame {
    val frame = cstack.popFrame()
    val depths = frame.depths
    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels)
    vstack.shrink(0, depths.values)
    vstack.framePointer = frame.previousFramePointer
    return frame
}

private fun writeTailCallResults(
    vstack: ValueStack,
    frame: ActivationFrame,
    results: List<Long>,
) {
    val visibleResultBase = FrameSlotVisibleResultBase(frame)
    if (visibleResultBase != null) {
        results.forEachIndexed { index, value ->
            vstack.setFrameSlot(visibleResultBase + index, value)
        }
    } else {
        results.forEach { result ->
            vstack.push(result)
        }
    }
}

private fun resolveIndirectFunction(
    store: Store,
    table: TableInstance,
    type: RTT,
    elementIndex: Int,
): FunctionInstance {
    val address = table.element(elementIndex).toFunctionAddress()
    val functionInstance = store.function(address)
    val actualType = functionInstance.rtt
    if (actualType !== type && actualType.superTypes.none { superType -> superType === type }) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }
    return functionInstance
}

private fun invokeHostFunction(
    config: io.github.charlietap.chasm.config.RuntimeConfig,
    store: Store,
    callingInstance: io.github.charlietap.chasm.runtime.instance.ModuleInstance,
    function: FunctionInstance.HostFunction,
    params: List<ExecutionValue>,
): List<Long> {
    val functionContext = HostFunctionContext(
        config = config,
        store = store,
        instance = callingInstance,
    )
    val results = try {
        function.function.invoke(functionContext, params)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    return results.map { result ->
        result.toLongFromBoxed()
    }
}

private fun popFusedHandler(
    cstack: ControlStack,
    vstack: ValueStack,
): ExceptionHandler {
    val handler = cstack.popHandler()
    cstack.shrinkLabels(handler.labelsDepth)
    cstack.shrinkFrames(handler.framesDepth)
    cstack.shrinkInstructions(handler.instructionsDepth)
    vstack.framePointer = handler.framePointer
    return handler
}

private fun breakToLabel(
    cstack: ControlStack,
    vstack: ValueStack,
    labelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
): Int {
    val breakLabel = cstack.peekNthLabel(labelIndex.idx)
    val frame = cstack.peekFrame()
    val depths = breakLabel.depths

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels)

    if (!frame.frameSlotMode) {
        vstack.shrink(breakLabel.arity, depths.values)
    }

    return when {
        breakLabel.continuation != null -> error("fused IP labels cannot resume through stack continuations")
        breakLabel.loopIp >= 0 -> breakLabel.loopIp
        breakLabel.branchIp >= 0 -> breakLabel.branchIp
        else -> throw InvocationException(InvocationError.ProgramFinishedInconsistentState)
    }
}

private fun writeCatchPayloadToSlots(
    vstack: ValueStack,
    fields: LongArray,
    payloadDestinationSlots: List<Int>,
) {
    fields.forEachIndexed { index, value ->
        vstack.setFrameSlot(payloadDestinationSlots[index], value)
    }
}

private fun writeCatchRefPayloadToSlots(
    vstack: ValueStack,
    fields: LongArray,
    ref: Long,
    payloadDestinationSlots: List<Int>,
) {
    writeCatchPayloadToSlots(vstack, fields, payloadDestinationSlots)
    vstack.setFrameSlot(payloadDestinationSlots[fields.size], ref)
}
