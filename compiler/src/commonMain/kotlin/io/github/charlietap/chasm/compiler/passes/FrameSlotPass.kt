package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.instruction.TableSuperInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableSuperInstruction
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander
import io.github.charlietap.chasm.type.expansion.LegacyBlockTypeExpander
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.ext.structType

internal fun FrameSlotPass(
    context: PassContext,
    module: Module,
): Module = module.copy(
    functions = module.functions.map { function ->
        FrameSlotFunctionLowerer(context, function)
    },
)

private fun FrameSlotFunctionLowerer(
    context: PassContext,
    function: Function,
): Function {
    val functionType = context.module.definedTypes
        .getOrNull(function.typeIndex.idx)
        ?.functionType()
        ?: return function

    val layout = FrameSlotFunctionLayout(
        functionType = functionType,
        definedLocalTypes = function.locals.map { it.type },
    )
    val allocator = FrameSlotAllocator(
        temporarySlotBase = layout.temporarySlotBase,
    )
    val state = FrameSlotState(
        layout = layout,
        allocator = allocator,
        stack = mutableListOf(),
    )
    val rootLabel = LabelContext(
        kind = LabelKind.Block,
        baseHeight = 0,
        branchSlots = layout.returnSlots,
        fallthroughSlots = layout.returnSlots,
        branchTypes = functionType.results.types,
        fallthroughTypes = functionType.results.types,
    )
    val labels = ArrayDeque<LabelContext>().apply {
        addLast(rootLabel)
    }
    val loweredInstructions = FrameSlotExpressionLowerer(context, function.body.instructions, state, labels)
        ?: error(
            "frame-slot lowering failed before expression completed: " +
                "typeIndex=${function.typeIndex.idx} locals=${function.locals.size} " +
                "instructions=${function.body.instructions}",
        )

    val validLabelState = if (state.reachable) {
        labels.isEmpty() || (labels.size == 1 && labels.first() === rootLabel)
    } else {
        labels.size == 1 && labels.first() === rootLabel
    }

    if (!validLabelState) {
        error(
            "frame-slot lowering left invalid label state: " +
                "typeIndex=${function.typeIndex.idx} locals=${function.locals.size} " +
                "reachable=${state.reachable} labels=$labels",
        )
    }

    val finalizedInstructions = if (state.reachable) {
        if (state.stack.size < rootLabel.fallthroughSlots.size) {
            error(
                "frame-slot function lowering ended with too few stack values: " +
                    "typeIndex=${function.typeIndex.idx} locals=${function.locals.size} " +
                    "stack=${state.stack} returnSlots=${rootLabel.fallthroughSlots}",
            )
        }
        val finalResultOperands = state.stack.takeLast(rootLabel.fallthroughSlots.size)
        val (materializeResults, finalResultSlots) = FrameSlotMaterializeOperands(finalResultOperands, state)
        val trailingEndFunction = loweredInstructions.lastOrNull() as? AdminInstruction.EndFunction
        val bodyInstructions = if (trailingEndFunction != null) {
            loweredInstructions.dropLast(1)
        } else {
            loweredInstructions
        }

        buildList {
            addAll(bodyInstructions)
            addAll(materializeResults)
            addAll(FrameSlotCopyInstructions(finalResultSlots, rootLabel.fallthroughSlots))
            trailingEndFunction?.let(::add)
        }
    } else {
        loweredInstructions
    }

    return function.copy(
        body = Expression(finalizedInstructions),
        frameSlots = allocator.maxSlotExclusive,
        frameSlotMode = true,
        returnSlots = layout.returnSlots,
    )
}

private data class FrameSlotFunctionLayout(
    val paramTypes: List<ValueType>,
    val definedLocalTypes: List<ValueType>,
    val resultTypes: List<ValueType>,
) {
    val localTypes: List<ValueType> = buildList {
        addAll(paramTypes)
        addAll(definedLocalTypes)
    }

    val interfaceSlots: Int = maxOf(paramTypes.size, resultTypes.size)
    val baseSlots: Int = interfaceSlots + definedLocalTypes.size
    val returnSlots: List<Int> = List(resultTypes.size, ::resultSlot)
    val temporarySlotBase: Int = baseSlots

    fun localType(localIndex: Int): ValueType? = localTypes.getOrNull(localIndex)

    fun localSlot(localIndex: Int): Int = if (localIndex < paramTypes.size) {
        localIndex
    } else {
        interfaceSlots + (localIndex - paramTypes.size)
    }

    fun resultSlot(resultIndex: Int): Int = resultIndex
}

private fun FrameSlotFunctionLayout(
    functionType: FunctionType,
    definedLocalTypes: List<ValueType>,
): FrameSlotFunctionLayout = FrameSlotFunctionLayout(
    paramTypes = functionType.params.types,
    definedLocalTypes = definedLocalTypes,
    resultTypes = functionType.results.types,
)

private data class FrameSlotStackOperand(
    val type: ValueType?,
    val reservedSlot: Int,
    var immediate: FusedOperand? = null,
    var localAlias: Int? = null,
)

private data class FrameSlotAllocator(
    val temporarySlotBase: Int,
    var currentTemporaryHeight: Int = 0,
    var maxTemporaryHeight: Int = 0,
) {
    val currentSlotExclusive: Int
        get() = temporarySlotBase + currentTemporaryHeight

    val maxSlotExclusive: Int
        get() = temporarySlotBase + maxTemporaryHeight

    fun allocateTemporarySlot(): Int {
        val slot = currentSlotExclusive
        currentTemporaryHeight++
        maxTemporaryHeight = maxOf(maxTemporaryHeight, currentTemporaryHeight)
        return slot
    }

    fun reserveTemporarySlot(slot: Int) {
        if (!isTemporarySlot(slot)) return
        val requiredHeight = slot - temporarySlotBase + 1
        currentTemporaryHeight = maxOf(currentTemporaryHeight, requiredHeight)
        maxTemporaryHeight = maxOf(maxTemporaryHeight, currentTemporaryHeight)
    }

    fun releaseTemporarySlot(slot: Int) {
        if (!isTemporarySlot(slot)) return
        if (slot != currentSlotExclusive - 1) return
        currentTemporaryHeight--
    }

    fun rewindToReferencedSlots(referencedSlots: Iterable<Int>) {
        val highestReferencedSlot = referencedSlots
            .filter(::isTemporarySlot)
            .maxOrNull()

        currentTemporaryHeight = highestReferencedSlot
            ?.let { slot -> slot - temporarySlotBase + 1 }
            ?: 0
    }

    fun restoreTemporaryHeight(height: Int) {
        currentTemporaryHeight = height
    }

    fun isTemporarySlot(slot: Int): Boolean = slot >= temporarySlotBase

    fun temporaryHeightSnapshot(): Int = currentTemporaryHeight
}

private data class FrameSlotState(
    val layout: FrameSlotFunctionLayout,
    val allocator: FrameSlotAllocator,
    val stack: MutableList<FrameSlotStackOperand>,
    val localAliases: List<LinkedHashSet<FrameSlotStackOperand>> = List(layout.localTypes.size) { linkedSetOf() },
    var reachable: Boolean = true,
) {
    val baseSlots: Int
        get() = layout.baseSlots

    fun localType(localIndex: Int): ValueType? = layout.localType(localIndex)

    fun localSlot(localIndex: Int): Int = layout.localSlot(localIndex)
}

private fun FrameSlotForkState(
    state: FrameSlotState,
    reachable: Boolean = state.reachable,
): FrameSlotState {
    val clonedStack = state.stack.map { operand ->
        FrameSlotStackOperand(
            type = operand.type,
            reservedSlot = operand.reservedSlot,
            immediate = operand.immediate,
            localAlias = operand.localAlias,
        )
    }.toMutableList()
    val clonedState = FrameSlotState(
        layout = state.layout,
        allocator = state.allocator,
        stack = clonedStack,
        reachable = reachable,
    )
    clonedStack.forEach { operand ->
        clonedState.trackLocalAlias(operand)
    }
    return clonedState
}

private fun FrameSlotState.rewindTemporaryAllocator(
    labels: ArrayDeque<LabelContext> = ArrayDeque(),
) {
    allocator.rewindToReferencedSlots(
        referencedSlots = stack.map(FrameSlotStackOperand::reservedSlot),
    )
}

private fun FrameSlotState.temporaryHeightForStackPrefix(
    stackHeight: Int,
): Int = stack
    .take(stackHeight)
    .map(FrameSlotStackOperand::reservedSlot)
    .filter(allocator::isTemporarySlot)
    .maxOrNull()
    ?.let { slot -> slot - allocator.temporarySlotBase + 1 }
    ?: 0

private fun FrameSlotState.resultRegionSlots(
    baseHeight: Int,
    arity: Int,
): List<Int> {
    val entryTemporaryHeight = temporaryHeightForStackPrefix(baseHeight)
    return List(arity) { index -> allocator.temporarySlotBase + entryTemporaryHeight + index }
}

private enum class LabelKind {
    Block,
    Loop,
}

private data class LabelContext(
    val kind: LabelKind,
    val baseHeight: Int,
    val branchSlots: List<Int>,
    val fallthroughSlots: List<Int>,
    val branchTypes: List<ValueType>,
    val fallthroughTypes: List<ValueType>,
    var reachedByBranch: Boolean = false,
)

private fun FrameSlotExpressionLowerer(
    context: PassContext,
    instructions: List<Instruction>,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val lowered = ArrayList<Instruction>(instructions.size)

    for ((index, instruction) in instructions.withIndex()) {
        if (!state.reachable) {
            lowered.addAll(
                FrameSlotUnreachableInstructionsLowerer(
                    context = context,
                    instructions = instructions.subList(index, instructions.size),
                    state = state,
                ),
            )
            break
        }

        val loweredInstructions = FrameSlotInstructionLowerer(context, instruction, state, labels)
            ?: error(
                "frame-slot instruction lowering failed: instruction=$instruction " +
                    "state=$state labels=$labels",
            )
        lowered += loweredInstructions
        state.rewindTemporaryAllocator(labels)
    }

    return lowered
}

private fun FrameSlotUnreachableInstructionsLowerer(
    context: PassContext,
    instructions: List<Instruction>,
    state: FrameSlotState,
): List<Instruction> = buildList {
    instructions.forEach { instruction ->
        addAll(FrameSlotUnreachableInstructionLowerer(context, instruction, state))
        state.rewindTemporaryAllocator()
    }
}

private fun FrameSlotUnreachableInstructionLowerer(
    context: PassContext,
    instruction: Instruction,
    state: FrameSlotState,
): List<Instruction> = when (instruction) {
    is NumericSuperInstruction -> FrameSlotNumericInstructionLowerer(instruction, state) ?: listOf(instruction)
    is MemorySuperInstruction -> FrameSlotMemoryInstructionLowerer(instruction, state) ?: listOf(instruction)
    is ParametricInstruction.Select,
    is ParametricInstruction.SelectWithType,
    -> FrameSlotRawSelectLowerer(state) ?: listOf(instruction)
    is VariableInstruction.LocalGet -> FrameSlotLocalGetLowerer(
        localIdx = instruction.localIdx,
        state = state,
    )
    is VariableInstruction.GlobalGet -> FrameSlotGlobalGetLowerer(
        globalIdx = instruction.globalIdx,
        state = state,
    )
    is VariableInstruction.LocalSet -> FrameSlotLocalSetLowerer(
        operand = FusedOperand.ValueStack,
        localIdx = instruction.localIdx,
        state = state,
    ) ?: listOf(instruction)
    is VariableInstruction.GlobalSet -> {
        val operand = FrameSlotOperandLowerer(FusedOperand.ValueStack, state)
        operand?.let {
            listOf(
                VariableSuperInstruction.GlobalSet(
                    operand = it.lowered,
                    globalIdx = instruction.globalIdx,
                ),
            )
        } ?: listOf(instruction)
    }
    is VariableInstruction.LocalTee -> FrameSlotLocalTeeLowerer(
        operand = FusedOperand.ValueStack,
        localIdx = instruction.localIdx,
        state = state,
    ) ?: listOf(instruction)
    is VariableSuperInstruction.GlobalGet -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    } ?: listOf(instruction)
    is VariableSuperInstruction.GlobalSet -> {
        val operand = FrameSlotOperandLowerer(instruction.operand, state)
        operand?.let {
            listOf(
                instruction.copy(
                    operand = it.lowered,
                ),
            )
        } ?: listOf(instruction)
    }
    is VariableSuperInstruction.LocalSet -> FrameSlotLocalSetLowerer(
        operand = instruction.operand,
        localIdx = instruction.localIdx,
        state = state,
    ) ?: listOf(instruction)
    is VariableSuperInstruction.LocalTee -> FrameSlotLocalTeeLowerer(
        operand = instruction.operand,
        localIdx = instruction.localIdx,
        state = state,
    ) ?: listOf(instruction)
    is ParametricSuperInstruction.Select -> FrameSlotSelectLowerer(
        instruction = instruction,
        state = state,
    ) ?: listOf(instruction)
    is ReferenceInstruction -> FrameSlotRawReferenceInstructionLowerer(instruction, state) ?: listOf(instruction)
    is ReferenceSuperInstruction -> FrameSlotReferenceInstructionLowerer(
        instruction = instruction,
        state = state,
    ) ?: listOf(instruction)
    is TableInstruction -> FrameSlotRawTableInstructionLowerer(instruction, state) ?: listOf(instruction)
    is TableSuperInstruction -> FrameSlotTableInstructionLowerer(instruction, state) ?: listOf(instruction)
    is AggregateInstruction -> FrameSlotRawAggregateInstructionLowerer(context, instruction, state) ?: listOf(instruction)
    is AggregateSuperInstruction -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = instruction,
        state = state,
    ) ?: listOf(instruction)
    is ControlInstruction.Block -> listOf(
        instruction.copy(
            instructions = FrameSlotUnreachableInstructionsLowerer(
                context = context,
                instructions = instruction.instructions,
                state = FrameSlotForkState(state, reachable = false),
            ),
        ),
    )
    is ControlInstruction.Loop -> listOf(
        instruction.copy(
            instructions = FrameSlotUnreachableInstructionsLowerer(
                context = context,
                instructions = instruction.instructions,
                state = FrameSlotForkState(state, reachable = false),
            ),
        ),
    )
    is ControlInstruction.If -> listOf(
        instruction.copy(
            thenInstructions = FrameSlotUnreachableInstructionsLowerer(
                context = context,
                instructions = instruction.thenInstructions,
                state = FrameSlotForkState(state, reachable = false),
            ),
            elseInstructions = instruction.elseInstructions?.let { elseInstructions ->
                FrameSlotUnreachableInstructionsLowerer(
                    context = context,
                    instructions = elseInstructions,
                    state = FrameSlotForkState(state, reachable = false),
                )
            },
        ),
    )
    is ControlInstruction.TryTable -> listOf(
        instruction.copy(
            instructions = FrameSlotUnreachableInstructionsLowerer(
                context = context,
                instructions = instruction.instructions,
                state = FrameSlotForkState(state, reachable = false),
            ),
        ),
    )
    is ControlSuperInstruction.If -> listOf(
        instruction.copy(
            thenInstructions = FrameSlotUnreachableInstructionsLowerer(
                context = context,
                instructions = instruction.thenInstructions,
                state = FrameSlotForkState(state, reachable = false),
            ),
            elseInstructions = instruction.elseInstructions?.let { elseInstructions ->
                FrameSlotUnreachableInstructionsLowerer(
                    context = context,
                    instructions = elseInstructions,
                    state = FrameSlotForkState(state, reachable = false),
                )
            },
        ),
    )
    is ControlSuperInstruction.Call -> FrameSlotCallLowerer(
        context = context,
        operands = instruction.operands,
        functionIndex = instruction.functionIndex,
        state = state,
    ) ?: listOf(instruction)
    is ControlSuperInstruction.CallIndirect -> FrameSlotCallIndirectLowerer(
        context = context,
        elementIndex = instruction.elementIndex,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        tableIndex = instruction.tableIndex,
        state = state,
    ) ?: listOf(instruction)
    is ControlSuperInstruction.CallRef -> FrameSlotCallRefLowerer(
        context = context,
        functionReference = instruction.functionReference,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        state = state,
    ) ?: listOf(instruction)
    is ControlSuperInstruction.ReturnCall -> FrameSlotReturnCallLowerer(
        context = context,
        operands = instruction.operands,
        functionIndex = instruction.functionIndex,
        state = state,
    ) ?: listOf(instruction)
    is ControlSuperInstruction.ReturnCallIndirect -> FrameSlotReturnCallIndirectLowerer(
        context = context,
        elementIndex = instruction.elementIndex,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        tableIndex = instruction.tableIndex,
        state = state,
    ) ?: listOf(instruction)
    is ControlSuperInstruction.ReturnCallRef -> FrameSlotReturnCallRefLowerer(
        context = context,
        functionReference = instruction.functionReference,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        state = state,
    ) ?: listOf(instruction)
    else -> listOf(instruction)
}

private fun FrameSlotInstructionLowerer(
    context: PassContext,
    instruction: Instruction,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? = when (instruction) {
    is AdminInstruction.EndBlock -> FrameSlotEndBlockLowerer(state, labels)
    is AdminInstruction.EndFunction -> listOf(instruction)
    is ControlInstruction.Nop -> listOf(instruction)
    is ControlInstruction.Unreachable -> {
        state.reachable = false
        listOf(instruction)
    }
    is ControlInstruction.Br -> FrameSlotBrLowerer(
        instruction = instruction,
        state = state,
        labels = labels,
    )
    is ControlInstruction.BrIf -> FrameSlotBrIfLowerer(
        operand = FusedOperand.ValueStack,
        labelIndex = instruction.labelIndex,
        state = state,
        labels = labels,
    )
    is ControlInstruction.BrTable -> FrameSlotBrTableLowerer(
        operand = FusedOperand.ValueStack,
        labelIndices = instruction.labelIndices,
        defaultLabelIndex = instruction.defaultLabelIndex,
        state = state,
        labels = labels,
    )
    is ControlInstruction.BrOnNull -> FrameSlotBrOnNullLowerer(
        labelIndex = instruction.labelIndex,
        state = state,
        labels = labels,
    )
    is ControlInstruction.BrOnNonNull -> FrameSlotBrOnNonNullLowerer(
        labelIndex = instruction.labelIndex,
        state = state,
        labels = labels,
    )
    is ControlInstruction.BrOnCast -> FrameSlotBrOnCastLowerer(
        instruction = instruction,
        state = state,
        labels = labels,
    )
    is ControlInstruction.BrOnCastFail -> FrameSlotBrOnCastFailLowerer(
        instruction = instruction,
        state = state,
        labels = labels,
    )
    is ControlInstruction.Call -> FrameSlotCallLowerer(
        context = context,
        operands = null,
        functionIndex = instruction.functionIndex,
        state = state,
    )
    is ControlInstruction.CallIndirect -> FrameSlotCallIndirectLowerer(
        context = context,
        elementIndex = null,
        operands = null,
        typeIndex = instruction.typeIndex,
        tableIndex = instruction.tableIndex,
        state = state,
    )
    is ControlInstruction.CallRef -> FrameSlotCallRefLowerer(
        context = context,
        functionReference = null,
        operands = null,
        typeIndex = instruction.typeIndex,
        state = state,
    )
    is ControlInstruction.If -> FrameSlotIfLowerer(
        context = context,
        operand = FusedOperand.ValueStack,
        blockType = instruction.blockType,
        thenInstructions = instruction.thenInstructions,
        elseInstructions = instruction.elseInstructions,
        state = state,
        labels = labels,
    )
    is ControlInstruction.TryTable -> FrameSlotTryTableLowerer(
        context = context,
        instruction = instruction,
        state = state,
        labels = labels,
    )
    is ControlInstruction.Return -> FrameSlotReturnLowerer(
        state = state,
        labels = labels,
    )
    is ControlInstruction.Throw -> FrameSlotThrowLowerer(
        context = context,
        instruction = instruction,
        state = state,
    )
    is ControlInstruction.ThrowRef -> FrameSlotThrowRefLowerer(state)
    is ControlInstruction.ReturnCall -> FrameSlotReturnCallLowerer(
        context = context,
        operands = null,
        functionIndex = instruction.functionIndex,
        state = state,
    )
    is ControlInstruction.ReturnCallIndirect -> FrameSlotReturnCallIndirectLowerer(
        context = context,
        elementIndex = null,
        operands = null,
        typeIndex = instruction.typeIndex,
        tableIndex = instruction.tableIndex,
        state = state,
    )
    is ControlInstruction.ReturnCallRef -> FrameSlotReturnCallRefLowerer(
        context = context,
        functionReference = null,
        operands = null,
        typeIndex = instruction.typeIndex,
        state = state,
    )
    is ParametricInstruction.Drop -> {
        state.popStackOperand() ?: return null
        emptyList()
    }
    is ParametricInstruction.Select,
    is ParametricInstruction.SelectWithType,
    -> FrameSlotRawSelectLowerer(state)
    is VariableInstruction.LocalGet -> FrameSlotLocalGetLowerer(
        localIdx = instruction.localIdx,
        state = state,
    )
    is VariableInstruction.GlobalGet -> FrameSlotGlobalGetLowerer(
        globalIdx = instruction.globalIdx,
        state = state,
    )
    is VariableInstruction.LocalSet -> FrameSlotLocalSetLowerer(
        operand = FusedOperand.ValueStack,
        localIdx = instruction.localIdx,
        state = state,
    )
    is VariableInstruction.GlobalSet -> {
        val operand = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
        listOf(
            VariableSuperInstruction.GlobalSet(
                operand = operand.lowered,
                globalIdx = instruction.globalIdx,
            ),
        )
    }
    is VariableInstruction.LocalTee -> FrameSlotLocalTeeLowerer(
        operand = FusedOperand.ValueStack,
        localIdx = instruction.localIdx,
        state = state,
    )
    is ReferenceInstruction -> FrameSlotRawReferenceInstructionLowerer(instruction, state)
    is NumericInstruction -> FrameSlotRawNumericInstructionLowerer(instruction, state)
    is TableInstruction -> FrameSlotRawTableInstructionLowerer(instruction, state)
    is AggregateInstruction -> FrameSlotRawAggregateInstructionLowerer(context, instruction, state)
    is NumericSuperInstruction -> FrameSlotNumericInstructionLowerer(instruction, state)
    is MemoryInstruction -> FrameSlotRawMemoryInstructionLowerer(instruction, state)
    is MemorySuperInstruction -> FrameSlotMemoryInstructionLowerer(instruction, state)
    is VariableSuperInstruction.GlobalGet -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is VariableSuperInstruction.GlobalSet -> {
        val operand = FrameSlotOperandLowerer(instruction.operand, state) ?: return null
        listOf(
            instruction.copy(
                operand = operand.lowered,
            ),
        )
    }
    is VariableSuperInstruction.LocalSet -> FrameSlotLocalSetLowerer(
        operand = instruction.operand,
        localIdx = instruction.localIdx,
        state = state,
    )
    is VariableSuperInstruction.LocalTee -> FrameSlotLocalTeeLowerer(
        operand = instruction.operand,
        localIdx = instruction.localIdx,
        state = state,
    )
    is ParametricSuperInstruction.Select -> FrameSlotSelectLowerer(
        instruction = instruction,
        state = state,
    )
    is ReferenceSuperInstruction -> FrameSlotReferenceInstructionLowerer(
        instruction = instruction,
        state = state,
    )
    is TableSuperInstruction -> FrameSlotTableInstructionLowerer(
        instruction = instruction,
        state = state,
    )
    is AggregateSuperInstruction -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = instruction,
        state = state,
    )
    is ControlInstruction.Block -> {
        val functionType = context.blockType(instruction.blockType) ?: return null
        FrameSlotStructuredControlLowerer(
            context = context,
            kind = LabelKind.Block,
            functionType = functionType,
            instructions = instruction.instructions,
            state = state,
            labels = labels,
        ) { loweredInstructions ->
            instruction.copy(
                instructions = loweredInstructions,
            )
        }
    }
    is ControlInstruction.Loop -> {
        val functionType = context.blockType(instruction.blockType) ?: return null
        FrameSlotStructuredControlLowerer(
            context = context,
            kind = LabelKind.Loop,
            functionType = functionType,
            instructions = instruction.instructions,
            state = state,
            labels = labels,
        ) { loweredInstructions ->
            instruction.copy(
                instructions = loweredInstructions,
            )
        }
    }
    is ControlSuperInstruction.BrIf -> FrameSlotBrIfLowerer(
        operand = instruction.operand,
        labelIndex = instruction.labelIndex,
        state = state,
        labels = labels,
    )
    is ControlSuperInstruction.Call -> FrameSlotCallLowerer(
        context = context,
        operands = instruction.operands,
        functionIndex = instruction.functionIndex,
        state = state,
    )
    is ControlSuperInstruction.CallIndirect -> FrameSlotCallIndirectLowerer(
        context = context,
        elementIndex = instruction.elementIndex,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        tableIndex = instruction.tableIndex,
        state = state,
    )
    is ControlSuperInstruction.CallRef -> FrameSlotCallRefLowerer(
        context = context,
        functionReference = instruction.functionReference,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        state = state,
    )
    is ControlSuperInstruction.ReturnCall -> FrameSlotReturnCallLowerer(
        context = context,
        operands = instruction.operands,
        functionIndex = instruction.functionIndex,
        state = state,
    )
    is ControlSuperInstruction.ReturnCallIndirect -> FrameSlotReturnCallIndirectLowerer(
        context = context,
        elementIndex = instruction.elementIndex,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        tableIndex = instruction.tableIndex,
        state = state,
    )
    is ControlSuperInstruction.ReturnCallRef -> FrameSlotReturnCallRefLowerer(
        context = context,
        functionReference = instruction.functionReference,
        operands = instruction.operands,
        typeIndex = instruction.typeIndex,
        state = state,
    )
    is ControlSuperInstruction.If -> {
        FrameSlotIfLowerer(
            context = context,
            operand = instruction.operand,
            blockType = instruction.blockType,
            thenInstructions = instruction.thenInstructions,
            elseInstructions = instruction.elseInstructions,
            state = state,
            labels = labels,
        )
    }
    else -> null
}

private fun FrameSlotStructuredControlLowerer(
    context: PassContext,
    kind: LabelKind,
    functionType: FunctionType,
    instructions: List<Instruction>,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
    rewrite: (List<Instruction>) -> Instruction,
): List<Instruction>? {
    val baseHeight = state.stack.size - functionType.params.types.size
    if (baseHeight < 0) return null

    val resultSlots = state.resultRegionSlots(baseHeight, functionType.results.types.size)
    val branchSlots = when (kind) {
        LabelKind.Block -> resultSlots
        LabelKind.Loop -> state.stack.takeLast(functionType.params.types.size)
            .map(FrameSlotStackOperand::reservedSlot)
    }
    val label = LabelContext(
        kind = kind,
        baseHeight = baseHeight,
        branchSlots = branchSlots,
        fallthroughSlots = resultSlots,
        branchTypes = when (kind) {
            LabelKind.Block -> functionType.results.types
            LabelKind.Loop -> functionType.params.types
        },
        fallthroughTypes = functionType.results.types,
    )
    val innerState = FrameSlotForkState(state)
    val loopParamMaterializations = if (kind == LabelKind.Loop && functionType.params.types.isNotEmpty()) {
        FrameSlotMaterializeOperands(
            operands = innerState.stack.takeLast(functionType.params.types.size),
            state = innerState,
        ).first
    } else {
        emptyList()
    }
    val innerLabels = ArrayDeque(labels).apply {
        addLast(label)
    }
    val loweredInstructions = FrameSlotExpressionLowerer(context, instructions, innerState, innerLabels) ?: return null
    val finalizedInstructions = FrameSlotFinalizeReachableLabel(
        instructions = loweredInstructions,
        state = innerState,
        labels = innerLabels,
        label = label,
    ) ?: return null
    val outcome = FrameSlotStructuredOutcome(
        entryStack = state.stack,
        state = innerState,
        labels = innerLabels,
        outerLabelCount = labels.size,
        currentLabel = label,
        branchToCurrentContinues = kind != LabelKind.Loop,
    ) ?: return null

    state.stack.clear()
    state.stack.addAll(outcome.stack)
    state.rebuildLocalAliases()
    state.reachable = outcome.reachable
    state.rewindTemporaryAllocator(labels)

    return buildList {
        addAll(loopParamMaterializations)
        add(rewrite(finalizedInstructions))
    }
}

private fun FrameSlotIfLowerer(
    context: PassContext,
    operand: FusedOperand,
    blockType: BlockType,
    thenInstructions: List<Instruction>,
    elseInstructions: List<Instruction>?,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val functionType = context.blockType(blockType) ?: return null
    val baseHeight = state.stack.size - functionType.params.types.size
    if (baseHeight < 0) return null

    val resultSlots = state.resultRegionSlots(baseHeight, functionType.results.types.size)
    val entryStack = state.stack.toList()

    val branchEntryTemporaryHeight = state.allocator.temporaryHeightSnapshot()

    val thenLabel = LabelContext(
        kind = LabelKind.Block,
        baseHeight = baseHeight,
        branchSlots = resultSlots,
        fallthroughSlots = resultSlots,
        branchTypes = functionType.results.types,
        fallthroughTypes = functionType.results.types,
    )
    val thenState = FrameSlotForkState(state)
    val thenLabels = ArrayDeque(labels).apply {
        addLast(thenLabel)
    }
    val loweredThenInstructions = FrameSlotExpressionLowerer(context, thenInstructions, thenState, thenLabels) ?: return null
    val finalizedThenInstructions = FrameSlotFinalizeReachableLabel(
        instructions = loweredThenInstructions,
        state = thenState,
        labels = thenLabels,
        label = thenLabel,
    ) ?: return null
    val thenOutcome = FrameSlotStructuredOutcome(
        entryStack = entryStack,
        state = thenState,
        labels = thenLabels,
        outerLabelCount = labels.size,
        currentLabel = thenLabel,
        branchToCurrentContinues = true,
    ) ?: return null

    state.allocator.restoreTemporaryHeight(branchEntryTemporaryHeight)

    val loweredElseInput = elseInstructions ?: emptyList()
    val elseLabel = LabelContext(
        kind = LabelKind.Block,
        baseHeight = baseHeight,
        branchSlots = resultSlots,
        fallthroughSlots = resultSlots,
        branchTypes = functionType.results.types,
        fallthroughTypes = functionType.results.types,
    )
    val elseState = FrameSlotForkState(state)
    val elseLabels = ArrayDeque(labels).apply {
        addLast(elseLabel)
    }
    val loweredElseInstructions = FrameSlotExpressionLowerer(context, loweredElseInput, elseState, elseLabels) ?: return null
    val finalizedElseInstructions = FrameSlotFinalizeReachableLabel(
        instructions = loweredElseInstructions,
        state = elseState,
        labels = elseLabels,
        label = elseLabel,
    ) ?: return null
    val elseOutcome = FrameSlotStructuredOutcome(
        entryStack = entryStack,
        state = elseState,
        labels = elseLabels,
        outerLabelCount = labels.size,
        currentLabel = elseLabel,
        branchToCurrentContinues = true,
    ) ?: return null

    val mergedOutcome = when {
        thenOutcome.reachable && elseOutcome.reachable -> {
            if (thenOutcome.stack != elseOutcome.stack) return null
            thenOutcome
        }
        thenOutcome.reachable -> thenOutcome
        elseOutcome.reachable -> elseOutcome
        else -> StructuredOutcome(
            reachable = false,
            stack = emptyList(),
        )
    }

    state.stack.clear()
    state.stack.addAll(mergedOutcome.stack)
    state.rebuildLocalAliases()
    state.reachable = mergedOutcome.reachable
    state.rewindTemporaryAllocator(labels)

    return listOf(
        ControlSuperInstruction.If(
            operand = loweredOperand.lowered,
            blockType = blockType,
            thenInstructions = finalizedThenInstructions,
            elseInstructions = if (elseInstructions == null) null else finalizedElseInstructions,
        ),
    )
}

private fun FrameSlotTryTableLowerer(
    context: PassContext,
    instruction: ControlInstruction.TryTable,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val payloadDestinationSlots = instruction.handlers.map { handler ->
        val target = FrameSlotTargetLabel(labels, handler.labelIndex) ?: return null
        val payloadArity = context.catchHandlerPayloadArity(handler) ?: return null
        if (target.branchSlots.size != payloadArity) return null
        target.reachedByBranch = true
        target.branchSlots
    }
    val functionType = context.blockType(instruction.blockType) ?: return null

    return FrameSlotStructuredControlLowerer(
        context = context,
        kind = LabelKind.Block,
        functionType = functionType,
        instructions = instruction.instructions,
        state = state,
        labels = labels,
    ) { loweredInstructions ->
        instruction.copy(
            instructions = loweredInstructions,
            payloadDestinationSlots = payloadDestinationSlots,
        )
    }
}

private data class StructuredOutcome(
    val reachable: Boolean,
    val stack: List<FrameSlotStackOperand>,
)

private fun FrameSlotStructuredOutcome(
    entryStack: List<FrameSlotStackOperand>,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
    outerLabelCount: Int,
    currentLabel: LabelContext,
    branchToCurrentContinues: Boolean,
): StructuredOutcome? {
    if (state.reachable) {
        if (labels.size != outerLabelCount) return null
        return StructuredOutcome(
            reachable = true,
            stack = state.stack.toList(),
        )
    }

    if (labels.size != outerLabelCount + 1) return null

    val remainingLabel = labels.removeLast()
    if (remainingLabel !== currentLabel) return null

    if (!branchToCurrentContinues || !remainingLabel.reachedByBranch) {
        return StructuredOutcome(
            reachable = false,
            stack = emptyList(),
        )
    }

    return StructuredOutcome(
        reachable = true,
        stack = buildList {
            addAll(entryStack.take(remainingLabel.baseHeight))
            addAll(
                remainingLabel.fallthroughSlots.mapIndexed { index, slot ->
                    FrameSlotStackOperand(
                        type = remainingLabel.fallthroughTypes.getOrNull(index),
                        reservedSlot = slot,
                    )
                },
            )
        },
    )
}

private fun FrameSlotFinalizeReachableLabel(
    instructions: List<Instruction>,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
    label: LabelContext,
): List<Instruction>? {
    if (!state.reachable || labels.lastOrNull() !== label) {
        return instructions
    }

    labels.removeLast()
    val finalizedInstructions = FrameSlotFinalizeLabelFallthrough(label, state) ?: return null
    state.rewindTemporaryAllocator(labels)
    return instructions + finalizedInstructions
}

private fun FrameSlotFinalizeLabelFallthrough(
    label: LabelContext,
    state: FrameSlotState,
): List<Instruction>? {
    if (state.stack.size != label.baseHeight + label.fallthroughSlots.size) {
        return null
    }

    val currentResultOperands = state.stack.takeLast(label.fallthroughSlots.size)
    val (materializeResults, currentResultSlots) = FrameSlotMaterializeOperands(currentResultOperands, state)
    val lowered = buildList {
        addAll(materializeResults)
        addAll(FrameSlotCopyInstructions(currentResultSlots, label.fallthroughSlots))
    }

    state.stack.subList(label.baseHeight, state.stack.size).clear()
    state.stack.addAll(
        label.fallthroughSlots.mapIndexed { index, slot ->
            FrameSlotStackOperand(
                type = label.fallthroughTypes.getOrNull(index),
                reservedSlot = slot,
            )
        },
    )
    state.rebuildLocalAliases()

    return lowered
}

private fun FrameSlotEndBlockLowerer(
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    if (labels.isEmpty()) return null

    val label = labels.removeLast()
    return FrameSlotFinalizeLabelFallthrough(label, state)
}

private fun FrameSlotBrLowerer(
    instruction: ControlInstruction.Br,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val target = FrameSlotTargetLabel(labels, instruction.labelIndex) ?: return null

    val sourceOperands = state.branchSourceOperands(target.branchSlots.size) ?: return null
    val (materializeResults, sourceSlots) = FrameSlotMaterializeOperands(sourceOperands, state)
    target.reachedByBranch = true
    state.reachable = false

    return buildList {
        addAll(materializeResults)
        addAll(FrameSlotCopyInstructions(sourceSlots, target.branchSlots))
        add(instruction)
    }
}

private fun FrameSlotBrIfLowerer(
    operand: FusedOperand,
    labelIndex: Index.LabelIndex,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val target = FrameSlotTargetLabel(labels, labelIndex) ?: return null

    val sourceOperands = state.branchSourceOperands(target.branchSlots.size) ?: return null
    val (materializeResults, sourceSlots) = FrameSlotPlanMaterializedOperands(sourceOperands, state)
    target.reachedByBranch = true

    return listOf(
        ControlSuperInstruction.BrIf(
            operand = loweredOperand.lowered,
            labelIndex = labelIndex,
            takenInstructions = materializeResults + FrameSlotCopyInstructions(sourceSlots, target.branchSlots),
        ),
    )
}

private fun FrameSlotBrTableLowerer(
    operand: FusedOperand,
    labelIndices: List<Index.LabelIndex>,
    defaultLabelIndex: Index.LabelIndex,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val targets = labelIndices.map { labelIndex ->
        FrameSlotTargetLabel(labels, labelIndex) ?: return null
    }
    val defaultTarget = FrameSlotTargetLabel(labels, defaultLabelIndex) ?: return null
    val arity = (targets.firstOrNull() ?: defaultTarget).branchSlots.size
    if (targets.any { it.branchSlots.size != arity } || defaultTarget.branchSlots.size != arity) {
        return null
    }

    val sourceOperands = state.branchSourceOperands(arity) ?: return null
    val (materializeResults, sourceSlots) = FrameSlotMaterializeOperands(sourceOperands, state)
    targets.forEach { target ->
        target.reachedByBranch = true
    }
    defaultTarget.reachedByBranch = true
    state.reachable = false

    return listOf(
        ControlSuperInstruction.BrTable(
            operand = loweredOperand.lowered,
            labelIndices = labelIndices,
            defaultLabelIndex = defaultLabelIndex,
            takenInstructions = targets.map { target -> materializeResults + FrameSlotCopyInstructions(sourceSlots, target.branchSlots) },
            defaultTakenInstructions = materializeResults + FrameSlotCopyInstructions(sourceSlots, defaultTarget.branchSlots),
        ),
    )
}

private fun FrameSlotBrOnNullLowerer(
    labelIndex: Index.LabelIndex,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val operand = state.peekStackOperand() ?: return null
    val target = FrameSlotTargetLabel(labels, labelIndex) ?: return null
    val sourceOperands = state.branchSourceOperandsExcludingTop(target.branchSlots.size) ?: return null
    val (operandMaterialization, operandSlot) = FrameSlotReadableSlot(operand, state)
    val (materializeResults, sourceSlots) = FrameSlotPlanMaterializedOperands(sourceOperands, state)
    target.reachedByBranch = true

    return operandMaterialization + listOf(
        ControlSuperInstruction.BrOnNull(
            operand = FusedOperand.FrameSlot(operandSlot),
            labelIndex = labelIndex,
            takenInstructions = materializeResults + FrameSlotCopyInstructions(sourceSlots, target.branchSlots),
        ),
    )
}

private fun FrameSlotBrOnNonNullLowerer(
    labelIndex: Index.LabelIndex,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val operand = state.peekStackOperand() ?: return null
    val target = FrameSlotTargetLabel(labels, labelIndex) ?: return null
    val sourceOperands = state.branchSourceOperands(target.branchSlots.size) ?: return null
    val (operandMaterialization, operandSlot) = FrameSlotReadableSlot(operand, state)
    val (materializeResults, sourceSlots) = FrameSlotPlanMaterializedOperands(sourceOperands, state)
    state.popStackOperand() ?: return null
    target.reachedByBranch = true

    return operandMaterialization + listOf(
        ControlSuperInstruction.BrOnNonNull(
            operand = FusedOperand.FrameSlot(operandSlot),
            labelIndex = labelIndex,
            takenInstructions = materializeResults + FrameSlotCopyInstructions(sourceSlots, target.branchSlots),
        ),
    )
}

private fun FrameSlotBrOnCastLowerer(
    instruction: ControlInstruction.BrOnCast,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val operand = state.peekStackOperand() ?: return null
    val target = FrameSlotTargetLabel(labels, instruction.labelIndex) ?: return null
    val sourceOperands = state.branchSourceOperands(target.branchSlots.size) ?: return null
    val (operandMaterialization, operandSlot) = FrameSlotReadableSlot(operand, state)
    val (materializeResults, sourceSlots) = FrameSlotPlanMaterializedOperands(sourceOperands, state)
    target.reachedByBranch = true

    return operandMaterialization + listOf(
        ControlSuperInstruction.BrOnCast(
            operand = FusedOperand.FrameSlot(operandSlot),
            labelIndex = instruction.labelIndex,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
            takenInstructions = materializeResults + FrameSlotCopyInstructions(sourceSlots, target.branchSlots),
        ),
    )
}

private fun FrameSlotBrOnCastFailLowerer(
    instruction: ControlInstruction.BrOnCastFail,
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val operand = state.peekStackOperand() ?: return null
    val target = FrameSlotTargetLabel(labels, instruction.labelIndex) ?: return null
    val sourceOperands = state.branchSourceOperands(target.branchSlots.size) ?: return null
    val (operandMaterialization, operandSlot) = FrameSlotReadableSlot(operand, state)
    val (materializeResults, sourceSlots) = FrameSlotPlanMaterializedOperands(sourceOperands, state)
    target.reachedByBranch = true

    return operandMaterialization + listOf(
        ControlSuperInstruction.BrOnCastFail(
            operand = FusedOperand.FrameSlot(operandSlot),
            labelIndex = instruction.labelIndex,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
            takenInstructions = materializeResults + FrameSlotCopyInstructions(sourceSlots, target.branchSlots),
        ),
    )
}

private fun FrameSlotReturnLowerer(
    state: FrameSlotState,
    labels: ArrayDeque<LabelContext>,
): List<Instruction>? {
    val returnSlots = labels.firstOrNull()?.branchSlots ?: return null
    val sourceOperands = state.branchSourceOperands(returnSlots.size) ?: return null
    val (materializeResults, sourceSlots) = FrameSlotMaterializeOperands(sourceOperands, state)
    state.reachable = false

    return buildList {
        addAll(materializeResults)
        addAll(FrameSlotCopyInstructions(sourceSlots, returnSlots))
        add(ControlInstruction.Return)
    }
}

private fun FrameSlotThrowLowerer(
    context: PassContext,
    instruction: ControlInstruction.Throw,
    state: FrameSlotState,
): List<Instruction>? {
    val payloadArity = context.tagType(instruction.tagIndex)?.params?.types?.size ?: return null
    val loweredOperands = FrameSlotStackOperands(payloadArity, state) ?: return null
    val (payloadMaterialization, payloadSlots) = FrameSlotOperandSlots(
        operands = loweredOperands.lowered,
        sources = loweredOperands.sources,
        state = state,
    ) ?: return null
    state.reachable = false

    return buildList {
        addAll(payloadMaterialization)
        add(
            ControlSuperInstruction.Throw(
                tagIndex = instruction.tagIndex,
                payloads = payloadSlots.map { slot -> FusedOperand.FrameSlot(slot) },
            ),
        )
    }
}

private fun FrameSlotThrowRefLowerer(
    state: FrameSlotState,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    state.reachable = false

    return listOf(
        ControlSuperInstruction.ThrowRef(
            exceptionReference = loweredOperand.lowered,
        ),
    )
}

private fun FrameSlotCallLowerer(
    context: PassContext,
    operands: List<FusedOperand>?,
    functionIndex: Index.FunctionIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val functionType = context.functionTypes.getOrNull(functionIndex.idx)?.functionType() ?: return null
    val loweredOperands = operands?.let { explicitOperands ->
        FrameSlotOperandsLowerer(explicitOperands, state)
    } ?: FrameSlotStackOperands(functionType.params.types.size, state)
    loweredOperands ?: return null

    val callFrameSlot = FrameSlotCallFrameSlot(state)
    val interfaceSlotCount = FrameSlotCallInterfaceSlotCount(
        paramCount = functionType.params.types.size,
        resultCount = functionType.results.types.size,
    )
    FrameSlotReserveCallInterfaceSlots(state, callFrameSlot, interfaceSlotCount)
    val preparedOperands = FrameSlotPrepareCallOperands(
        operands = loweredOperands.lowered,
        destinationSlots = FrameSlotCallOperandSlots(
            paramCount = functionType.params.types.size,
            callFrameSlot = callFrameSlot,
        ),
    )
    val resultSlots = FrameSlotCallResultSlots(functionType.results.types, callFrameSlot)
    FrameSlotPushResultOperands(
        state = state,
        resultTypes = functionType.results.types,
        resultSlots = resultSlots,
    )

    return buildList {
        addAll(preparedOperands.instructions)
        add(
            ControlSuperInstruction.Call(
                operands = emptyList(),
                functionIndex = functionIndex,
                resultSlots = resultSlots,
                callFrameSlot = callFrameSlot,
            ),
        )
    }
}

private fun FrameSlotCallIndirectLowerer(
    context: PassContext,
    elementIndex: FusedOperand?,
    operands: List<FusedOperand>?,
    typeIndex: Index.TypeIndex,
    tableIndex: Index.TableIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val functionType = context.functionType(typeIndex) ?: return null
    val loweredElementIndex = elementIndex?.let { explicitElementIndex ->
        FrameSlotOperandLowerer(explicitElementIndex, state)
    } ?: FrameSlotStackOperands(1, state)?.let { lowered ->
        LoweredOperand(
            lowered = lowered.lowered.single(),
            consumed = lowered.consumed,
        )
    }
    loweredElementIndex ?: return null
    val loweredOperands = operands?.let { explicitOperands ->
        FrameSlotOperandsLowerer(explicitOperands, state)
    } ?: FrameSlotStackOperands(functionType.params.types.size, state)
    loweredOperands ?: return null
    val callFrameSlot = FrameSlotCallFrameSlot(state)
    val interfaceSlotCount = FrameSlotCallInterfaceSlotCount(
        paramCount = functionType.params.types.size,
        resultCount = functionType.results.types.size,
    )
    FrameSlotReserveCallInterfaceSlots(state, callFrameSlot, interfaceSlotCount)
    val callOperandSlots = FrameSlotCallOperandSlots(
        paramCount = functionType.params.types.size,
        callFrameSlot = callFrameSlot,
    )
    val (prepareElementIndex, preparedElementIndex) = FrameSlotPrepareCallTargetOperand(
        operand = loweredElementIndex.lowered,
        overwrittenSlots = callOperandSlots,
        state = state,
    )
    val preparedOperands = FrameSlotPrepareCallOperands(
        operands = loweredOperands.lowered,
        destinationSlots = callOperandSlots,
    )
    val resultSlots = FrameSlotCallResultSlots(functionType.results.types, callFrameSlot)
    FrameSlotPushResultOperands(
        state = state,
        resultTypes = functionType.results.types,
        resultSlots = resultSlots,
    )

    return buildList {
        addAll(prepareElementIndex)
        addAll(preparedOperands.instructions)
        add(
            ControlSuperInstruction.CallIndirect(
                elementIndex = preparedElementIndex,
                operands = emptyList(),
                typeIndex = typeIndex,
                tableIndex = tableIndex,
                resultSlots = resultSlots,
                callFrameSlot = callFrameSlot,
            ),
        )
    }
}

private fun FrameSlotCallRefLowerer(
    context: PassContext,
    functionReference: FusedOperand?,
    operands: List<FusedOperand>?,
    typeIndex: Index.TypeIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val functionType = context.functionType(typeIndex) ?: return null
    val loweredFunctionReference = functionReference?.let { explicitFunctionReference ->
        FrameSlotOperandLowerer(explicitFunctionReference, state)
    } ?: FrameSlotStackOperands(1, state)?.let { lowered ->
        LoweredOperand(
            lowered = lowered.lowered.single(),
            consumed = lowered.consumed,
        )
    }
    loweredFunctionReference ?: return null
    val loweredOperands = operands?.let { explicitOperands ->
        FrameSlotOperandsLowerer(explicitOperands, state)
    } ?: FrameSlotStackOperands(functionType.params.types.size, state)
    loweredOperands ?: return null
    val callFrameSlot = FrameSlotCallFrameSlot(state)
    val interfaceSlotCount = FrameSlotCallInterfaceSlotCount(
        paramCount = functionType.params.types.size,
        resultCount = functionType.results.types.size,
    )
    FrameSlotReserveCallInterfaceSlots(state, callFrameSlot, interfaceSlotCount)
    val callOperandSlots = FrameSlotCallOperandSlots(
        paramCount = functionType.params.types.size,
        callFrameSlot = callFrameSlot,
    )
    val (prepareFunctionReference, preparedFunctionReference) = FrameSlotPrepareCallTargetOperand(
        operand = loweredFunctionReference.lowered,
        overwrittenSlots = callOperandSlots,
        state = state,
    )
    val preparedOperands = FrameSlotPrepareCallOperands(
        operands = loweredOperands.lowered,
        destinationSlots = callOperandSlots,
    )
    val resultSlots = FrameSlotCallResultSlots(functionType.results.types, callFrameSlot)
    FrameSlotPushResultOperands(
        state = state,
        resultTypes = functionType.results.types,
        resultSlots = resultSlots,
    )

    return buildList {
        addAll(prepareFunctionReference)
        addAll(preparedOperands.instructions)
        add(
            ControlSuperInstruction.CallRef(
                functionReference = preparedFunctionReference,
                operands = emptyList(),
                typeIndex = typeIndex,
                resultSlots = resultSlots,
                callFrameSlot = callFrameSlot,
            ),
        )
    }
}

private fun FrameSlotReturnCallLowerer(
    context: PassContext,
    operands: List<FusedOperand>?,
    functionIndex: Index.FunctionIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val functionType = context.functionTypes.getOrNull(functionIndex.idx)?.functionType() ?: return null
    val loweredOperands = operands?.let { explicitOperands ->
        FrameSlotOperandsLowerer(explicitOperands, state)
    } ?: FrameSlotStackOperands(functionType.params.types.size, state)
    loweredOperands ?: return null
    state.reachable = false

    return listOf(
        ControlSuperInstruction.ReturnCall(
            operands = loweredOperands.lowered,
            functionIndex = functionIndex,
        ),
    )
}

private fun FrameSlotReturnCallIndirectLowerer(
    context: PassContext,
    elementIndex: FusedOperand?,
    operands: List<FusedOperand>?,
    typeIndex: Index.TypeIndex,
    tableIndex: Index.TableIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val functionType = context.functionType(typeIndex) ?: return null
    val loweredElementIndex = elementIndex?.let { explicitElementIndex ->
        FrameSlotOperandLowerer(explicitElementIndex, state)
    } ?: FrameSlotStackOperands(1, state)?.let { lowered ->
        LoweredOperand(
            lowered = lowered.lowered.single(),
            consumed = lowered.consumed,
        )
    }
    loweredElementIndex ?: return null
    val loweredOperands = operands?.let { explicitOperands ->
        FrameSlotOperandsLowerer(explicitOperands, state)
    } ?: FrameSlotStackOperands(functionType.params.types.size, state)
    loweredOperands ?: return null
    state.reachable = false

    return listOf(
        ControlSuperInstruction.ReturnCallIndirect(
            elementIndex = loweredElementIndex.lowered,
            operands = loweredOperands.lowered,
            typeIndex = typeIndex,
            tableIndex = tableIndex,
        ),
    )
}

private fun FrameSlotReturnCallRefLowerer(
    context: PassContext,
    functionReference: FusedOperand?,
    operands: List<FusedOperand>?,
    typeIndex: Index.TypeIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val functionType = context.functionType(typeIndex) ?: return null
    val loweredFunctionReference = functionReference?.let { explicitFunctionReference ->
        FrameSlotOperandLowerer(explicitFunctionReference, state)
    } ?: FrameSlotStackOperands(1, state)?.let { lowered ->
        LoweredOperand(
            lowered = lowered.lowered.single(),
            consumed = lowered.consumed,
        )
    }
    loweredFunctionReference ?: return null
    val loweredOperands = operands?.let { explicitOperands ->
        FrameSlotOperandsLowerer(explicitOperands, state)
    } ?: FrameSlotStackOperands(functionType.params.types.size, state)
    loweredOperands ?: return null
    state.reachable = false

    return listOf(
        ControlSuperInstruction.ReturnCallRef(
            functionReference = loweredFunctionReference.lowered,
            operands = loweredOperands.lowered,
            typeIndex = typeIndex,
        ),
    )
}

private fun FrameSlotLocalGetLowerer(
    localIdx: Index.LocalIndex,
    state: FrameSlotState,
): List<Instruction> {
    state.pushStackOperand(
        FrameSlotStackOperand(
            type = state.localType(localIdx.idx),
            reservedSlot = state.allocator.allocateTemporarySlot(),
            localAlias = localIdx.idx,
        ),
    )
    return emptyList()
}

private fun FrameSlotLocalSetLowerer(
    operand: FusedOperand,
    localIdx: Index.LocalIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val preserveAliases = FrameSlotPreserveOverwrittenLocalSlot(
        localIndex = localIdx.idx,
        newValue = loweredOperand.lowered,
        state = state,
    )

    return buildList {
        addAll(preserveAliases)
        add(
            VariableSuperInstruction.LocalSet(
                operand = loweredOperand.lowered,
                localIdx = localIdx,
            ),
        )
    }
}

private fun FrameSlotLocalTeeLowerer(
    operand: FusedOperand,
    localIdx: Index.LocalIndex,
    state: FrameSlotState,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val preserveAliases = FrameSlotPreserveOverwrittenLocalSlot(
        localIndex = localIdx.idx,
        newValue = loweredOperand.lowered,
        state = state,
    )
    state.pushStackOperand(
        FrameSlotStackOperandForTeeResult(
            operand = operand,
            loweredOperand = loweredOperand,
            localIdx = localIdx,
            state = state,
        ),
    )

    return buildList {
        addAll(preserveAliases)
        add(
            VariableSuperInstruction.LocalSet(
                operand = loweredOperand.lowered,
                localIdx = localIdx,
            ),
        )
    }
}

private fun FrameSlotSelectLowerer(
    instruction: ParametricSuperInstruction.Select,
    state: FrameSlotState,
): List<Instruction>? {
    val const = FrameSlotOperandLowerer(instruction.const, state) ?: return null
    val val2 = FrameSlotOperandLowerer(instruction.val2, state) ?: return null
    val val1 = FrameSlotOperandLowerer(instruction.val1, state) ?: return null
    val destination = FrameSlotDestinationLowerer(instruction.destination, state) ?: return null

    return listOf(
        instruction.copy(
            const = const.lowered,
            val1 = val1.lowered,
            val2 = val2.lowered,
            destination = destination.lowered(val1.consumed + val2.consumed),
        ),
    )
}

private fun FrameSlotRawSelectLowerer(
    state: FrameSlotState,
): List<Instruction>? = FrameSlotSelectLowerer(
    instruction = ParametricSuperInstruction.Select(
        const = FusedOperand.ValueStack,
        val1 = FusedOperand.ValueStack,
        val2 = FusedOperand.ValueStack,
        destination = FusedDestination.ValueStack,
    ),
    state = state,
)

private fun FrameSlotGlobalGetLowerer(
    globalIdx: Index.GlobalIndex,
    state: FrameSlotState,
): List<Instruction> {
    val slot = state.allocator.allocateTemporarySlot()
    state.pushStackOperand(
        FrameSlotStackOperand(
            type = null,
            reservedSlot = slot,
        ),
    )

    return listOf(
        VariableSuperInstruction.GlobalGet(
            globalIdx = globalIdx,
            destination = FusedDestination.FrameSlot(slot),
        ),
    )
}

private inline fun <T : Instruction> FrameSlotConstantLowerer(
    constant: FusedOperand,
    destination: FusedDestination,
    state: FrameSlotState,
    rewrite: (FusedDestination) -> T,
): List<Instruction>? = when (destination) {
    FusedDestination.ValueStack -> {
        state.pushStackOperand(
            FrameSlotStackOperand(
                type = FrameSlotConstantValueType(constant),
                reservedSlot = state.allocator.allocateTemporarySlot(),
                immediate = constant,
            ),
        )
        emptyList()
    }
    else -> FrameSlotDestinationOnlyLowerer(destination, state) { loweredDestination ->
        rewrite(loweredDestination)
    }
}

private inline fun <T : Instruction> FrameSlotDestinationOnlyLowerer(
    destination: FusedDestination,
    state: FrameSlotState,
    rewrite: (FusedDestination) -> T,
): List<Instruction>? {
    val loweredDestination = FrameSlotDestinationLowerer(destination, state) ?: return null
    return listOf(rewrite(loweredDestination.lowered(emptyList())))
}

private inline fun <T : Instruction> FrameSlotOperandListLowerer(
    operands: List<FusedOperand>,
    state: FrameSlotState,
    rewrite: (List<FusedOperand>, List<Int>) -> T,
): List<Instruction>? {
    val loweredOperands = FrameSlotOperandsLowerer(operands, state) ?: return null
    return listOf(rewrite(loweredOperands.lowered, loweredOperands.consumed))
}

private inline fun <T : Instruction> FrameSlotUnaryLowerer(
    operand: FusedOperand,
    destination: FusedDestination,
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedDestination) -> T,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val loweredDestination = FrameSlotDestinationLowerer(destination, state) ?: return null

    return listOf(
        rewrite(
            loweredOperand.lowered,
            loweredDestination.lowered(loweredOperand.consumed),
        ),
    )
}

private fun FrameSlotBitcastLowerer(
    operand: FusedOperand,
    destination: FusedDestination,
    resultType: ValueType,
    state: FrameSlotState,
): List<Instruction>? {
    val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
    val loweredDestination = FrameSlotDestinationLowerer(
        destination = destination,
        state = state,
        valueType = resultType,
    ) ?: return null
    val loweredBitcastDestination = loweredDestination.lowered(loweredOperand.consumed)
    val destinationSlot = (loweredBitcastDestination as? FusedDestination.FrameSlot)?.offset ?: return null

    return when (val lowered = loweredOperand.lowered) {
        is FusedOperand.FrameSlot -> {
            if (lowered.offset == destinationSlot) {
                emptyList()
            } else {
                FrameSlotCopyInstructions(
                    sourceSlots = listOf(lowered.offset),
                    destinationSlots = listOf(destinationSlot),
                )
            }
        }
        is FusedOperand.I32Const,
        is FusedOperand.I64Const,
        is FusedOperand.F32Const,
        is FusedOperand.F64Const,
        -> listOf(FrameSlotImmediateInstruction(lowered, destinationSlot))
        is FusedOperand.LocalGet,
        FusedOperand.ValueStack,
        -> error("unexpected lowered bitcast operand: $lowered")
    }
}

private inline fun <T : Instruction> FrameSlotBinaryLowerer(
    left: FusedOperand,
    right: FusedOperand,
    destination: FusedDestination,
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedOperand, FusedDestination) -> T,
): List<Instruction>? {
    val loweredLeft = FrameSlotOperandLowerer(left, state) ?: return null
    val loweredRight = FrameSlotOperandLowerer(right, state) ?: return null
    val loweredDestination = FrameSlotDestinationLowerer(destination, state) ?: return null

    return listOf(
        rewrite(
            loweredLeft.lowered,
            loweredRight.lowered,
            loweredDestination.lowered(loweredLeft.consumed + loweredRight.consumed),
        ),
    )
}

private inline fun <T : Instruction> FrameSlotBinaryOperandsLowerer(
    left: FusedOperand,
    right: FusedOperand,
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedOperand) -> T,
): List<Instruction>? {
    val loweredLeft = FrameSlotOperandLowerer(left, state) ?: return null
    val loweredRight = FrameSlotOperandLowerer(right, state) ?: return null

    return listOf(
        rewrite(
            loweredLeft.lowered,
            loweredRight.lowered,
        ),
    )
}

private inline fun <T : Instruction> FrameSlotBinaryDualDestinationLowerer(
    left: FusedOperand,
    right: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedOperand, FusedDestination, FusedDestination) -> T,
): List<Instruction>? {
    val loweredLeft = FrameSlotOperandLowerer(left, state) ?: return null
    val loweredRight = FrameSlotOperandLowerer(right, state) ?: return null
    val loweredDestinations = FrameSlotMultiDestinationLowerer(
        destinations = listOf(destinationLow, destinationHigh),
        state = state,
    ) ?: return null

    return listOf(
        rewrite(
            loweredLeft.lowered,
            loweredRight.lowered,
            loweredDestinations[0],
            loweredDestinations[1],
        ),
    )
}

private inline fun <T : Instruction> FrameSlotQuadOperandDualDestinationLowerer(
    operand1: FusedOperand,
    operand2: FusedOperand,
    operand3: FusedOperand,
    operand4: FusedOperand,
    destinationLow: FusedDestination,
    destinationHigh: FusedDestination,
    state: FrameSlotState,
    rewrite: (
        FusedOperand,
        FusedOperand,
        FusedOperand,
        FusedOperand,
        FusedDestination,
        FusedDestination,
    ) -> T,
): List<Instruction>? {
    val loweredOperand1 = FrameSlotOperandLowerer(operand1, state) ?: return null
    val loweredOperand2 = FrameSlotOperandLowerer(operand2, state) ?: return null
    val loweredOperand3 = FrameSlotOperandLowerer(operand3, state) ?: return null
    val loweredOperand4 = FrameSlotOperandLowerer(operand4, state) ?: return null
    val loweredDestinations = FrameSlotMultiDestinationLowerer(
        destinations = listOf(destinationLow, destinationHigh),
        state = state,
    ) ?: return null

    return listOf(
        rewrite(
            loweredOperand1.lowered,
            loweredOperand2.lowered,
            loweredOperand3.lowered,
            loweredOperand4.lowered,
            loweredDestinations[0],
            loweredDestinations[1],
        ),
    )
}

private fun FrameSlotMultiDestinationLowerer(
    destinations: List<FusedDestination>,
    state: FrameSlotState,
): List<FusedDestination>? = destinations.map { destination ->
    when (destination) {
        is FusedDestination.FrameSlot,
        -> destination
        is FusedDestination.LocalSet -> FusedDestination.FrameSlot(state.localSlot(destination.index.idx))
        FusedDestination.ValueStack -> {
            val slot = state.allocator.allocateTemporarySlot()
            state.pushStackOperand(
                FrameSlotStackOperand(
                    type = null,
                    reservedSlot = slot,
                ),
            )
            FusedDestination.FrameSlot(slot)
        }
    }
}

private fun FrameSlotReferenceInstructionLowerer(
    instruction: ReferenceSuperInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is ReferenceSuperInstruction.RefCast -> FrameSlotUnaryLowerer(instruction.reference, instruction.destination, state) { reference, destination ->
        instruction.copy(reference = reference, destination = destination)
    }
    is ReferenceSuperInstruction.RefEq -> FrameSlotBinaryLowerer(instruction.reference1, instruction.reference2, instruction.destination, state) { reference1, reference2, destination ->
        instruction.copy(reference1 = reference1, reference2 = reference2, destination = destination)
    }
    is ReferenceSuperInstruction.RefIsNull -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is ReferenceSuperInstruction.RefAsNonNull -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is ReferenceSuperInstruction.RefFunc -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is ReferenceSuperInstruction.RefNull -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is ReferenceSuperInstruction.RefTest -> FrameSlotUnaryLowerer(instruction.reference, instruction.destination, state) { reference, destination ->
        instruction.copy(reference = reference, destination = destination)
    }
}

private fun FrameSlotTableInstructionLowerer(
    instruction: TableSuperInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is TableSuperInstruction.TableGet -> FrameSlotUnaryLowerer(instruction.elementIndex, instruction.destination, state) { elementIndex, destination ->
        instruction.copy(elementIndex = elementIndex, destination = destination)
    }
    is TableSuperInstruction.TableSet -> FrameSlotBinaryOperandsLowerer(instruction.value, instruction.elementIdx, state) { value, elementIdx ->
        instruction.copy(value = value, elementIdx = elementIdx)
    }
    is TableSuperInstruction.TableCopy -> FrameSlotOperandListLowerer(
        operands = listOf(instruction.elementsToCopy, instruction.srcOffset, instruction.dstOffset),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToCopy = operands[0],
            srcOffset = operands[1],
            dstOffset = operands[2],
        )
    }
    is TableSuperInstruction.TableFill -> FrameSlotOperandListLowerer(
        operands = listOf(instruction.elementsToFill, instruction.fillValue, instruction.tableOffset),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToFill = operands[0],
            fillValue = operands[1],
            tableOffset = operands[2],
        )
    }
    is TableSuperInstruction.TableGrow -> FrameSlotBinaryLowerer(instruction.elementsToAdd, instruction.referenceValue, instruction.destination, state) { elementsToAdd, referenceValue, destination ->
        instruction.copy(elementsToAdd = elementsToAdd, referenceValue = referenceValue, destination = destination)
    }
    is TableSuperInstruction.TableInit -> FrameSlotOperandListLowerer(
        operands = listOf(instruction.elementsToInitialise, instruction.segmentOffset, instruction.tableOffset),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToInitialise = operands[0],
            segmentOffset = operands[1],
            tableOffset = operands[2],
        )
    }
    is TableSuperInstruction.TableSize -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
}

private fun FrameSlotAggregateInstructionLowerer(
    context: PassContext,
    instruction: AggregateSuperInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is AggregateSuperInstruction.ArrayCopy -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.elementsToCopy,
            instruction.sourceOffset,
            instruction.sourceAddress,
            instruction.destinationOffset,
            instruction.destinationAddress,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToCopy = operands[0],
            sourceOffset = operands[1],
            sourceAddress = operands[2],
            destinationOffset = operands[3],
            destinationAddress = operands[4],
        )
    }
    is AggregateSuperInstruction.ArrayFill -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.elementsToFill,
            instruction.fillValue,
            instruction.arrayElementOffset,
            instruction.address,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToFill = operands[0],
            fillValue = operands[1],
            arrayElementOffset = operands[2],
            address = operands[3],
        )
    }
    is AggregateSuperInstruction.ArrayGet -> FrameSlotBinaryLowerer(instruction.address, instruction.field, instruction.destination, state) { address, field, destination ->
        instruction.copy(address = address, field = field, destination = destination)
    }
    is AggregateSuperInstruction.ArrayGetSigned -> FrameSlotBinaryLowerer(instruction.address, instruction.field, instruction.destination, state) { address, field, destination ->
        instruction.copy(address = address, field = field, destination = destination)
    }
    is AggregateSuperInstruction.ArrayGetUnsigned -> FrameSlotBinaryLowerer(instruction.address, instruction.field, instruction.destination, state) { address, field, destination ->
        instruction.copy(address = address, field = field, destination = destination)
    }
    is AggregateSuperInstruction.ArrayLen -> FrameSlotUnaryLowerer(instruction.address, instruction.destination, state) { address, destination ->
        instruction.copy(address = address, destination = destination)
    }
    is AggregateSuperInstruction.ArrayNew -> FrameSlotBinaryLowerer(instruction.size, instruction.value, instruction.destination, state) { size, value, destination ->
        instruction.copy(size = size, value = value, destination = destination)
    }
    is AggregateSuperInstruction.ArrayNewDefault -> FrameSlotUnaryLowerer(instruction.size, instruction.destination, state) { size, destination ->
        instruction.copy(size = size, destination = destination)
    }
    is AggregateSuperInstruction.ArrayNewData -> FrameSlotBinaryLowerer(instruction.arrayLength, instruction.sourceOffset, instruction.destination, state) { arrayLength, sourceOffset, destination ->
        instruction.copy(sourceOffset = sourceOffset, arrayLength = arrayLength, destination = destination)
    }
    is AggregateSuperInstruction.ArrayNewElement -> FrameSlotBinaryLowerer(instruction.arrayLength, instruction.sourceOffset, instruction.destination, state) { arrayLength, sourceOffset, destination ->
        instruction.copy(sourceOffset = sourceOffset, arrayLength = arrayLength, destination = destination)
    }
    is AggregateSuperInstruction.ArrayNewFixed -> {
        val values = FrameSlotStackOperands(instruction.size, state) ?: return null
        val destination = FrameSlotDestinationLowerer(instruction.destination, state) ?: return null
        val (materializeValues, valueSlots) = FrameSlotOperandSlots(values.lowered, values.sources, state) ?: return null

        materializeValues + listOf(
            instruction.copy(
                destination = destination.lowered(values.consumed),
                valueSlots = valueSlots,
            ),
        )
    }
    is AggregateSuperInstruction.ArraySet -> FrameSlotOperandListLowerer(
        operands = listOf(instruction.value, instruction.field, instruction.address),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            value = operands[0],
            field = operands[1],
            address = operands[2],
        )
    }
    is AggregateSuperInstruction.ArrayInitData -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.elementsToCopy,
            instruction.sourceOffset,
            instruction.destinationOffset,
            instruction.address,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToCopy = operands[0],
            sourceOffset = operands[1],
            destinationOffset = operands[2],
            address = operands[3],
        )
    }
    is AggregateSuperInstruction.ArrayInitElement -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.elementsToCopy,
            instruction.sourceOffset,
            instruction.destinationOffset,
            instruction.address,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            elementsToCopy = operands[0],
            sourceOffset = operands[1],
            destinationOffset = operands[2],
            address = operands[3],
        )
    }
    is AggregateSuperInstruction.RefI31 -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is AggregateSuperInstruction.I31GetSigned -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is AggregateSuperInstruction.I31GetUnsigned -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is AggregateSuperInstruction.AnyConvertExtern -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is AggregateSuperInstruction.ExternConvertAny -> FrameSlotUnaryLowerer(instruction.value, instruction.destination, state) { value, destination ->
        instruction.copy(value = value, destination = destination)
    }
    is AggregateSuperInstruction.StructGet -> FrameSlotUnaryLowerer(instruction.address, instruction.destination, state) { address, destination ->
        instruction.copy(address = address, destination = destination)
    }
    is AggregateSuperInstruction.StructGetSigned -> FrameSlotUnaryLowerer(instruction.address, instruction.destination, state) { address, destination ->
        instruction.copy(address = address, destination = destination)
    }
    is AggregateSuperInstruction.StructGetUnsigned -> FrameSlotUnaryLowerer(instruction.address, instruction.destination, state) { address, destination ->
        instruction.copy(address = address, destination = destination)
    }
    is AggregateSuperInstruction.StructNew -> {
        val fieldCount = context.structFieldCount(instruction.typeIndex) ?: return null
        val fields = FrameSlotStackOperands(fieldCount, state) ?: return null
        val destination = FrameSlotDestinationLowerer(instruction.destination, state) ?: return null
        val (materializeFields, fieldSlots) = FrameSlotOperandSlots(fields.lowered, fields.sources, state) ?: return null

        materializeFields + listOf(
            instruction.copy(
                destination = destination.lowered(fields.consumed),
                fieldSlots = fieldSlots,
            ),
        )
    }
    is AggregateSuperInstruction.StructNewDefault -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is AggregateSuperInstruction.StructSet -> FrameSlotBinaryOperandsLowerer(instruction.value, instruction.address, state) { value, address ->
        instruction.copy(value = value, address = address)
    }
}

private fun FrameSlotRawAggregateInstructionLowerer(
    context: PassContext,
    instruction: AggregateInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is AggregateInstruction.StructNew -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.StructNew(
            destination = FusedDestination.ValueStack,
            typeIndex = instruction.typeIndex,
        ),
        state = state,
    )
    is AggregateInstruction.StructNewDefault -> FrameSlotDestinationOnlyLowerer(FusedDestination.ValueStack, state) { destination ->
        AggregateSuperInstruction.StructNewDefault(
            destination = destination,
            typeIndex = instruction.typeIndex,
        )
    }
    is AggregateInstruction.StructGet -> FrameSlotRawUnaryLowerer(state) { address, destination ->
        AggregateSuperInstruction.StructGet(
            address = address,
            destination = destination,
            typeIndex = instruction.typeIndex,
            fieldIndex = instruction.fieldIndex,
        )
    }
    is AggregateInstruction.StructGetSigned -> FrameSlotRawUnaryLowerer(state) { address, destination ->
        AggregateSuperInstruction.StructGetSigned(
            address = address,
            destination = destination,
            typeIndex = instruction.typeIndex,
            fieldIndex = instruction.fieldIndex,
        )
    }
    is AggregateInstruction.StructGetUnsigned -> FrameSlotRawUnaryLowerer(state) { address, destination ->
        AggregateSuperInstruction.StructGetUnsigned(
            address = address,
            destination = destination,
            typeIndex = instruction.typeIndex,
            fieldIndex = instruction.fieldIndex,
        )
    }
    is AggregateInstruction.StructSet -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.StructSet(
            value = FusedOperand.ValueStack,
            address = FusedOperand.ValueStack,
            typeIndex = instruction.typeIndex,
            fieldIndex = instruction.fieldIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayNew -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayNew(
            size = FusedOperand.ValueStack,
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            typeIndex = instruction.typeIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayNewFixed -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayNewFixed(
            destination = FusedDestination.ValueStack,
            typeIndex = instruction.typeIndex,
            size = instruction.size.toInt(),
        ),
        state = state,
    )
    is AggregateInstruction.ArrayNewDefault -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayNewDefault(
            size = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            typeIndex = instruction.typeIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayNewData -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayNewData(
            sourceOffset = FusedOperand.ValueStack,
            arrayLength = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            typeIndex = instruction.typeIndex,
            dataIndex = instruction.dataIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayNewElement -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayNewElement(
            sourceOffset = FusedOperand.ValueStack,
            arrayLength = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            typeIndex = instruction.typeIndex,
            elementIndex = instruction.elementIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayGet -> FrameSlotRawBinaryLowerer(state) { address, field, destination ->
        AggregateSuperInstruction.ArrayGet(
            address = address,
            field = field,
            destination = destination,
            typeIndex = instruction.typeIndex,
        )
    }
    is AggregateInstruction.ArrayGetSigned -> FrameSlotRawBinaryLowerer(state) { address, field, destination ->
        AggregateSuperInstruction.ArrayGetSigned(
            address = address,
            field = field,
            destination = destination,
            typeIndex = instruction.typeIndex,
        )
    }
    is AggregateInstruction.ArrayGetUnsigned -> FrameSlotRawBinaryLowerer(state) { address, field, destination ->
        AggregateSuperInstruction.ArrayGetUnsigned(
            address = address,
            field = field,
            destination = destination,
            typeIndex = instruction.typeIndex,
        )
    }
    is AggregateInstruction.ArraySet -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArraySet(
            value = FusedOperand.ValueStack,
            field = FusedOperand.ValueStack,
            address = FusedOperand.ValueStack,
            typeIndex = instruction.typeIndex,
        ),
        state = state,
    )
    AggregateInstruction.ArrayLen -> FrameSlotRawUnaryLowerer(state) { address, destination ->
        AggregateSuperInstruction.ArrayLen(
            address = address,
            destination = destination,
        )
    }
    is AggregateInstruction.ArrayFill -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayFill(
            elementsToFill = FusedOperand.ValueStack,
            fillValue = FusedOperand.ValueStack,
            arrayElementOffset = FusedOperand.ValueStack,
            address = FusedOperand.ValueStack,
            typeIndex = instruction.typeIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayCopy -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayCopy(
            elementsToCopy = FusedOperand.ValueStack,
            sourceOffset = FusedOperand.ValueStack,
            sourceAddress = FusedOperand.ValueStack,
            destinationOffset = FusedOperand.ValueStack,
            destinationAddress = FusedOperand.ValueStack,
            sourceTypeIndex = instruction.sourceTypeIndex,
            destinationTypeIndex = instruction.destinationTypeIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayInitData -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayInitData(
            elementsToCopy = FusedOperand.ValueStack,
            sourceOffset = FusedOperand.ValueStack,
            destinationOffset = FusedOperand.ValueStack,
            address = FusedOperand.ValueStack,
            typeIndex = instruction.typeIndex,
            dataIndex = instruction.dataIndex,
        ),
        state = state,
    )
    is AggregateInstruction.ArrayInitElement -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ArrayInitElement(
            elementsToCopy = FusedOperand.ValueStack,
            sourceOffset = FusedOperand.ValueStack,
            destinationOffset = FusedOperand.ValueStack,
            address = FusedOperand.ValueStack,
            typeIndex = instruction.typeIndex,
            elementIndex = instruction.elementIndex,
        ),
        state = state,
    )
    AggregateInstruction.RefI31 -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.RefI31(
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
        ),
        state = state,
    )
    AggregateInstruction.I31GetSigned -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.I31GetSigned(
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
        ),
        state = state,
    )
    AggregateInstruction.I31GetUnsigned -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.I31GetUnsigned(
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
        ),
        state = state,
    )
    AggregateInstruction.AnyConvertExtern -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.AnyConvertExtern(
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
        ),
        state = state,
    )
    AggregateInstruction.ExternConvertAny -> FrameSlotAggregateInstructionLowerer(
        context = context,
        instruction = AggregateSuperInstruction.ExternConvertAny(
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
        ),
        state = state,
    )
}

private fun FrameSlotRawReferenceInstructionLowerer(
    instruction: ReferenceInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is ReferenceInstruction.RefNull -> FrameSlotDestinationOnlyLowerer(FusedDestination.ValueStack, state) { destination ->
        ReferenceSuperInstruction.RefNull(
            destination = destination,
            type = instruction.type,
        )
    }
    ReferenceInstruction.RefIsNull -> FrameSlotRawUnaryLowerer(state) { value, destination ->
        ReferenceSuperInstruction.RefIsNull(
            value = value,
            destination = destination,
        )
    }
    ReferenceInstruction.RefAsNonNull -> FrameSlotReferenceInstructionLowerer(
        instruction = ReferenceSuperInstruction.RefAsNonNull(
            value = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
        ),
        state = state,
    )
    is ReferenceInstruction.RefFunc -> FrameSlotDestinationOnlyLowerer(FusedDestination.ValueStack, state) { destination ->
        ReferenceSuperInstruction.RefFunc(
            destination = destination,
            funcIdx = instruction.funcIdx,
        )
    }
    ReferenceInstruction.RefEq -> FrameSlotRawBinaryLowerer(state) { reference1, reference2, destination ->
        ReferenceSuperInstruction.RefEq(
            reference1 = reference1,
            reference2 = reference2,
            destination = destination,
        )
    }
    is ReferenceInstruction.RefTest -> FrameSlotRawUnaryLowerer(state) { reference, destination ->
        ReferenceSuperInstruction.RefTest(
            reference = reference,
            destination = destination,
            referenceType = instruction.referenceType,
        )
    }
    is ReferenceInstruction.RefCast -> FrameSlotRawUnaryLowerer(state) { reference, destination ->
        ReferenceSuperInstruction.RefCast(
            reference = reference,
            destination = destination,
            referenceType = instruction.referenceType,
        )
    }
}

private fun FrameSlotRawTableInstructionLowerer(
    instruction: TableInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is TableInstruction.TableGet -> FrameSlotTableInstructionLowerer(
        instruction = TableSuperInstruction.TableGet(
            elementIndex = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            tableIdx = instruction.tableIdx,
        ),
        state = state,
    )
    is TableInstruction.TableSet -> FrameSlotTableInstructionLowerer(
        instruction = TableSuperInstruction.TableSet(
            value = FusedOperand.ValueStack,
            elementIdx = FusedOperand.ValueStack,
            tableIdx = instruction.tableIdx,
        ),
        state = state,
    )
    is TableInstruction.TableCopy -> FrameSlotTableInstructionLowerer(
        instruction = TableSuperInstruction.TableCopy(
            elementsToCopy = FusedOperand.ValueStack,
            srcOffset = FusedOperand.ValueStack,
            dstOffset = FusedOperand.ValueStack,
            srcTableIdx = instruction.srcTableIdx,
            destTableIdx = instruction.destTableIdx,
        ),
        state = state,
    )
    is TableInstruction.TableFill -> FrameSlotTableInstructionLowerer(
        instruction = TableSuperInstruction.TableFill(
            elementsToFill = FusedOperand.ValueStack,
            fillValue = FusedOperand.ValueStack,
            tableOffset = FusedOperand.ValueStack,
            tableIdx = instruction.tableIdx,
        ),
        state = state,
    )
    is TableInstruction.TableGrow -> FrameSlotTableInstructionLowerer(
        instruction = TableSuperInstruction.TableGrow(
            elementsToAdd = FusedOperand.ValueStack,
            referenceValue = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            tableIdx = instruction.tableIdx,
        ),
        state = state,
    )
    is TableInstruction.TableInit -> FrameSlotTableInstructionLowerer(
        instruction = TableSuperInstruction.TableInit(
            elementsToInitialise = FusedOperand.ValueStack,
            segmentOffset = FusedOperand.ValueStack,
            tableOffset = FusedOperand.ValueStack,
            elemIdx = instruction.elemIdx,
            tableIdx = instruction.tableIdx,
        ),
        state = state,
    )
    is TableInstruction.TableSize -> FrameSlotDestinationOnlyLowerer(FusedDestination.ValueStack, state) { destination ->
        TableSuperInstruction.TableSize(
            destination = destination,
            tableIdx = instruction.tableIdx,
        )
    }
    is TableInstruction.ElemDrop -> listOf(instruction)
}

private fun FrameSlotNumericInstructionLowerer(
    instruction: NumericSuperInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is NumericSuperInstruction.I32Const -> FrameSlotConstantLowerer(FusedOperand.I32Const(instruction.value), instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is NumericSuperInstruction.I64Const -> FrameSlotConstantLowerer(FusedOperand.I64Const(instruction.value), instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is NumericSuperInstruction.F32Const -> FrameSlotConstantLowerer(FusedOperand.F32Const(Float.fromBits(instruction.bits)), instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is NumericSuperInstruction.F64Const -> FrameSlotConstantLowerer(FusedOperand.F64Const(Double.fromBits(instruction.bits)), instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is NumericSuperInstruction.I32Add -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Sub -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Mul -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32DivS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32DivU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32RemS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32RemU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32And -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Or -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Xor -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Shl -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32ShrS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32ShrU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Rotl -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I32Rotr -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I64Add -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I64Sub -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I64Mul -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination ->
        instruction.copy(left = left, right = right, destination = destination)
    }
    is NumericSuperInstruction.I64Add128 -> FrameSlotQuadOperandDualDestinationLowerer(
        operand1 = instruction.leftLow,
        operand2 = instruction.leftHigh,
        operand3 = instruction.rightLow,
        operand4 = instruction.rightHigh,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
        state = state,
    ) { leftLow, leftHigh, rightLow, rightHigh, destinationLow, destinationHigh ->
        instruction.copy(
            leftLow = leftLow,
            leftHigh = leftHigh,
            rightLow = rightLow,
            rightHigh = rightHigh,
            destinationLow = destinationLow,
            destinationHigh = destinationHigh,
        )
    }
    is NumericSuperInstruction.I64Sub128 -> FrameSlotQuadOperandDualDestinationLowerer(
        operand1 = instruction.leftLow,
        operand2 = instruction.leftHigh,
        operand3 = instruction.rightLow,
        operand4 = instruction.rightHigh,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
        state = state,
    ) { leftLow, leftHigh, rightLow, rightHigh, destinationLow, destinationHigh ->
        instruction.copy(
            leftLow = leftLow,
            leftHigh = leftHigh,
            rightLow = rightLow,
            rightHigh = rightHigh,
            destinationLow = destinationLow,
            destinationHigh = destinationHigh,
        )
    }
    is NumericSuperInstruction.I64MulWideS -> FrameSlotBinaryDualDestinationLowerer(
        left = instruction.left,
        right = instruction.right,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
        state = state,
    ) { left, right, destinationLow, destinationHigh ->
        instruction.copy(
            left = left,
            right = right,
            destinationLow = destinationLow,
            destinationHigh = destinationHigh,
        )
    }
    is NumericSuperInstruction.I64MulWideU -> FrameSlotBinaryDualDestinationLowerer(
        left = instruction.left,
        right = instruction.right,
        destinationLow = instruction.destinationLow,
        destinationHigh = instruction.destinationHigh,
        state = state,
    ) { left, right, destinationLow, destinationHigh ->
        instruction.copy(
            left = left,
            right = right,
            destinationLow = destinationLow,
            destinationHigh = destinationHigh,
        )
    }
    is NumericSuperInstruction.I64DivS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64DivU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64RemS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64RemU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64And -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Or -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Xor -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Shl -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64ShrS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64ShrU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Rotl -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Rotr -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Add -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Sub -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Mul -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Div -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Min -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Max -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Copysign -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Add -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Sub -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Mul -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Div -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Min -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Max -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Copysign -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32Eqz -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Eqz -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Abs -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Neg -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Ceil -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Floor -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Trunc -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Nearest -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32Sqrt -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Abs -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Neg -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Ceil -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Floor -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Trunc -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Nearest -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64Sqrt -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32Clz -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32Ctz -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32Popcnt -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Clz -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Ctz -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Popcnt -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32Eq -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32Ne -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32LtS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32LtU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32GtS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32GtU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32LeS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32LeU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32GeS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I32GeU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Eq -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64Ne -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64LtS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64LtU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64GtS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64GtU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64LeS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64LeU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64GeS -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.I64GeU -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Eq -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Ne -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Lt -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Gt -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Le -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32Ge -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Eq -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Ne -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Lt -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Gt -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Le -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F64Ge -> FrameSlotBinaryLowerer(instruction.left, instruction.right, instruction.destination, state) { left, right, destination -> instruction.copy(left = left, right = right, destination = destination) }
    is NumericSuperInstruction.F32ConvertI32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32ConvertI32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32ConvertI64S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32ConvertI64U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32DemoteF64 -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F32ReinterpretI32 -> FrameSlotBitcastLowerer(
        operand = instruction.operand,
        destination = instruction.destination,
        resultType = ValueType.Number(NumberType.F32),
        state = state,
    )
    is NumericSuperInstruction.F64ConvertI32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64ConvertI32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64ConvertI64S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64ConvertI64U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64PromoteF32 -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.F64ReinterpretI64 -> FrameSlotBitcastLowerer(
        operand = instruction.operand,
        destination = instruction.destination,
        resultType = ValueType.Number(NumberType.F64),
        state = state,
    )
    is NumericSuperInstruction.I32Extend16S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32Extend8S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32ReinterpretF32 -> FrameSlotBitcastLowerer(
        operand = instruction.operand,
        destination = instruction.destination,
        resultType = ValueType.Number(NumberType.I32),
        state = state,
    )
    is NumericSuperInstruction.I32TruncF32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncF32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncF64S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncF64U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncSatF32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncSatF32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncSatF64S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32TruncSatF64U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I32WrapI64 -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Extend16S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Extend32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64Extend8S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64ExtendI32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64ExtendI32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64ReinterpretF64 -> FrameSlotBitcastLowerer(
        operand = instruction.operand,
        destination = instruction.destination,
        resultType = ValueType.Number(NumberType.I64),
        state = state,
    )
    is NumericSuperInstruction.I64TruncF32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncF32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncF64S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncF64U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncSatF32S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncSatF32U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncSatF64S -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
    is NumericSuperInstruction.I64TruncSatF64U -> FrameSlotUnaryLowerer(instruction.operand, instruction.destination, state) { operand, destination -> instruction.copy(operand = operand, destination = destination) }
}

private fun FrameSlotRawNumericInstructionLowerer(
    instruction: NumericInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is NumericInstruction.I32Const -> FrameSlotConstantLowerer(FusedOperand.I32Const(instruction.value), FusedDestination.ValueStack, state) { destination ->
        NumericSuperInstruction.I32Const(instruction.value, destination)
    }
    is NumericInstruction.I64Const -> FrameSlotConstantLowerer(FusedOperand.I64Const(instruction.value), FusedDestination.ValueStack, state) { destination ->
        NumericSuperInstruction.I64Const(instruction.value, destination)
    }
    is NumericInstruction.F32Const -> FrameSlotConstantLowerer(FusedOperand.F32Const(instruction.value), FusedDestination.ValueStack, state) { destination ->
        NumericSuperInstruction.F32Const(instruction.bits, destination)
    }
    is NumericInstruction.F64Const -> FrameSlotConstantLowerer(FusedOperand.F64Const(instruction.value), FusedDestination.ValueStack, state) { destination ->
        NumericSuperInstruction.F64Const(instruction.bits, destination)
    }

    is NumericInstruction.I32Eqz -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32Eqz(operand, destination) }
    is NumericInstruction.I64Eqz -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Eqz(operand, destination) }
    is NumericInstruction.I32Clz -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32Clz(operand, destination) }
    is NumericInstruction.I32Ctz -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32Ctz(operand, destination) }
    is NumericInstruction.I32Popcnt -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32Popcnt(operand, destination) }
    is NumericInstruction.I64Clz -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Clz(operand, destination) }
    is NumericInstruction.I64Ctz -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Ctz(operand, destination) }
    is NumericInstruction.I64Popcnt -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Popcnt(operand, destination) }
    is NumericInstruction.F32Abs -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Abs(operand, destination) }
    is NumericInstruction.F32Neg -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Neg(operand, destination) }
    is NumericInstruction.F32Ceil -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Ceil(operand, destination) }
    is NumericInstruction.F32Floor -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Floor(operand, destination) }
    is NumericInstruction.F32Trunc -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Trunc(operand, destination) }
    is NumericInstruction.F32Nearest -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Nearest(operand, destination) }
    is NumericInstruction.F32Sqrt -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32Sqrt(operand, destination) }
    is NumericInstruction.F64Abs -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Abs(operand, destination) }
    is NumericInstruction.F64Neg -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Neg(operand, destination) }
    is NumericInstruction.F64Ceil -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Ceil(operand, destination) }
    is NumericInstruction.F64Floor -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Floor(operand, destination) }
    is NumericInstruction.F64Trunc -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Trunc(operand, destination) }
    is NumericInstruction.F64Nearest -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Nearest(operand, destination) }
    is NumericInstruction.F64Sqrt -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64Sqrt(operand, destination) }
    is NumericInstruction.I32WrapI64 -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32WrapI64(operand, destination) }
    is NumericInstruction.I32TruncF32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncF32S(operand, destination) }
    is NumericInstruction.I32TruncF32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncF32U(operand, destination) }
    is NumericInstruction.I32TruncF64S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncF64S(operand, destination) }
    is NumericInstruction.I32TruncF64U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncF64U(operand, destination) }
    is NumericInstruction.I64ExtendI32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64ExtendI32S(operand, destination) }
    is NumericInstruction.I64ExtendI32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64ExtendI32U(operand, destination) }
    is NumericInstruction.I64TruncF32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncF32S(operand, destination) }
    is NumericInstruction.I64TruncF32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncF32U(operand, destination) }
    is NumericInstruction.I64TruncF64S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncF64S(operand, destination) }
    is NumericInstruction.I64TruncF64U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncF64U(operand, destination) }
    is NumericInstruction.F32ConvertI32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32ConvertI32S(operand, destination) }
    is NumericInstruction.F32ConvertI32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32ConvertI32U(operand, destination) }
    is NumericInstruction.F32ConvertI64S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32ConvertI64S(operand, destination) }
    is NumericInstruction.F32ConvertI64U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32ConvertI64U(operand, destination) }
    is NumericInstruction.F32DemoteF64 -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F32DemoteF64(operand, destination) }
    is NumericInstruction.F64ConvertI32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64ConvertI32S(operand, destination) }
    is NumericInstruction.F64ConvertI32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64ConvertI32U(operand, destination) }
    is NumericInstruction.F64ConvertI64S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64ConvertI64S(operand, destination) }
    is NumericInstruction.F64ConvertI64U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64ConvertI64U(operand, destination) }
    is NumericInstruction.F64PromoteF32 -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.F64PromoteF32(operand, destination) }
    is NumericInstruction.I32ReinterpretF32 -> FrameSlotBitcastLowerer(
        operand = FusedOperand.ValueStack,
        destination = FusedDestination.ValueStack,
        resultType = ValueType.Number(NumberType.I32),
        state = state,
    )
    is NumericInstruction.I64ReinterpretF64 -> FrameSlotBitcastLowerer(
        operand = FusedOperand.ValueStack,
        destination = FusedDestination.ValueStack,
        resultType = ValueType.Number(NumberType.I64),
        state = state,
    )
    is NumericInstruction.F32ReinterpretI32 -> FrameSlotBitcastLowerer(
        operand = FusedOperand.ValueStack,
        destination = FusedDestination.ValueStack,
        resultType = ValueType.Number(NumberType.F32),
        state = state,
    )
    is NumericInstruction.F64ReinterpretI64 -> FrameSlotBitcastLowerer(
        operand = FusedOperand.ValueStack,
        destination = FusedDestination.ValueStack,
        resultType = ValueType.Number(NumberType.F64),
        state = state,
    )
    is NumericInstruction.I32Extend8S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32Extend8S(operand, destination) }
    is NumericInstruction.I32Extend16S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32Extend16S(operand, destination) }
    is NumericInstruction.I64Extend8S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Extend8S(operand, destination) }
    is NumericInstruction.I64Extend16S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Extend16S(operand, destination) }
    is NumericInstruction.I64Extend32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64Extend32S(operand, destination) }
    is NumericInstruction.I32TruncSatF32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncSatF32S(operand, destination) }
    is NumericInstruction.I32TruncSatF32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncSatF32U(operand, destination) }
    is NumericInstruction.I32TruncSatF64S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncSatF64S(operand, destination) }
    is NumericInstruction.I32TruncSatF64U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I32TruncSatF64U(operand, destination) }
    is NumericInstruction.I64TruncSatF32S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncSatF32S(operand, destination) }
    is NumericInstruction.I64TruncSatF32U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncSatF32U(operand, destination) }
    is NumericInstruction.I64TruncSatF64S -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncSatF64S(operand, destination) }
    is NumericInstruction.I64TruncSatF64U -> FrameSlotRawNumericUnaryLowerer(state) { operand, destination -> NumericSuperInstruction.I64TruncSatF64U(operand, destination) }

    is NumericInstruction.I32Add -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Add(left, right, destination) }
    is NumericInstruction.I32Sub -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Sub(left, right, destination) }
    is NumericInstruction.I32Mul -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Mul(left, right, destination) }
    is NumericInstruction.I32DivS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32DivS(left, right, destination) }
    is NumericInstruction.I32DivU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32DivU(left, right, destination) }
    is NumericInstruction.I32RemS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32RemS(left, right, destination) }
    is NumericInstruction.I32RemU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32RemU(left, right, destination) }
    is NumericInstruction.I32And -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32And(left, right, destination) }
    is NumericInstruction.I32Or -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Or(left, right, destination) }
    is NumericInstruction.I32Xor -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Xor(left, right, destination) }
    is NumericInstruction.I32Shl -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Shl(left, right, destination) }
    is NumericInstruction.I32ShrS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32ShrS(left, right, destination) }
    is NumericInstruction.I32ShrU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32ShrU(left, right, destination) }
    is NumericInstruction.I32Rotl -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Rotl(left, right, destination) }
    is NumericInstruction.I32Rotr -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Rotr(left, right, destination) }
    is NumericInstruction.I64Add -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Add(left, right, destination) }
    is NumericInstruction.I64Sub -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Sub(left, right, destination) }
    is NumericInstruction.I64Mul -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Mul(left, right, destination) }
    is NumericInstruction.I64DivS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64DivS(left, right, destination) }
    is NumericInstruction.I64DivU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64DivU(left, right, destination) }
    is NumericInstruction.I64RemS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64RemS(left, right, destination) }
    is NumericInstruction.I64RemU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64RemU(left, right, destination) }
    is NumericInstruction.I64And -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64And(left, right, destination) }
    is NumericInstruction.I64Or -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Or(left, right, destination) }
    is NumericInstruction.I64Xor -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Xor(left, right, destination) }
    is NumericInstruction.I64Shl -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Shl(left, right, destination) }
    is NumericInstruction.I64ShrS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64ShrS(left, right, destination) }
    is NumericInstruction.I64ShrU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64ShrU(left, right, destination) }
    is NumericInstruction.I64Rotl -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Rotl(left, right, destination) }
    is NumericInstruction.I64Rotr -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Rotr(left, right, destination) }
    is NumericInstruction.F32Add -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Add(left, right, destination) }
    is NumericInstruction.F32Sub -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Sub(left, right, destination) }
    is NumericInstruction.F32Mul -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Mul(left, right, destination) }
    is NumericInstruction.F32Div -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Div(left, right, destination) }
    is NumericInstruction.F32Min -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Min(left, right, destination) }
    is NumericInstruction.F32Max -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Max(left, right, destination) }
    is NumericInstruction.F32Copysign -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Copysign(left, right, destination) }
    is NumericInstruction.F64Add -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Add(left, right, destination) }
    is NumericInstruction.F64Sub -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Sub(left, right, destination) }
    is NumericInstruction.F64Mul -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Mul(left, right, destination) }
    is NumericInstruction.F64Div -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Div(left, right, destination) }
    is NumericInstruction.F64Min -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Min(left, right, destination) }
    is NumericInstruction.F64Max -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Max(left, right, destination) }
    is NumericInstruction.F64Copysign -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Copysign(left, right, destination) }
    is NumericInstruction.I32Eq -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Eq(left, right, destination) }
    is NumericInstruction.I32Ne -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32Ne(left, right, destination) }
    is NumericInstruction.I32LtS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32LtS(left, right, destination) }
    is NumericInstruction.I32LtU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32LtU(left, right, destination) }
    is NumericInstruction.I32GtS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32GtS(left, right, destination) }
    is NumericInstruction.I32GtU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32GtU(left, right, destination) }
    is NumericInstruction.I32LeS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32LeS(left, right, destination) }
    is NumericInstruction.I32LeU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32LeU(left, right, destination) }
    is NumericInstruction.I32GeS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32GeS(left, right, destination) }
    is NumericInstruction.I32GeU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I32GeU(left, right, destination) }
    is NumericInstruction.I64Eq -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Eq(left, right, destination) }
    is NumericInstruction.I64Ne -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64Ne(left, right, destination) }
    is NumericInstruction.I64LtS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64LtS(left, right, destination) }
    is NumericInstruction.I64LtU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64LtU(left, right, destination) }
    is NumericInstruction.I64GtS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64GtS(left, right, destination) }
    is NumericInstruction.I64GtU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64GtU(left, right, destination) }
    is NumericInstruction.I64LeS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64LeS(left, right, destination) }
    is NumericInstruction.I64LeU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64LeU(left, right, destination) }
    is NumericInstruction.I64GeS -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64GeS(left, right, destination) }
    is NumericInstruction.I64GeU -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.I64GeU(left, right, destination) }
    is NumericInstruction.F32Eq -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Eq(left, right, destination) }
    is NumericInstruction.F32Ne -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Ne(left, right, destination) }
    is NumericInstruction.F32Lt -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Lt(left, right, destination) }
    is NumericInstruction.F32Gt -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Gt(left, right, destination) }
    is NumericInstruction.F32Le -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Le(left, right, destination) }
    is NumericInstruction.F32Ge -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F32Ge(left, right, destination) }
    is NumericInstruction.F64Eq -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Eq(left, right, destination) }
    is NumericInstruction.F64Ne -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Ne(left, right, destination) }
    is NumericInstruction.F64Lt -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Lt(left, right, destination) }
    is NumericInstruction.F64Gt -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Gt(left, right, destination) }
    is NumericInstruction.F64Le -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Le(left, right, destination) }
    is NumericInstruction.F64Ge -> FrameSlotRawNumericBinaryLowerer(state) { left, right, destination -> NumericSuperInstruction.F64Ge(left, right, destination) }

    is NumericInstruction.I64Add128 -> FrameSlotRawNumericQuadDualDestinationLowerer(state) { leftLow, leftHigh, rightLow, rightHigh, destinationLow, destinationHigh ->
        NumericSuperInstruction.I64Add128(leftLow, leftHigh, rightLow, rightHigh, destinationLow, destinationHigh)
    }
    is NumericInstruction.I64Sub128 -> FrameSlotRawNumericQuadDualDestinationLowerer(state) { leftLow, leftHigh, rightLow, rightHigh, destinationLow, destinationHigh ->
        NumericSuperInstruction.I64Sub128(leftLow, leftHigh, rightLow, rightHigh, destinationLow, destinationHigh)
    }
    is NumericInstruction.I64MulWideS -> FrameSlotRawNumericBinaryDualDestinationLowerer(state) { left, right, destinationLow, destinationHigh ->
        NumericSuperInstruction.I64MulWideS(left, right, destinationLow, destinationHigh)
    }
    is NumericInstruction.I64MulWideU -> FrameSlotRawNumericBinaryDualDestinationLowerer(state) { left, right, destinationLow, destinationHigh ->
        NumericSuperInstruction.I64MulWideU(left, right, destinationLow, destinationHigh)
    }
}

private inline fun <T : Instruction> FrameSlotRawNumericUnaryLowerer(
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedDestination) -> T,
): List<Instruction>? = FrameSlotUnaryLowerer(
    operand = FusedOperand.ValueStack,
    destination = FusedDestination.ValueStack,
    state = state,
    rewrite = rewrite,
)

private inline fun <T : Instruction> FrameSlotRawNumericBinaryLowerer(
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedOperand, FusedDestination) -> T,
): List<Instruction>? {
    val loweredRight = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredLeft = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredDestination = FrameSlotDestinationLowerer(FusedDestination.ValueStack, state) ?: return null

    return listOf(
        rewrite(
            loweredLeft.lowered,
            loweredRight.lowered,
            loweredDestination.lowered(loweredLeft.consumed + loweredRight.consumed),
        ),
    )
}

private inline fun <T : Instruction> FrameSlotRawNumericBinaryDualDestinationLowerer(
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedOperand, FusedDestination, FusedDestination) -> T,
): List<Instruction>? {
    val loweredRight = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredLeft = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredDestinations = FrameSlotMultiDestinationLowerer(
        destinations = listOf(FusedDestination.ValueStack, FusedDestination.ValueStack),
        state = state,
    ) ?: return null

    return listOf(
        rewrite(
            loweredLeft.lowered,
            loweredRight.lowered,
            loweredDestinations[0],
            loweredDestinations[1],
        ),
    )
}

private inline fun <T : Instruction> FrameSlotRawNumericQuadDualDestinationLowerer(
    state: FrameSlotState,
    rewrite: (
        FusedOperand,
        FusedOperand,
        FusedOperand,
        FusedOperand,
        FusedDestination,
        FusedDestination,
    ) -> T,
): List<Instruction>? {
    val loweredRightHigh = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredRightLow = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredLeftHigh = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredLeftLow = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredDestinations = FrameSlotMultiDestinationLowerer(
        destinations = listOf(FusedDestination.ValueStack, FusedDestination.ValueStack),
        state = state,
    ) ?: return null

    return listOf(
        rewrite(
            loweredLeftLow.lowered,
            loweredLeftHigh.lowered,
            loweredRightLow.lowered,
            loweredRightHigh.lowered,
            loweredDestinations[0],
            loweredDestinations[1],
        ),
    )
}

private fun FrameSlotRawMemoryInstructionLowerer(
    instruction: MemoryInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is MemoryInstruction.Load -> FrameSlotRawMemoryLoadLowerer(instruction, state)
    is MemoryInstruction.Store -> FrameSlotRawMemoryStoreLowerer(instruction, state)
    is MemoryInstruction.MemorySize -> FrameSlotDestinationOnlyLowerer(FusedDestination.ValueStack, state) { destination ->
        MemorySuperInstruction.MemorySize(
            destination = destination,
            memoryIndex = instruction.memoryIndex,
        )
    }
    is MemoryInstruction.MemoryGrow -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.MemoryGrow(
            pagesToAdd = FusedOperand.ValueStack,
            destination = FusedDestination.ValueStack,
            memoryIndex = instruction.memoryIndex,
        ),
        state,
    )
    is MemoryInstruction.MemoryInit -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.MemoryInit(
            bytesToCopy = FusedOperand.ValueStack,
            sourceOffset = FusedOperand.ValueStack,
            destinationOffset = FusedOperand.ValueStack,
            memoryIndex = instruction.memoryIndex,
            dataIndex = instruction.dataIndex,
        ),
        state,
    )
    is MemoryInstruction.DataDrop -> listOf(instruction)
    is MemoryInstruction.MemoryCopy -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.MemoryCopy(
            bytesToCopy = FusedOperand.ValueStack,
            sourceOffset = FusedOperand.ValueStack,
            destinationOffset = FusedOperand.ValueStack,
            srcIndex = instruction.srcIndex,
            dstIndex = instruction.dstIndex,
        ),
        state,
    )
    is MemoryInstruction.MemoryFill -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.MemoryFill(
            bytesToFill = FusedOperand.ValueStack,
            fillValue = FusedOperand.ValueStack,
            offset = FusedOperand.ValueStack,
            memoryIndex = instruction.memoryIndex,
        ),
        state,
    )
}

private fun FrameSlotMemoryInstructionLowerer(
    instruction: MemorySuperInstruction,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is MemorySuperInstruction.MemorySize -> FrameSlotDestinationOnlyLowerer(instruction.destination, state) { destination ->
        instruction.copy(destination = destination)
    }
    is MemorySuperInstruction.MemoryGrow -> FrameSlotUnaryLowerer(instruction.pagesToAdd, instruction.destination, state) { pagesToAdd, destination ->
        instruction.copy(pagesToAdd = pagesToAdd, destination = destination)
    }
    is MemorySuperInstruction.MemoryInit -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.bytesToCopy,
            instruction.sourceOffset,
            instruction.destinationOffset,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            bytesToCopy = operands[0],
            sourceOffset = operands[1],
            destinationOffset = operands[2],
        )
    }
    is MemorySuperInstruction.MemoryCopy -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.bytesToCopy,
            instruction.sourceOffset,
            instruction.destinationOffset,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            bytesToCopy = operands[0],
            sourceOffset = operands[1],
            destinationOffset = operands[2],
        )
    }
    is MemorySuperInstruction.MemoryFill -> FrameSlotOperandListLowerer(
        operands = listOf(
            instruction.bytesToFill,
            instruction.fillValue,
            instruction.offset,
        ),
        state = state,
    ) { operands, _ ->
        instruction.copy(
            bytesToFill = operands[0],
            fillValue = operands[1],
            offset = operands[2],
        )
    }
    is MemorySuperInstruction.I32Load -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.F32Load -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.F64Load -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I32Load8S -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I32Load8U -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I32Load16S -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I32Load16U -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load8S -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load8U -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load16S -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load16U -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load32S -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I64Load32U -> FrameSlotUnaryLowerer(instruction.addressOperand, instruction.destination, state) { addressOperand, destination ->
        instruction.copy(addressOperand = addressOperand, destination = destination)
    }
    is MemorySuperInstruction.I32Store -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.I64Store -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.F32Store -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.F64Store -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.I32Store8 -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.I32Store16 -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.I64Store8 -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.I64Store16 -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
    is MemorySuperInstruction.I64Store32 -> FrameSlotBinaryOperandsLowerer(instruction.valueOperand, instruction.addressOperand, state) { valueOperand, addressOperand ->
        instruction.copy(valueOperand = valueOperand, addressOperand = addressOperand)
    }
}

private inline fun <T : Instruction> FrameSlotRawUnaryLowerer(
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedDestination) -> T,
): List<Instruction>? = FrameSlotUnaryLowerer(
    operand = FusedOperand.ValueStack,
    destination = FusedDestination.ValueStack,
    state = state,
    rewrite = rewrite,
)

private inline fun <T : Instruction> FrameSlotRawBinaryLowerer(
    state: FrameSlotState,
    rewrite: (FusedOperand, FusedOperand, FusedDestination) -> T,
): List<Instruction>? {
    val loweredRight = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredLeft = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
    val loweredDestination = FrameSlotDestinationLowerer(FusedDestination.ValueStack, state) ?: return null

    return listOf(
        rewrite(
            loweredLeft.lowered,
            loweredRight.lowered,
            loweredDestination.lowered(loweredLeft.consumed + loweredRight.consumed),
        ),
    )
}

private fun FrameSlotRawMemoryLoadLowerer(
    instruction: MemoryInstruction.Load,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is MemoryInstruction.Load.I32.I32Load -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Load(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I32.I32Load8S -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Load8S(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I32.I32Load8U -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Load8U(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I32.I32Load16S -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Load16S(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I32.I32Load16U -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Load16U(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load8S -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load8S(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load8U -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load8U(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load16S -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load16S(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load16U -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load16U(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load32S -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load32S(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.I64.I64Load32U -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Load32U(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.F32.F32Load -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.F32Load(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Load.F64.F64Load -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.F64Load(FusedOperand.ValueStack, FusedDestination.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
}

private fun FrameSlotRawMemoryStoreLowerer(
    instruction: MemoryInstruction.Store,
    state: FrameSlotState,
): List<Instruction>? = when (instruction) {
    is MemoryInstruction.Store.I32.I32Store -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Store(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.I32.I32Store8 -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Store8(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.I32.I32Store16 -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I32Store16(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.I64.I64Store -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Store(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.I64.I64Store8 -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Store8(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.I64.I64Store16 -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Store16(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.I64.I64Store32 -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.I64Store32(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.F32.F32Store -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.F32Store(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
    is MemoryInstruction.Store.F64.F64Store -> FrameSlotMemoryInstructionLowerer(
        MemorySuperInstruction.F64Store(FusedOperand.ValueStack, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg),
        state,
    )
}

private fun FrameSlotTargetLabel(
    labels: ArrayDeque<LabelContext>,
    labelIndex: Index.LabelIndex,
): LabelContext? {
    val targetIndex = labels.size - 1 - labelIndex.idx
    if (targetIndex !in 0 until labels.size) return null
    return labels.elementAt(targetIndex)
}

private fun FrameSlotCopyInstructions(
    sourceSlots: List<Int>,
    destinationSlots: List<Int>,
): List<Instruction> = if (sourceSlots == destinationSlots) {
    emptyList()
} else {
    listOf(
        AdminInstruction.CopySlots(
            sourceSlots = sourceSlots,
            destinationSlots = destinationSlots,
        ),
    )
}

private fun FrameSlotPreserveOverwrittenLocalSlot(
    localIndex: Int,
    newValue: FusedOperand,
    state: FrameSlotState,
): List<Instruction> {
    val localSlot = state.localSlot(localIndex)
    val writesBackToSameLocal = newValue is FusedOperand.FrameSlot && newValue.offset == localSlot
    val aliasedOperands = state.localAliases.getOrNull(localIndex)?.toList().orEmpty()
    if (writesBackToSameLocal || aliasedOperands.isEmpty()) {
        return emptyList()
    }

    aliasedOperands.forEach { operand ->
        state.untrackLocalAlias(operand)
        operand.localAlias = null
    }

    return FrameSlotCopyInstructions(
        sourceSlots = List(aliasedOperands.size) { localSlot },
        destinationSlots = aliasedOperands.map(FrameSlotStackOperand::reservedSlot),
    )
}

private data class LoweredOperand(
    val lowered: FusedOperand,
    val consumed: List<Int>,
    val source: FrameSlotStackOperand? = null,
)

private data class LoweredOperands(
    val lowered: List<FusedOperand>,
    val consumed: List<Int>,
    val sources: List<FrameSlotStackOperand?>,
)

private fun FrameSlotOperandLowerer(
    operand: FusedOperand,
    state: FrameSlotState,
): LoweredOperand? = when (operand) {
    is FusedOperand.I32Const,
    is FusedOperand.I64Const,
    is FusedOperand.F32Const,
    is FusedOperand.F64Const,
    is FusedOperand.FrameSlot,
    -> LoweredOperand(operand, emptyList())
    is FusedOperand.LocalGet -> LoweredOperand(
        lowered = FusedOperand.FrameSlot(state.localSlot(operand.index.idx)),
        consumed = emptyList(),
    )
    FusedOperand.ValueStack -> {
        val stackOperand = state.popStackOperand()
            ?: if (!state.reachable) {
                FrameSlotStackOperand(
                    type = null,
                    reservedSlot = state.allocator.allocateTemporarySlot(),
                )
            } else {
                return null
            }
        LoweredOperand(
            lowered = FrameSlotLoweredOperand(stackOperand, state),
            consumed = listOf(stackOperand.reservedSlot),
            source = stackOperand,
        )
    }
}

private fun FrameSlotOperandsLowerer(
    operands: List<FusedOperand>,
    state: FrameSlotState,
): LoweredOperands? {
    val lowered = ArrayList<FusedOperand>(operands.size)
    val consumed = ArrayList<Int>()
    val sources = ArrayList<FrameSlotStackOperand?>(operands.size)
    operands.forEach { operand ->
        val loweredOperand = FrameSlotOperandLowerer(operand, state) ?: return null
        lowered += loweredOperand.lowered
        consumed += loweredOperand.consumed
        sources += loweredOperand.source
    }
    return LoweredOperands(
        lowered = lowered,
        consumed = consumed,
        sources = sources,
    )
}

private fun FrameSlotStackOperands(
    count: Int,
    state: FrameSlotState,
): LoweredOperands? {
    val lowered = ArrayList<FusedOperand>(count)
    val consumed = ArrayList<Int>(count)
    val sources = ArrayList<FrameSlotStackOperand?>(count)
    repeat(count) {
        val loweredOperand = FrameSlotOperandLowerer(FusedOperand.ValueStack, state) ?: return null
        lowered += loweredOperand.lowered
        consumed += loweredOperand.consumed
        sources += loweredOperand.source
    }
    lowered.reverse()
    consumed.reverse()
    sources.reverse()
    return LoweredOperands(
        lowered = lowered,
        consumed = consumed,
        sources = sources,
    )
}

private fun FrameSlotOperandSlots(
    operands: List<FusedOperand>,
    sources: List<FrameSlotStackOperand?>,
    state: FrameSlotState,
): Pair<List<Instruction>, List<Int>>? {
    val instructions = ArrayList<Instruction>()
    val slots = ArrayList<Int>(operands.size)

    operands.forEachIndexed { index, operand ->
        when (operand) {
            is FusedOperand.FrameSlot -> slots += operand.offset
            else -> {
                val source = sources.getOrNull(index) ?: return null
                instructions += FrameSlotMaterializeOperand(source, state)
                slots += source.reservedSlot
            }
        }
    }
    return instructions to slots
}

private fun FrameSlotState.liveTemporarySlotExclusive(): Int {
    val highestLiveTemporarySlot = stack
        .map { operand -> FrameSlotLoweredOperand(operand, this) }
        .mapNotNull { operand ->
            (operand as? FusedOperand.FrameSlot)
                ?.offset
                ?.takeIf(allocator::isTemporarySlot)
        }.maxOrNull()

    return highestLiveTemporarySlot?.plus(1) ?: baseSlots
}

private fun FrameSlotCallFrameSlot(
    state: FrameSlotState,
): Int = state.liveTemporarySlotExclusive()

private fun FrameSlotCallInterfaceSlotCount(
    paramCount: Int,
    resultCount: Int,
): Int = maxOf(paramCount, resultCount)

private fun FrameSlotReserveCallInterfaceSlots(
    state: FrameSlotState,
    callFrameSlot: Int,
    slotCount: Int,
) {
    if (slotCount == 0) return
    state.allocator.reserveTemporarySlot(callFrameSlot + slotCount - 1)
}

private data class PreparedCallOperands(
    val instructions: List<Instruction>,
    val operands: List<FusedOperand>,
)

private fun FrameSlotCallOperandSlots(
    paramCount: Int,
    callFrameSlot: Int,
): List<Int> = List(paramCount) { index -> callFrameSlot + index }

private fun FrameSlotPrepareCallOperands(
    operands: List<FusedOperand>,
    destinationSlots: List<Int>,
): PreparedCallOperands {
    check(operands.size == destinationSlots.size) {
        "prepared call operand count must match destination slots"
    }

    val sourceSlots = mutableListOf<Int>()
    val copiedDestinationSlots = mutableListOf<Int>()
    val immediateInstructions = mutableListOf<Instruction>()

    operands.forEachIndexed { index, operand ->
        val destinationSlot = destinationSlots[index]
        when (operand) {
            is FusedOperand.FrameSlot -> if (operand.offset != destinationSlot) {
                sourceSlots += operand.offset
                copiedDestinationSlots += destinationSlot
            }
            is FusedOperand.I32Const,
            is FusedOperand.I64Const,
            is FusedOperand.F32Const,
            is FusedOperand.F64Const,
            -> immediateInstructions += FrameSlotImmediateInstruction(operand, destinationSlot)
            else -> error("call operands must lower to frame slots or immediates before call preparation: $operand")
        }
    }

    return PreparedCallOperands(
        instructions = buildList {
            addAll(FrameSlotCopyInstructions(sourceSlots, copiedDestinationSlots))
            addAll(immediateInstructions)
        },
        operands = destinationSlots.map { slot -> FusedOperand.FrameSlot(slot) },
    )
}

private fun FrameSlotPrepareCallTargetOperand(
    operand: FusedOperand,
    overwrittenSlots: List<Int>,
    state: FrameSlotState,
): Pair<List<Instruction>, FusedOperand> = when (operand) {
    is FusedOperand.FrameSlot -> {
        if (operand.offset !in overwrittenSlots) {
            emptyList<Instruction>() to operand
        } else {
            val stagedSlot = state.allocator.allocateTemporarySlot()
            FrameSlotCopyInstructions(
                sourceSlots = listOf(operand.offset),
                destinationSlots = listOf(stagedSlot),
            ) to FusedOperand.FrameSlot(stagedSlot)
        }
    }
    else -> emptyList<Instruction>() to operand
}

private fun FrameSlotCallResultSlots(
    resultTypes: List<ValueType>,
    callFrameSlot: Int,
): List<Int> = List(resultTypes.size) { index -> callFrameSlot + index }

private fun FrameSlotPushResultOperands(
    state: FrameSlotState,
    resultTypes: List<ValueType>,
    resultSlots: List<Int>,
) {
    resultSlots.forEachIndexed { index, slot ->
        state.pushStackOperand(
            FrameSlotStackOperand(
                type = resultTypes.getOrNull(index),
                reservedSlot = slot,
            ),
        )
    }
}

private data class LoweredDestination(
    val lowered: (List<Int>) -> FusedDestination,
)

private fun FrameSlotDestinationLowerer(
    destination: FusedDestination,
    state: FrameSlotState,
    valueType: ValueType? = null,
): LoweredDestination? = when (destination) {
    is FusedDestination.FrameSlot,
    -> LoweredDestination { destination }
    is FusedDestination.LocalSet -> LoweredDestination {
        FusedDestination.FrameSlot(state.localSlot(destination.index.idx))
    }
    FusedDestination.ValueStack -> LoweredDestination { consumedSlots ->
        val slot = consumedSlots.lastOrNull()
            ?.takeIf(state::canReuseValueStackDestinationSlot)
            ?: state.allocator.allocateTemporarySlot()
        state.pushStackOperand(
            FrameSlotStackOperand(
                type = valueType,
                reservedSlot = slot,
            ),
        )
        FusedDestination.FrameSlot(slot)
    }
}

private fun FrameSlotState.popStackOperand(): FrameSlotStackOperand? {
    if (stack.isEmpty()) return null
    val operand = stack.removeAt(stack.lastIndex)
    untrackLocalAlias(operand)
    allocator.releaseTemporarySlot(operand.reservedSlot)
    return operand
}

private fun FrameSlotState.peekStackOperand(): FrameSlotStackOperand? = stack.lastOrNull()

private fun FrameSlotState.branchSourceOperands(arity: Int): List<FrameSlotStackOperand>? {
    if (arity > stack.size) return null
    return stack.takeLast(arity)
}

private fun FrameSlotState.branchSourceOperandsExcludingTop(arity: Int): List<FrameSlotStackOperand>? {
    if (arity + 1 > stack.size) return null
    return stack.dropLast(1).takeLast(arity)
}

private fun FrameSlotState.pushStackOperand(operand: FrameSlotStackOperand) {
    allocator.reserveTemporarySlot(operand.reservedSlot)
    stack += operand
    trackLocalAlias(operand)
}

private fun FrameSlotState.canReuseValueStackDestinationSlot(slot: Int): Boolean {
    if (!allocator.isTemporarySlot(slot)) return false
    if (stack.any { operand -> operand.reservedSlot == slot }) return false
    return true
}

private fun FrameSlotState.trackLocalAlias(operand: FrameSlotStackOperand) {
    val localAlias = operand.localAlias ?: return
    localAliases.getOrNull(localAlias)?.add(operand)
}

private fun FrameSlotState.untrackLocalAlias(operand: FrameSlotStackOperand) {
    val localAlias = operand.localAlias ?: return
    localAliases.getOrNull(localAlias)?.remove(operand)
}

private fun FrameSlotState.rebuildLocalAliases() {
    localAliases.forEach(MutableSet<FrameSlotStackOperand>::clear)
    stack.forEach(::trackLocalAlias)
}

private fun FrameSlotConstantValueType(operand: FusedOperand): ValueType = when (operand) {
    is FusedOperand.I32Const -> ValueType.Number(NumberType.I32)
    is FusedOperand.I64Const -> ValueType.Number(NumberType.I64)
    is FusedOperand.F32Const -> ValueType.Number(NumberType.F32)
    is FusedOperand.F64Const -> ValueType.Number(NumberType.F64)
    else -> error("operand is not a constant: $operand")
}

private fun FrameSlotLoweredOperand(
    operand: FrameSlotStackOperand,
    state: FrameSlotState,
): FusedOperand = when {
    operand.immediate != null -> operand.immediate!!
    operand.localAlias != null -> FusedOperand.FrameSlot(state.localSlot(operand.localAlias!!))
    else -> FusedOperand.FrameSlot(operand.reservedSlot)
}

private fun FrameSlotMaterializeOperands(
    operands: List<FrameSlotStackOperand>,
    state: FrameSlotState,
): Pair<List<Instruction>, List<Int>> {
    val instructions = buildList {
        operands.forEach { operand ->
            addAll(FrameSlotMaterializeOperand(operand, state))
        }
    }
    return instructions to operands.map(FrameSlotStackOperand::reservedSlot)
}

private fun FrameSlotPlanMaterializedOperands(
    operands: List<FrameSlotStackOperand>,
    state: FrameSlotState,
): Pair<List<Instruction>, List<Int>> {
    val instructions = buildList {
        operands.forEach { operand ->
            addAll(FrameSlotPlannedMaterialization(operand, state))
        }
    }
    return instructions to operands.map(FrameSlotStackOperand::reservedSlot)
}

private fun FrameSlotMaterializeOperand(
    operand: FrameSlotStackOperand,
    state: FrameSlotState,
): List<Instruction> {
    operand.immediate?.let { immediate ->
        operand.immediate = null
        return listOf(FrameSlotImmediateInstruction(immediate, operand.reservedSlot))
    }

    val localAlias = operand.localAlias ?: return emptyList()
    state.untrackLocalAlias(operand)
    operand.localAlias = null
    return FrameSlotCopyInstructions(
        sourceSlots = listOf(state.localSlot(localAlias)),
        destinationSlots = listOf(operand.reservedSlot),
    )
}

private fun FrameSlotPlannedMaterialization(
    operand: FrameSlotStackOperand,
    state: FrameSlotState,
): List<Instruction> {
    operand.immediate?.let { immediate ->
        return listOf(FrameSlotImmediateInstruction(immediate, operand.reservedSlot))
    }

    val localAlias = operand.localAlias ?: return emptyList()
    return FrameSlotCopyInstructions(
        sourceSlots = listOf(state.localSlot(localAlias)),
        destinationSlots = listOf(operand.reservedSlot),
    )
}

private fun FrameSlotImmediateInstruction(
    operand: FusedOperand,
    destinationSlot: Int,
): Instruction = when (operand) {
    is FusedOperand.I32Const -> NumericSuperInstruction.I32Const(
        value = operand.const,
        destination = FusedDestination.FrameSlot(destinationSlot),
    )
    is FusedOperand.I64Const -> NumericSuperInstruction.I64Const(
        value = operand.const,
        destination = FusedDestination.FrameSlot(destinationSlot),
    )
    is FusedOperand.F32Const -> NumericSuperInstruction.F32Const(
        bits = operand.const.toRawBits(),
        destination = FusedDestination.FrameSlot(destinationSlot),
    )
    is FusedOperand.F64Const -> NumericSuperInstruction.F64Const(
        bits = operand.const.toRawBits(),
        destination = FusedDestination.FrameSlot(destinationSlot),
    )
    else -> error("operand is not materializable as an immediate: $operand")
}

private fun FrameSlotReadableSlot(
    operand: FrameSlotStackOperand,
    state: FrameSlotState,
): Pair<List<Instruction>, Int> {
    if (operand.immediate != null) {
        val instructions = FrameSlotMaterializeOperand(operand, state)
        return instructions to operand.reservedSlot
    }

    return emptyList<Instruction>() to (operand.localAlias?.let(state::localSlot) ?: operand.reservedSlot)
}

private fun FrameSlotStackOperandForTeeResult(
    operand: FusedOperand,
    loweredOperand: LoweredOperand,
    localIdx: Index.LocalIndex,
    state: FrameSlotState,
): FrameSlotStackOperand = when (operand) {
    FusedOperand.ValueStack ->
        loweredOperand.source
            ?: error("value stack lowering must retain source operand for local.tee")
    is FusedOperand.I32Const,
    is FusedOperand.I64Const,
    is FusedOperand.F32Const,
    is FusedOperand.F64Const,
    -> FrameSlotStackOperand(
        type = FrameSlotConstantValueType(operand),
        reservedSlot = state.allocator.allocateTemporarySlot(),
        immediate = operand,
    )
    else -> FrameSlotStackOperand(
        type = state.localType(localIdx.idx),
        reservedSlot = state.allocator.allocateTemporarySlot(),
        localAlias = localIdx.idx,
    )
}

private fun PassContext.blockType(
    blockType: BlockType,
): FunctionType? = LegacyBlockTypeExpander(module.definedTypes, blockType)

private fun PassContext.functionType(
    typeIndex: Index.TypeIndex,
): FunctionType? = module.definedTypes.getOrNull(typeIndex.idx)?.functionType()

private fun PassContext.tagType(
    tagIndex: Index.TagIndex,
) = buildList {
    addAll(
        module.imports
            .map(Import::descriptor)
            .filterIsInstance<Import.Descriptor.Tag>()
            .map { descriptor -> module.definedTypes.getOrNull(descriptor.type.typeIndex)?.functionType() },
    )
    addAll(module.tags.map { it.type.functionType })
}.getOrNull(tagIndex.idx)

private fun PassContext.catchHandlerPayloadArity(
    handler: ControlInstruction.CatchHandler,
): Int? = when (handler) {
    is ControlInstruction.CatchHandler.Catch -> tagType(handler.tagIndex)?.params?.types?.size
    is ControlInstruction.CatchHandler.CatchRef -> tagType(handler.tagIndex)?.params?.types?.size?.plus(1)
    is ControlInstruction.CatchHandler.CatchAll -> 0
    is ControlInstruction.CatchHandler.CatchAllRef -> 1
}

private fun PassContext.structFieldCount(
    typeIndex: Index.TypeIndex,
): Int? = module.definedTypes.getOrNull(typeIndex.idx)?.asSubType?.compositeType?.structType()?.fields?.size
