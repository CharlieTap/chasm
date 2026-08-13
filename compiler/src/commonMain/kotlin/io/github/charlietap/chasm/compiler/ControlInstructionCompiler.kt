package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.BlockContext
import io.github.charlietap.chasm.compiler.context.BlockKind
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.context.function
import io.github.charlietap.chasm.compiler.context.rtt
import io.github.charlietap.chasm.compiler.context.table
import io.github.charlietap.chasm.compiler.context.tag
import io.github.charlietap.chasm.compiler.emptyIntArray
import io.github.charlietap.chasm.compiler.instruction.BranchOutcome
import io.github.charlietap.chasm.compiler.instruction.callFrameSlot
import io.github.charlietap.chasm.compiler.instruction.emitBranchIf
import io.github.charlietap.chasm.compiler.instruction.emitBranchOnCast
import io.github.charlietap.chasm.compiler.instruction.emitBranchOnNull
import io.github.charlietap.chasm.compiler.instruction.emitBranchTable
import io.github.charlietap.chasm.compiler.instruction.emitCall
import io.github.charlietap.chasm.compiler.instruction.emitCallIndirect
import io.github.charlietap.chasm.compiler.instruction.emitCallRef
import io.github.charlietap.chasm.compiler.instruction.emitCopies
import io.github.charlietap.chasm.compiler.instruction.emitFunctionReturn
import io.github.charlietap.chasm.compiler.instruction.emitJump
import io.github.charlietap.chasm.compiler.instruction.emitPopHandler
import io.github.charlietap.chasm.compiler.instruction.emitPushHandler
import io.github.charlietap.chasm.compiler.instruction.emitReturnCall
import io.github.charlietap.chasm.compiler.instruction.emitReturnCallIndirect
import io.github.charlietap.chasm.compiler.instruction.emitReturnCallRef
import io.github.charlietap.chasm.compiler.instruction.emitThrow
import io.github.charlietap.chasm.compiler.instruction.emitThrowRef
import io.github.charlietap.chasm.compiler.instruction.emptySlotCopyPlan
import io.github.charlietap.chasm.compiler.instruction.planCopies
import io.github.charlietap.chasm.compiler.instruction.prepareBranchTarget
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.compiler.program.ProgramTarget
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.UnreachableDispatcher
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction as RuntimeControlInstruction

internal fun beginFunctionControl(state: FunctionCompilationContext) {
    val target = state.program.target()
    val root = BlockContext(
        kind = BlockKind.Function,
        baseHeight = 0,
        branchSlots = state.layout.returnSlots,
        resultSlots = state.layout.returnSlots,
        parameterSlots = emptyIntArray,
        branchTypes = state.layout.resultTypes,
        resultTypes = state.layout.resultTypes,
        parameterTypes = emptyList(),
        branchTarget = target,
        continuationTarget = target,
        handlerDepth = 0,
    )
    state.rootControl = root
}

internal fun finishFunctionControl(state: FunctionCompilationContext) {
    check(state.controls.isEmpty()) {
        "function ended with unclosed controls: ${state.controls}"
    }
    val root = state.rootControl
        ?: error("function control root is unavailable")
    check(root.kind == BlockKind.Function)
    state.rootControl = null

    if (state.reachable) {
        check(state.operands.size == root.resultSlots.size) {
            "function result stack does not match its type: stack=${state.operands} results=${root.resultTypes}"
        }
        val copies = state.planCopies(root.resultSlots)
        if (root.reachedByBranch) {
            if (copies.isIdentity()) {
                state.bind(root.continuationTarget)
                state.emit(AdminInstruction.EndFunction, ::EndFunctionDispatcher)
                return
            }
            state.emitFunctionReturn(copies)
            state.bind(root.continuationTarget)
            state.emit(AdminInstruction.EndFunction, ::EndFunctionDispatcher)
            return
        }
        state.bind(root.continuationTarget)
        state.emitFunctionReturn(copies)
        return
    }
    state.bind(root.continuationTarget)
    if (root.reachedByBranch) {
        state.emit(AdminInstruction.EndFunction, ::EndFunctionDispatcher)
    }
}

internal fun compileControlInstruction(
    state: FunctionCompilationContext,
    instruction: ControlInstruction,
    nextInstruction: Instruction?,
): Boolean {
    if (!state.reachable && instruction !is ControlInstruction.Block && instruction !is ControlInstruction.Loop &&
        instruction !is ControlInstruction.If && instruction !is ControlInstruction.TryTable &&
        instruction != ControlInstruction.Else && instruction !is ControlInstruction.End
    ) {
        return false
    }

    var consumesNextInstruction = false
    when (instruction) {
        ControlInstruction.Unreachable -> {
            state.emit(
                RuntimeControlInstruction.Unreachable,
                ::UnreachableDispatcher,
            )
            state.reachable = false
        }
        ControlInstruction.Nop -> Unit
        is ControlInstruction.Block -> enterBlock(state, BlockKind.Block, instruction.blockType)
        is ControlInstruction.Loop -> enterBlock(state, BlockKind.Loop, instruction.blockType)
        is ControlInstruction.If -> enterIf(state, instruction)
        is ControlInstruction.TryTable -> enterTryTable(state, instruction)
        ControlInstruction.Else -> enterElse(state)
        is ControlInstruction.End -> repeat(instruction.count) { exitControl(state) }
        is ControlInstruction.Br -> compileBranch(state, instruction)
        is ControlInstruction.BrIf -> compileBranchIf(state, instruction)
        is ControlInstruction.BrTable -> compileBranchTable(state, instruction)
        ControlInstruction.Return -> compileReturn(state)
        is ControlInstruction.BrOnNull -> compileBranchOnNull(state, instruction)
        is ControlInstruction.BrOnNonNull -> compileBranchOnNonNull(state, instruction)
        is ControlInstruction.BrOnCast -> compileBranchOnCast(state, instruction, onSuccess = true)
        is ControlInstruction.BrOnCastFail -> compileBranchOnCast(state, instruction, onSuccess = false)
        is ControlInstruction.Throw -> compileThrow(state, instruction)
        ControlInstruction.ThrowRef -> compileThrowRef(state)
        is ControlInstruction.Call -> {
            consumesNextInstruction = compileCall(state, instruction, nextInstruction)
        }
        is ControlInstruction.CallIndirect -> {
            consumesNextInstruction = compileCallIndirect(state, instruction, nextInstruction)
        }
        is ControlInstruction.CallRef -> {
            consumesNextInstruction = compileCallRef(state, instruction, nextInstruction)
        }
        is ControlInstruction.ReturnCall -> compileReturnCall(state, instruction)
        is ControlInstruction.ReturnCallIndirect -> compileReturnCallIndirect(state, instruction)
        is ControlInstruction.ReturnCallRef -> compileReturnCallRef(state, instruction)
    }
    return consumesNextInstruction
}

private fun enterBlock(
    state: FunctionCompilationContext,
    kind: BlockKind,
    blockType: io.github.charlietap.chasm.type.BlockType,
) {
    if (!state.reachable) {
        state.controls.pushInert(kind)
        return
    }

    val type = state.compiler.blockType(blockType)
    val parameterCount = type.params.types.size
    val baseHeight = state.operands.size - parameterCount
    check(baseHeight >= 0)
    state.materializeBelow(parameterCount)

    val resultSlots = state.resultRegionSlots(baseHeight, type.results.types.size)
    val parameterSlots = if (parameterCount == 0) {
        emptyIntArray
    } else {
        IntArray(parameterCount) { index ->
            state.operands[state.operands.size - parameterCount + index].reservedSlot
        }
    }
    val branchTarget = state.program.target()
    val continuationTarget = if (kind == BlockKind.Loop) state.program.target() else branchTarget
    val branchSlots = if (kind == BlockKind.Loop) {
        if (parameterCount == 0) {
            emptyIntArray
        } else {
            IntArray(parameterCount) { index ->
                state.materialize(state.operands[state.operands.size - parameterCount + index])
            }
        }
    } else {
        resultSlots
    }
    if (kind == BlockKind.Loop) state.bind(branchTarget)

    state.controls.push(
        kind = kind,
        baseHeight = baseHeight,
        branchSlots = branchSlots,
        resultSlots = resultSlots,
        parameterSlots = parameterSlots,
        branchTypes = if (kind == BlockKind.Loop) type.params.types else type.results.types,
        resultTypes = type.results.types,
        parameterTypes = type.params.types,
        branchTarget = branchTarget,
        continuationTarget = continuationTarget,
        handlerDepth = state.handlerDepth,
    )
}

private fun enterIf(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.If,
) {
    if (!state.reachable) {
        state.controls.pushInert(BlockKind.If)
        return
    }

    val condition = state.pop()
    enterIf(state, instruction) { elseTarget ->
        state.emitBranchIf(condition, elseTarget, emptySlotCopyPlan, whenZero = true)
    }
}

internal fun compileIfCondition(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.If,
    condition: NumericCondition,
) {
    enterIf(state, instruction) { elseTarget ->
        state.emitBranchIf(condition, elseTarget, emptySlotCopyPlan, branchOnMatch = false)
    }
}

private inline fun enterIf(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.If,
    emitCondition: (ProgramTarget) -> BranchOutcome,
) {
    val type = state.compiler.blockType(instruction.blockType)
    val parameterCount = type.params.types.size
    val baseHeight = state.operands.size - parameterCount
    check(baseHeight >= 0)
    state.materializeBelow(0)
    state.rewindFrame()

    val resultSlots = state.resultRegionSlots(baseHeight, type.results.types.size)
    val parameterSlots = if (parameterCount == 0) {
        emptyIntArray
    } else {
        IntArray(parameterCount) { index ->
            state.operands[state.operands.size - parameterCount + index].reservedSlot
        }
    }
    val continuationTarget = state.program.target()
    val elseTarget = state.program.target()
    val block = state.controls.push(
        kind = BlockKind.If,
        baseHeight = baseHeight,
        branchSlots = resultSlots,
        resultSlots = resultSlots,
        parameterSlots = parameterSlots,
        branchTypes = type.results.types,
        resultTypes = type.results.types,
        parameterTypes = type.params.types,
        branchTarget = continuationTarget,
        continuationTarget = continuationTarget,
        handlerDepth = state.handlerDepth,
    )
    block.elseTarget = elseTarget
    block.entryFrameHeight = state.frame.snapshot()
    when (emitCondition(elseTarget)) {
        BranchOutcome.Always -> {
            block.thenReachableFromCondition = false
            state.reachable = false
        }
        BranchOutcome.Never -> block.elseReachableFromCondition = false
        BranchOutcome.Dynamic -> Unit
    }
}

private fun enterTryTable(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.TryTable,
) {
    if (!state.reachable) {
        state.controls.pushInert(BlockKind.TryTable)
        return
    }
    val targetIndices = IntArray(instruction.handlers.size)
    val payloadDestinationSlots = ArrayList<IntArray>(instruction.handlers.size)
    for (index in instruction.handlers.indices) {
        val handler = instruction.handlers[index]
        val target = state.target(handler.labelIndex.toInt())
        val payloadArity = when (handler) {
            is ControlInstruction.CatchHandler.Catch -> state.compiler.tag(handler.tagIndex).type.functionType.params.types.size
            is ControlInstruction.CatchHandler.CatchRef ->
                state.compiler.tag(handler.tagIndex).type.functionType.params.types.size + 1
            is ControlInstruction.CatchHandler.CatchAll -> 0
            is ControlInstruction.CatchHandler.CatchAllRef -> 1
        }
        check(target.branchSlots.size == payloadArity)
        target.reachedByBranch = true
        targetIndices[index] = target.branchTarget.index
        payloadDestinationSlots.add(target.branchSlots)
    }
    state.handlerDepth++
    enterBlock(state, BlockKind.TryTable, instruction.blockType)
    state.emitPushHandler(
        handlers = instruction.handlers,
        targetIndices = targetIndices,
        payloadDestinationSlots = payloadDestinationSlots,
    )
}

private fun enterElse(state: FunctionCompilationContext) {
    val frame = state.controls.lastOrNull()
        ?: error("else does not match an if")
    check(frame.kind == BlockKind.If) {
        "else does not match an if"
    }
    if (frame.inert) {
        check(!frame.inElse)
        frame.inElse = true
    } else {
        transitionToElse(state, frame)
    }
}

private fun transitionToElse(
    state: FunctionCompilationContext,
    frame: BlockContext,
) {
    check(!frame.inElse)
    finalizeFallthrough(state, frame)
    frame.thenReachable = state.reachable && frame.thenReachableFromCondition
    if (frame.thenReachable) {
        state.emitJump(frame.continuationTarget)
    }
    state.bind(frame.elseTarget)
    state.unwindToHeight(frame.baseHeight)
    state.frame.restore(frame.entryFrameHeight)
    for (index in frame.parameterTypes.indices) {
        state.pushFrame(frame.parameterTypes[index], frame.parameterSlots[index])
    }
    state.reachable = frame.elseReachableFromCondition
    frame.inElse = true
}

private fun exitControl(state: FunctionCompilationContext) {
    if (state.controls.isEmpty()) {
        error("end does not match an open control")
    }
    val frame = state.controls.pop()
    when {
        frame.inert -> Unit
        frame.kind == BlockKind.If -> exitIf(state, frame)
        else -> exitStructured(state, frame)
    }
}

private fun exitStructured(
    state: FunctionCompilationContext,
    block: BlockContext,
) {
    finalizeFallthrough(state, block)
    val continuesFromBranch = block.kind != BlockKind.Loop && block.reachedByBranch
    if (!state.reachable && continuesFromBranch) {
        restoreBlockResults(state, block)
    }
    state.bind(block.continuationTarget)
    if (block.kind == BlockKind.TryTable) {
        state.emitPopHandler()
        state.handlerDepth--
    }
    state.rewindFrame()
}

private fun exitIf(
    state: FunctionCompilationContext,
    frame: BlockContext,
) {
    if (!frame.inElse) transitionToElse(state, frame)
    finalizeFallthrough(state, frame)
    val elseReachable = state.reachable
    when {
        elseReachable -> Unit
        frame.thenReachable -> restoreBlockResults(state, frame)
        frame.reachedByBranch -> restoreBlockResults(state, frame)
        else -> state.reachable = false
    }
    state.bind(frame.continuationTarget)
    state.rewindFrame()
}

private fun finalizeFallthrough(
    state: FunctionCompilationContext,
    block: BlockContext,
) {
    if (!state.reachable) return
    check(state.operands.size == block.baseHeight + block.resultSlots.size) {
        "control fallthrough does not match its result type: block=${block.kind} stack=${state.operands}"
    }
    val resultCount = block.resultSlots.size
    if (resultCount > 0) {
        val sourceSlots = IntArray(resultCount) { index ->
            state.materialize(state.operands[state.operands.size - resultCount + index])
        }
        state.emitCopies(sourceSlots, block.resultSlots)
    }
    state.replaceStack(block.baseHeight, block.resultTypes, block.resultSlots)
}

private fun restoreBlockResults(
    state: FunctionCompilationContext,
    block: BlockContext,
) {
    state.replaceStack(block.baseHeight, block.resultTypes, block.resultSlots)
    state.reachable = true
}

private fun compileBranch(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.Br,
) {
    val target = state.target(instruction.labelIndex.toInt())
    state.emitJump(
        target.branchTarget,
        state.planCopies(target.branchSlots),
        state.handlerDepth - target.handlerDepth,
    )
    target.reachedByBranch = true
    state.reachable = false
}

private fun compileBranchIf(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.BrIf,
) {
    val condition = state.pop()
    val target = state.target(instruction.labelIndex.toInt())
    val outcome = state.emitBranchIf(
        condition = condition,
        target = target.branchTarget,
        copies = state.planCopies(target.branchSlots),
        handlerPopCount = state.handlerDepth - target.handlerDepth,
    )
    if (outcome != BranchOutcome.Never) target.reachedByBranch = true
    if (outcome == BranchOutcome.Always) state.reachable = false
}

internal fun compileBranchIfCondition(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.BrIf,
    condition: NumericCondition,
) {
    val target = state.target(instruction.labelIndex.toInt())
    val outcome = state.emitBranchIf(
        condition = condition,
        target = target.branchTarget,
        copies = state.planCopies(target.branchSlots),
        handlerPopCount = state.handlerDepth - target.handlerDepth,
    )
    if (outcome != BranchOutcome.Never) target.reachedByBranch = true
    if (outcome == BranchOutcome.Always) state.reachable = false
}

private fun compileBranchTable(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.BrTable,
) {
    val selector = state.pop()
    if (selector.sourceKind == OperandSourceKind.I32Immediate) {
        val selectorIndex = selector.sourceBits.toInt()
        val labelIndex = instruction.labelIndices.getOrNull(selectorIndex) ?: instruction.defaultLabelIndex
        val target = state.target(labelIndex.toInt())
        state.emitJump(
            target = target.branchTarget,
            copies = state.planCopies(target.branchSlots),
            handlerPopCount = state.handlerDepth - target.handlerDepth,
        )
        target.reachedByBranch = true
        state.reachable = false
        return
    }

    val defaultTarget = state.target(instruction.defaultLabelIndex.toInt())
    val arity = defaultTarget.branchSlots.size
    val targetIndices = IntArray(instruction.labelIndices.size + 1)
    for (index in instruction.labelIndices.indices) {
        val target = state.target(instruction.labelIndices[index].toInt())
        check(target.branchSlots.size == arity)
        targetIndices[index] = state.prepareBranchTarget(
            target = target.branchTarget,
            copies = state.planCopies(target.branchSlots),
            handlerPopCount = state.handlerDepth - target.handlerDepth,
        ).index
        target.reachedByBranch = true
    }
    targetIndices[instruction.labelIndices.size] = state.prepareBranchTarget(
        target = defaultTarget.branchTarget,
        copies = state.planCopies(defaultTarget.branchSlots),
        handlerPopCount = state.handlerDepth - defaultTarget.handlerDepth,
    ).index
    state.emitBranchTable(
        selector = selector,
        targetIndices = targetIndices,
    )
    defaultTarget.reachedByBranch = true
    state.reachable = false
}

private fun compileReturn(state: FunctionCompilationContext) {
    val root = checkNotNull(state.rootControl)
    state.emitFunctionReturn(state.planCopies(root.branchSlots))
    state.reachable = false
}

private fun compileBranchOnNull(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.BrOnNull,
) {
    val operand = state.operands.last()
    val target = state.target(instruction.labelIndex.toInt())
    state.emitBranchOnNull(
        operand = operand,
        target = target.branchTarget,
        copies = state.planCopies(target.branchSlots, excludedTopCount = 1),
        onNull = true,
        handlerPopCount = state.handlerDepth - target.handlerDepth,
    )
    target.reachedByBranch = true
}

private fun compileBranchOnNonNull(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.BrOnNonNull,
) {
    val operand = state.operands.last()
    val target = state.target(instruction.labelIndex.toInt())
    state.emitBranchOnNull(
        operand = operand,
        target = target.branchTarget,
        copies = state.planCopies(target.branchSlots),
        onNull = false,
        handlerPopCount = state.handlerDepth - target.handlerDepth,
    )
    state.pop()
    target.reachedByBranch = true
}

private fun compileBranchOnCast(
    state: FunctionCompilationContext,
    instruction: ControlInstruction,
    onSuccess: Boolean,
) {
    val (labelIndex, destinationType) = when (instruction) {
        is ControlInstruction.BrOnCast -> instruction.labelIndex to instruction.dstReferenceType
        is ControlInstruction.BrOnCastFail -> instruction.labelIndex to instruction.dstReferenceType
        else -> error("not a cast branch: $instruction")
    }
    val operand = state.operands.last()
    val target = state.target(labelIndex.toInt())
    state.emitBranchOnCast(
        operand = operand,
        target = target.branchTarget,
        copies = state.planCopies(target.branchSlots),
        typeTest = ReferenceTypeTest.from(destinationType, state.compiler.runtimeTypes),
        onSuccess = onSuccess,
        handlerPopCount = state.handlerDepth - target.handlerDepth,
    )
    target.reachedByBranch = true
}

private fun compileThrow(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.Throw,
) {
    val payloads = state.pop(state.compiler.tag(instruction.tagIndex).type.functionType.params.types.size)
    state.emitThrow(
        tagIndex = instruction.tagIndex,
        payloadSlots = if (payloads.isEmpty()) {
            emptyIntArray
        } else {
            IntArray(payloads.size) { index -> state.materialize(payloads[index]) }
        },
    )
    state.reachable = false
}

private fun compileThrowRef(state: FunctionCompilationContext) {
    state.emitThrowRef(state.materialize(state.pop()))
    state.reachable = false
}

private fun compileCall(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.Call,
    nextInstruction: Instruction?,
): Boolean {
    val function = state.compiler.function(instruction.functionIndex)
    val type = function.functionType
    val operands = state.pop(type.params.types.size)
    val callFrameSlot = state.callFrameSlot()
    val interfaceSlots = maxOf(type.params.types.size, type.results.types.size)
    reserveCallInterface(state, callFrameSlot, interfaceSlots)
    val destination = callResultDestination(state, type.results.types, nextInstruction)
    state.emitCall(
        function = function,
        operands = operands,
        resultSlotBase = destination?.slot ?: callFrameSlot,
        callFrameSlot = callFrameSlot,
    )
    return completeCallResults(state, type.results.types, callFrameSlot, destination)
}

private fun compileCallIndirect(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.CallIndirect,
    nextInstruction: Instruction?,
): Boolean {
    val type = state.compiler.types.functionType(instruction.typeIndex)
    val elementIndex = state.pop()
    val operands = state.pop(type.params.types.size)
    val callFrameSlot = state.callFrameSlot()
    val interfaceSlots = maxOf(type.params.types.size, type.results.types.size)
    reserveCallInterface(state, callFrameSlot, interfaceSlots)
    val destination = callResultDestination(state, type.results.types, nextInstruction)
    state.emitCallIndirect(
        elementIndex = elementIndex,
        operands = operands,
        type = state.compiler.rtt(instruction.typeIndex),
        table = state.compiler.table(instruction.tableIndex),
        resultSlotBase = destination?.slot ?: callFrameSlot,
        callFrameSlot = callFrameSlot,
    )
    return completeCallResults(state, type.results.types, callFrameSlot, destination)
}

private fun compileCallRef(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.CallRef,
    nextInstruction: Instruction?,
): Boolean {
    val type = state.compiler.types.functionType(instruction.typeIndex)
    val functionReference = state.pop()
    val operands = state.pop(type.params.types.size)
    val callFrameSlot = state.callFrameSlot()
    val interfaceSlots = maxOf(type.params.types.size, type.results.types.size)
    reserveCallInterface(state, callFrameSlot, interfaceSlots)
    val destination = callResultDestination(state, type.results.types, nextInstruction)
    check(!functionReference.isImmediate)
    state.emitCallRef(
        functionSlot = functionReference.sourceSlot,
        operands = operands,
        resultSlotBase = destination?.slot ?: callFrameSlot,
        callFrameSlot = callFrameSlot,
    )
    return completeCallResults(state, type.results.types, callFrameSlot, destination)
}

internal fun compileKnownReferenceCall(
    state: FunctionCompilationContext,
    reference: ReferenceInstruction.RefFunc,
    instruction: ControlInstruction.CallRef,
    nextInstruction: Instruction?,
): Boolean {
    val type = state.compiler.types.functionType(instruction.typeIndex)
    val operands = state.pop(type.params.types.size)
    val callFrameSlot = state.callFrameSlot()
    val interfaceSlots = maxOf(type.params.types.size, type.results.types.size)
    reserveCallInterface(state, callFrameSlot, interfaceSlots)
    val destination = callResultDestination(state, type.results.types, nextInstruction)
    state.emitCall(
        function = state.compiler.function(reference.funcIdx),
        operands = operands,
        resultSlotBase = destination?.slot ?: callFrameSlot,
        callFrameSlot = callFrameSlot,
    )
    return completeCallResults(state, type.results.types, callFrameSlot, destination)
}

private fun compileReturnCall(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.ReturnCall,
) {
    val function = state.compiler.function(instruction.functionIndex)
    val operands = state.pop(function.functionType.params.types.size)
    repeat(state.handlerDepth) { state.emitPopHandler() }
    state.emitReturnCall(function, operands)
    state.reachable = false
}

private fun compileReturnCallIndirect(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.ReturnCallIndirect,
) {
    val type = state.compiler.types.functionType(instruction.typeIndex)
    val elementIndex = state.pop()
    val operands = state.pop(type.params.types.size)
    repeat(state.handlerDepth) { state.emitPopHandler() }
    state.emitReturnCallIndirect(
        elementIndex = elementIndex,
        operands = operands,
        type = state.compiler.rtt(instruction.typeIndex),
        table = state.compiler.table(instruction.tableIndex),
    )
    state.reachable = false
}

private fun compileReturnCallRef(
    state: FunctionCompilationContext,
    instruction: ControlInstruction.ReturnCallRef,
) {
    val type = state.compiler.types.functionType(instruction.typeIndex)
    val functionReference = state.pop()
    val operands = state.pop(type.params.types.size)
    repeat(state.handlerDepth) { state.emitPopHandler() }
    state.emitReturnCallRef(
        functionSlot = state.materialize(functionReference),
        operands = operands,
    )
    state.reachable = false
}

internal fun compileKnownReferenceReturnCall(
    state: FunctionCompilationContext,
    reference: ReferenceInstruction.RefFunc,
    instruction: ControlInstruction.ReturnCallRef,
) {
    val type = state.compiler.types.functionType(instruction.typeIndex)
    val operands = state.pop(type.params.types.size)
    repeat(state.handlerDepth) { state.emitPopHandler() }
    state.emitReturnCall(state.compiler.function(reference.funcIdx), operands)
    state.reachable = false
}

private fun reserveCallInterface(state: FunctionCompilationContext, base: Int, size: Int) {
    if (size > 0) state.frame.reserve(base + size - 1)
}

private fun pushCallResults(
    state: FunctionCompilationContext,
    types: List<io.github.charlietap.chasm.type.ValueType>,
    slotBase: Int,
) {
    for (index in types.indices) {
        state.pushFrame(types[index], slotBase + index)
    }
}

private fun callResultDestination(
    state: FunctionCompilationContext,
    types: List<io.github.charlietap.chasm.type.ValueType>,
    nextInstruction: Instruction?,
): Destination? = if (types.size == 1) destination(state, null, nextInstruction) else null

private fun completeCallResults(
    state: FunctionCompilationContext,
    types: List<io.github.charlietap.chasm.type.ValueType>,
    slotBase: Int,
    destination: Destination?,
): Boolean {
    if (destination == null) {
        pushCallResults(state, types, slotBase)
    } else {
        completeDestination(state, types.single(), destination)
    }
    return destination?.consumesNextInstruction == true
}

private fun FunctionCompilationContext.target(depth: Int): BlockContext {
    check(depth >= 0)
    val controlIndex = controls.lastIndex - depth
    if (controlIndex >= 0) {
        return controls[controlIndex].also { control ->
            check(!control.inert) {
                "reachable code cannot target an inert control"
            }
        }
    }
    if (controlIndex == -1) return checkNotNull(rootControl)
    error("label depth is outside the active control stack: $depth")
}
