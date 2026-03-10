package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.function.FinishStrictFrameSlotCallResult
import io.github.charlietap.chasm.executor.invoker.function.FusedDirectCall
import io.github.charlietap.chasm.executor.invoker.function.FusedDirectReturnCall
import io.github.charlietap.chasm.executor.invoker.function.FusedIndirectCall
import io.github.charlietap.chasm.executor.invoker.function.FusedIndirectReturnCall
import io.github.charlietap.chasm.executor.invoker.function.FusedReferenceCall
import io.github.charlietap.chasm.executor.invoker.function.FusedReferenceReturnCall
import io.github.charlietap.chasm.executor.invoker.function.FusedThrow
import io.github.charlietap.chasm.executor.invoker.function.FusedThrowRef
import io.github.charlietap.chasm.executor.invoker.function.RestoreLegacyCallResult
import io.github.charlietap.chasm.executor.invoker.thread.EXIT_IP
import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.dispatch.FusedIpDispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.function.FusedIpBody

internal fun FusedIpBodyPredecoder(
    context: PredecodingContext,
    instructions: List<Instruction>,
): Result<FusedIpBody?, ModuleTrapError> = binding {
    if (!supportsFusedIp(instructions)) {
        null
    } else {
        val codeArenaOffset = context.store.codeArenaArray.size
        val exitIp = codeArenaOffset + instructions.size
        val dispatchables = instructions.mapIndexed { index, instruction ->
            predecodeFusedIpInstruction(
                context = context,
                instruction = instruction,
                baseIp = codeArenaOffset,
                index = index,
                instructionCount = instructions.size,
            ).bind()
        }

        context.store.appendCodeArena(dispatchables)

        FusedIpBody(
            arenaEntryIp = if (instructions.isEmpty()) EXIT_IP else codeArenaOffset,
            arenaExitIp = exitIp,
        )
    }
}

private fun predecodeFusedIpInstruction(
    context: PredecodingContext,
    instruction: Instruction,
    baseIp: Int,
    index: Int,
    instructionCount: Int,
): Result<FusedIpDispatchableInstruction, ModuleTrapError> = binding {
    val nextIp = fallthroughIp(baseIp, index, instructionCount)

    when (instruction) {
        is AdminInstruction -> predecodeAdminInstruction(context, instruction, baseIp, nextIp, instructionCount).bind()
        is ControlSuperInstruction -> predecodeControlSuperInstruction(context, instruction, nextIp).bind()
        is io.github.charlietap.chasm.ir.instruction.ControlInstruction -> unsupportedControlInstruction()
        else -> sequentialDispatchable(context, instruction, nextIp).bind()
    }
}

private fun predecodeAdminInstruction(
    context: PredecodingContext,
    instruction: AdminInstruction,
    baseIp: Int,
    nextIp: Int,
    instructionCount: Int,
): Result<FusedIpDispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AdminInstruction.EndBlock -> FusedIpDispatchableInstruction { _, _, _, _ ->
            nextIp
        }

        is AdminInstruction.EndFunction -> FusedIpDispatchableInstruction { vstack, cstack, _, _ ->
            val frame = cstack.popFrame()
            val depths = frame.depths

            cstack.shrinkHandlers(depths.handlers)
            cstack.shrinkInstructions(depths.instructions)
            cstack.shrinkLabels(depths.labels)

            if (FinishStrictFrameSlotCallResult(vstack, frame)) {
                frame.returnIp
            } else {
                RestoreLegacyCallResult(vstack, frame)
                vstack.framePointer = frame.previousFramePointer
                frame.returnIp
            }
        }

        is AdminInstruction.CopySlots -> sequentialDispatchable(context, instruction, nextIp).bind()

        is AdminInstruction.Jump -> {
            val targetIp = targetIpForOffset(baseIp, instructionCount, instruction.offset)
            FusedIpDispatchableInstruction { _, _, _, _ ->
                targetIp
            }
        }

        is AdminInstruction.JumpIf -> {
            val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
            val operandImmediate = jumpImmediate(instruction.operand)
            val operandSlot = jumpOperandSlot(instruction.operand)
            val targetIp = targetIpForOffset(baseIp, instructionCount, instruction.offset)

            when {
                operandImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    if (operandImmediate != 0L) {
                        executeInlineInstructions(takenInstructions, vstack, cstack, store, executionContext)
                        targetIp
                    } else {
                        nextIp
                    }
                }

                operandSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    if (vstack.getFrameSlot(operandSlot) != 0L) {
                        executeInlineInstructions(takenInstructions, vstack, cstack, store, executionContext)
                        targetIp
                    } else {
                        nextIp
                    }
                }

                else -> unsupportedJumpInstruction()
            }
        }

        is AdminInstruction.JumpTable -> {
            val takenInstructions = instruction.takenInstructions.map { instructions ->
                InstructionSequencePredecoderList(context, instructions).bind()
            }
            val defaultTakenInstructions = InstructionSequencePredecoderList(context, instruction.defaultTakenInstructions).bind()
            val operandImmediate = jumpIndexImmediate(instruction.operand)
            val operandSlot = jumpOperandSlot(instruction.operand)
            val continuations = instruction.offsets.map { offset ->
                targetIpForOffset(baseIp, instructionCount, offset)
            }
            val defaultContinuation = targetIpForOffset(baseIp, instructionCount, instruction.defaultOffset)

            when {
                operandImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeJumpTable(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        operand = operandImmediate,
                        continuations = continuations,
                        defaultContinuation = defaultContinuation,
                        takenInstructions = takenInstructions,
                        defaultTakenInstructions = defaultTakenInstructions,
                        nextIp = nextIp,
                    )
                }

                operandSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeJumpTable(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        operand = vstack.getFrameSlot(operandSlot).toInt(),
                        continuations = continuations,
                        defaultContinuation = defaultContinuation,
                        takenInstructions = takenInstructions,
                        defaultTakenInstructions = defaultTakenInstructions,
                        nextIp = nextIp,
                    )
                }

                else -> unsupportedJumpInstruction()
            }
        }

        is AdminInstruction.JumpOnNull -> {
            val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
            val operandImmediate = jumpImmediate(instruction.operand)
            val operandSlot = jumpOperandSlot(instruction.operand)
            val targetIp = targetIpForOffset(baseIp, instructionCount, instruction.offset)

            when {
                operandImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeConditionalJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        condition = operandImmediate.isNullableReference(),
                        takenInstructions = takenInstructions,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                operandSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeConditionalJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        condition = vstack.getFrameSlot(operandSlot).isNullableReference(),
                        takenInstructions = takenInstructions,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                else -> unsupportedJumpInstruction()
            }
        }

        is AdminInstruction.JumpOnNonNull -> {
            val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
            val operandImmediate = jumpImmediate(instruction.operand)
            val operandSlot = jumpOperandSlot(instruction.operand)
            val targetIp = targetIpForOffset(baseIp, instructionCount, instruction.offset)

            when {
                operandImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeConditionalJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        condition = !operandImmediate.isNullableReference(),
                        takenInstructions = takenInstructions,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                operandSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeConditionalJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        condition = !vstack.getFrameSlot(operandSlot).isNullableReference(),
                        takenInstructions = takenInstructions,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                else -> unsupportedJumpInstruction()
            }
        }

        is AdminInstruction.JumpOnCast -> {
            val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
            val operandImmediate = jumpImmediate(instruction.operand)
            val operandSlot = jumpOperandSlot(instruction.operand)
            val targetIp = targetIpForOffset(baseIp, instructionCount, instruction.offset)

            when {
                operandImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeCastJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        operand = operandImmediate,
                        dstReferenceType = instruction.dstReferenceType,
                        takenInstructions = takenInstructions,
                        jumpIfMatches = true,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                operandSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeCastJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        operand = vstack.getFrameSlot(operandSlot),
                        dstReferenceType = instruction.dstReferenceType,
                        takenInstructions = takenInstructions,
                        jumpIfMatches = true,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                else -> unsupportedJumpInstruction()
            }
        }

        is AdminInstruction.JumpOnCastFail -> {
            val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
            val operandImmediate = jumpImmediate(instruction.operand)
            val operandSlot = jumpOperandSlot(instruction.operand)
            val targetIp = targetIpForOffset(baseIp, instructionCount, instruction.offset)

            when {
                operandImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeCastJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        operand = operandImmediate,
                        dstReferenceType = instruction.dstReferenceType,
                        takenInstructions = takenInstructions,
                        jumpIfMatches = false,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                operandSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    executeCastJump(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        executionContext = executionContext,
                        operand = vstack.getFrameSlot(operandSlot),
                        dstReferenceType = instruction.dstReferenceType,
                        takenInstructions = takenInstructions,
                        jumpIfMatches = false,
                        takenIp = targetIp,
                        fallthroughIp = nextIp,
                    )
                }

                else -> unsupportedJumpInstruction()
            }
        }

        is AdminInstruction.PushHandler -> {
            val continuationIps = instruction.offsets.map { offset ->
                targetIpForOffset(baseIp, instructionCount, offset)
            }
            FusedIpDispatchableInstruction { vstack, cstack, _, _ ->
                cstack.push(
                    ExceptionHandler(
                        instructions = instruction.handlers,
                        payloadDestinationSlots = instruction.payloadDestinationSlots,
                        continuations = emptyList(),
                        framesDepth = cstack.framesDepth(),
                        instructionsDepth = 0,
                        labelsDepth = cstack.labelsDepth(),
                        framePointer = vstack.framePointer,
                        handlerIp = continuationIps.firstOrNull() ?: EXIT_IP,
                        continuationIps = continuationIps,
                    ),
                )
                nextIp
            }
        }

        is AdminInstruction.PopHandler -> FusedIpDispatchableInstruction { _, cstack, _, _ ->
            cstack.popHandler()
            nextIp
        }

        is AdminInstruction.Pause -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
            if (cstack.framesDepth() == 1) {
                io.github.charlietap.chasm.executor.invoker.GarbageCollector(store, vstack)
            }
            nextIp
        }

        is AdminInstruction.PauseIf -> FusedIpDispatchableInstruction { vstack, _, store, executionContext ->
            if (store.heap.sizeInBytes >= executionContext.config.gcThreshold.bytes) {
                io.github.charlietap.chasm.executor.invoker.GarbageCollector(store, vstack)
            }
            nextIp
        }
    }
}

private fun predecodeControlSuperInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction,
    nextIp: Int,
): Result<FusedIpDispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ControlSuperInstruction.Call -> {
            val address = context.instance.functionAddress(instruction.functionIndex).bind()
            val instance = context.store.function(address)
            FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                FusedDirectCall(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    context = executionContext,
                    functionInstance = instance,
                    resultSlots = instruction.resultSlots,
                    callFrameSlot = instruction.callFrameSlot,
                    nextIp = nextIp,
                )
            }
        }

        is ControlSuperInstruction.ReturnCall -> {
            val address = context.instance.functionAddress(instruction.functionIndex).bind()
            val instance = context.store.function(address)
            val operands = strictCallOperands(instruction.operands)
            FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                FusedDirectReturnCall(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    context = executionContext,
                    functionInstance = instance,
                    operands = operands,
                )
            }
        }

        is ControlSuperInstruction.CallIndirect -> {
            val address = context.instance.tableAddress(instruction.tableIndex).bind()
            val table = context.store.table(address)
            val type = context.instance.runtimeTypes[instruction.typeIndex.idx]
            val elementImmediate = strictControlIndexImmediate(instruction.elementIndex)
            val elementSlot = strictControlOperandSlot(instruction.elementIndex)

            when {
                elementImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    FusedIndirectCall(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        context = executionContext,
                        elementIndex = elementImmediate,
                        type = type,
                        table = table,
                        resultSlots = instruction.resultSlots,
                        callFrameSlot = instruction.callFrameSlot,
                        nextIp = nextIp,
                    )
                }

                elementSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    FusedIndirectCall(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        context = executionContext,
                        elementIndex = vstack.getFrameSlot(elementSlot).toInt(),
                        type = type,
                        table = table,
                        resultSlots = instruction.resultSlots,
                        callFrameSlot = instruction.callFrameSlot,
                        nextIp = nextIp,
                    )
                }

                else -> unsupportedControlInstruction()
            }
        }

        is ControlSuperInstruction.ReturnCallIndirect -> {
            val address = context.instance.tableAddress(instruction.tableIndex).bind()
            val table = context.store.table(address)
            val type = context.instance.runtimeTypes[instruction.typeIndex.idx]
            val operands = strictCallOperands(instruction.operands)
            val elementImmediate = strictControlIndexImmediate(instruction.elementIndex)
            val elementSlot = strictControlOperandSlot(instruction.elementIndex)

            when {
                elementImmediate != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    FusedIndirectReturnCall(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        context = executionContext,
                        elementIndex = elementImmediate,
                        operands = operands,
                        type = type,
                        table = table,
                    )
                }

                elementSlot != null -> FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                    FusedIndirectReturnCall(
                        vstack = vstack,
                        cstack = cstack,
                        store = store,
                        context = executionContext,
                        elementIndex = vstack.getFrameSlot(elementSlot).toInt(),
                        operands = operands,
                        type = type,
                        table = table,
                    )
                }

                else -> unsupportedControlInstruction()
            }
        }

        is ControlSuperInstruction.CallRef -> {
            val functionSlot = strictControlOperandSlot(instruction.functionReference)
                ?: unsupportedControlInstruction()
            FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                FusedReferenceCall(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    context = executionContext,
                    functionSlot = functionSlot,
                    resultSlots = instruction.resultSlots,
                    callFrameSlot = instruction.callFrameSlot,
                    nextIp = nextIp,
                )
            }
        }

        is ControlSuperInstruction.ReturnCallRef -> {
            val functionSlot = strictControlOperandSlot(instruction.functionReference)
                ?: unsupportedControlInstruction()
            val operands = strictCallOperands(instruction.operands)
            FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                FusedReferenceReturnCall(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    context = executionContext,
                    functionSlot = functionSlot,
                    operands = operands,
                )
            }
        }

        is ControlSuperInstruction.Throw -> {
            val payloadSlots = strictControlOperandSlots(instruction.payloads, "throw")
            FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                FusedThrow(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    context = executionContext,
                    tagIndex = instruction.tagIndex,
                    payloadSlots = payloadSlots,
                )
            }
        }

        is ControlSuperInstruction.ThrowRef -> {
            val exceptionSlot = strictControlOperandSlot(instruction.exceptionReference)
                ?: unsupportedControlInstruction()
            FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
                FusedThrowRef(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    context = executionContext,
                    ref = vstack.getFrameSlot(exceptionSlot),
                )
            }
        }

        else -> unsupportedControlInstruction()
    }
}

private fun sequentialDispatchable(
    context: PredecodingContext,
    instruction: Instruction,
    nextIp: Int,
): Result<FusedIpDispatchableInstruction, ModuleTrapError> = binding {
    val dispatchable = InstructionPredecoder(context, instruction).bind()
    FusedIpDispatchableInstruction { vstack, cstack, store, executionContext ->
        dispatchable(vstack, cstack, store, executionContext)
        nextIp
    }
}

private fun executeInlineInstructions(
    instructions: List<DispatchableInstruction>,
    vstack: io.github.charlietap.chasm.runtime.stack.ValueStack,
    cstack: io.github.charlietap.chasm.runtime.stack.ControlStack,
    store: io.github.charlietap.chasm.runtime.store.Store,
    context: io.github.charlietap.chasm.runtime.execution.ExecutionContext,
) {
    instructions.forEach { instruction ->
        instruction(vstack, cstack, store, context)
    }
}

private fun executeConditionalJump(
    vstack: io.github.charlietap.chasm.runtime.stack.ValueStack,
    cstack: io.github.charlietap.chasm.runtime.stack.ControlStack,
    store: io.github.charlietap.chasm.runtime.store.Store,
    executionContext: io.github.charlietap.chasm.runtime.execution.ExecutionContext,
    condition: Boolean,
    takenInstructions: List<DispatchableInstruction>,
    takenIp: Int,
    fallthroughIp: Int,
): Int {
    if (condition) {
        executeInlineInstructions(takenInstructions, vstack, cstack, store, executionContext)
        return takenIp
    }
    return fallthroughIp
}

private fun executeJumpTable(
    vstack: io.github.charlietap.chasm.runtime.stack.ValueStack,
    cstack: io.github.charlietap.chasm.runtime.stack.ControlStack,
    store: io.github.charlietap.chasm.runtime.store.Store,
    executionContext: io.github.charlietap.chasm.runtime.execution.ExecutionContext,
    operand: Int,
    continuations: List<Int>,
    defaultContinuation: Int,
    takenInstructions: List<List<DispatchableInstruction>>,
    defaultTakenInstructions: List<DispatchableInstruction>,
    nextIp: Int,
): Int {
    val targetIndex = if (operand >= 0 && operand < continuations.size) operand else -1
    val continuation = if (targetIndex >= 0) continuations[targetIndex] else defaultContinuation
    val selectedInstructions = if (targetIndex >= 0) takenInstructions[targetIndex] else defaultTakenInstructions

    executeInlineInstructions(selectedInstructions, vstack, cstack, store, executionContext)
    return continuation
}

private fun executeCastJump(
    vstack: io.github.charlietap.chasm.runtime.stack.ValueStack,
    cstack: io.github.charlietap.chasm.runtime.stack.ControlStack,
    store: io.github.charlietap.chasm.runtime.store.Store,
    executionContext: io.github.charlietap.chasm.runtime.execution.ExecutionContext,
    operand: Long,
    dstReferenceType: io.github.charlietap.chasm.type.ReferenceType,
    takenInstructions: List<DispatchableInstruction>,
    jumpIfMatches: Boolean,
    takenIp: Int,
    fallthroughIp: Int,
): Int {
    val moduleInstance = cstack.peekFrame().instance
    val casted = Caster(operand, dstReferenceType, moduleInstance, store)
    return executeConditionalJump(
        vstack = vstack,
        cstack = cstack,
        store = store,
        executionContext = executionContext,
        condition = casted == jumpIfMatches,
        takenInstructions = takenInstructions,
        takenIp = takenIp,
        fallthroughIp = fallthroughIp,
    )
}

private fun supportsFusedIp(
    instructions: List<Instruction>,
): Boolean = instructions.all(::supportsFusedIpInstruction)

private fun supportsFusedIpInstruction(
    instruction: Instruction,
): Boolean = when (instruction) {
    is AdminInstruction.EndBlock,
    is AdminInstruction.EndFunction,
    is AdminInstruction.CopySlots,
    is AdminInstruction.Jump,
    is AdminInstruction.PushHandler,
    is AdminInstruction.PopHandler,
    is AdminInstruction.Pause,
    is AdminInstruction.PauseIf,
    -> true

    is AdminInstruction.JumpIf -> instruction.takenInstructions.all(::supportsInlineInstruction)
    is AdminInstruction.JumpTable ->
        instruction.takenInstructions.all { group -> group.all(::supportsInlineInstruction) } &&
            instruction.defaultTakenInstructions.all(::supportsInlineInstruction)
    is AdminInstruction.JumpOnNull -> instruction.takenInstructions.all(::supportsInlineInstruction)
    is AdminInstruction.JumpOnNonNull -> instruction.takenInstructions.all(::supportsInlineInstruction)
    is AdminInstruction.JumpOnCast -> instruction.takenInstructions.all(::supportsInlineInstruction)
    is AdminInstruction.JumpOnCastFail -> instruction.takenInstructions.all(::supportsInlineInstruction)

    is ControlSuperInstruction.Call,
    is ControlSuperInstruction.ReturnCall,
    is ControlSuperInstruction.CallIndirect,
    is ControlSuperInstruction.ReturnCallIndirect,
    is ControlSuperInstruction.CallRef,
    is ControlSuperInstruction.ReturnCallRef,
    is ControlSuperInstruction.Throw,
    is ControlSuperInstruction.ThrowRef,
    -> true

    is ControlSuperInstruction.BrIf,
    is ControlSuperInstruction.BrTable,
    is ControlSuperInstruction.BrOnNull,
    is ControlSuperInstruction.BrOnNonNull,
    is ControlSuperInstruction.BrOnCast,
    is ControlSuperInstruction.BrOnCastFail,
    is ControlSuperInstruction.If,
    is io.github.charlietap.chasm.ir.instruction.ControlInstruction,
    -> false

    else -> true
}

private fun supportsInlineInstruction(
    instruction: Instruction,
): Boolean = when (instruction) {
    is io.github.charlietap.chasm.ir.instruction.ControlInstruction -> false
    is ControlSuperInstruction -> false
    else -> true
}

private fun fallthroughIp(
    baseIp: Int,
    index: Int,
    instructionCount: Int,
): Int = if (index == instructionCount - 1) {
    EXIT_IP
} else {
    baseIp + index + 1
}

private fun targetIpForOffset(
    baseIp: Int,
    instructionCount: Int,
    offset: Int,
): Int {
    require(offset in 0..instructionCount) {
        "jump target offset $offset is outside instruction sequence of size $instructionCount"
    }
    return if (offset == instructionCount) {
        EXIT_IP
    } else {
        baseIp + offset
    }
}

private fun jumpImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun jumpIndexImmediate(
    operand: FusedOperand,
): Int? = jumpImmediate(operand)?.toInt()

private fun jumpOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictControlImmediate(
    operand: FusedOperand,
): Long? = jumpImmediate(operand)

private fun strictControlIndexImmediate(
    operand: FusedOperand,
): Int? = strictControlImmediate(operand)?.toInt()

private fun strictControlOperandSlot(
    operand: FusedOperand,
): Int? = jumpOperandSlot(operand)

private fun strictCallOperands(
    operands: List<FusedOperand>,
): List<io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction.CallOperand> = operands.map { operand ->
    val immediate = strictControlImmediate(operand)
    if (immediate != null) {
        io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction.CallOperand.Immediate(immediate)
    } else {
        val slot = strictControlOperandSlot(operand) ?: error(
            "control fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode",
        )
        io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction.CallOperand.Slot(slot)
    }
}

private fun strictControlOperandSlots(
    operands: List<FusedOperand>,
    instructionName: String,
): List<Int> = operands.map { operand ->
    strictControlOperandSlot(operand)
        ?: error("$instructionName operands must lower to frame slots before predecode: $operand")
}

private fun unsupportedControlInstruction(): Nothing =
    error("control fused instruction must be frame-slot lowered to fused IP supported shapes")

private fun unsupportedJumpInstruction(): Nothing =
    error("jump instruction must be frame-slot lowered to immediate/frame-slot shapes before fused IP predecode")
