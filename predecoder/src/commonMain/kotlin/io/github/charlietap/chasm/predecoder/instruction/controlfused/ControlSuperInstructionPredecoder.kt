package io.github.charlietap.chasm.predecoder.instruction.controlfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrIfDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrOnCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrOnCastFailDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrOnNonNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrOnNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.BrTableDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.CallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.IfDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ReturnCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ThrowRefDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.predecoder.InstructionSequencePredecoder
import io.github.charlietap.chasm.predecoder.InstructionSequencePredecoderList
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction as RuntimeFusedControlInstruction

internal fun ControlSuperInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlSuperInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ControlSuperInstruction.BrIf -> strictBrIfInstruction(context, instruction).bind()
        is ControlSuperInstruction.BrOnCast -> strictBrOnCastInstruction(context, instruction).bind()
        is ControlSuperInstruction.BrOnCastFail -> strictBrOnCastFailInstruction(context, instruction).bind()
        is ControlSuperInstruction.BrOnNonNull -> strictBrOnNonNullInstruction(context, instruction).bind()
        is ControlSuperInstruction.BrOnNull -> strictBrOnNullInstruction(context, instruction).bind()
        is ControlSuperInstruction.BrTable -> strictBrTableInstruction(context, instruction).bind()
        is ControlSuperInstruction.Call -> strictCallInstruction(context, instruction).bind()
        is ControlSuperInstruction.CallIndirect -> strictCallIndirectInstruction(context, instruction).bind()
        is ControlSuperInstruction.CallRef -> strictCallRefInstruction(instruction)
        is ControlSuperInstruction.If -> strictIfInstruction(context, instruction).bind()
        is ControlSuperInstruction.ReturnCall -> strictReturnCallInstruction(context, instruction).bind()
        is ControlSuperInstruction.ReturnCallIndirect -> strictReturnCallIndirectInstruction(context, instruction).bind()
        is ControlSuperInstruction.ReturnCallRef -> strictReturnCallRefInstruction(instruction)
        is ControlSuperInstruction.Throw -> strictThrowInstruction(instruction)
        is ControlSuperInstruction.ThrowRef -> strictThrowRefInstruction(instruction)
    }
}

private fun strictCallInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.Call,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.functionAddress(instruction.functionIndex).bind()

    when (val instance = context.store.function(address)) {
        is FunctionInstance.HostFunction -> {
            CallDispatcher(
                RuntimeFusedControlInstruction.HostCall(
                    instance = instance,
                    resultSlots = instruction.resultSlots,
                    callFrameSlot = instruction.callFrameSlot,
                ),
            )
        }

        is FunctionInstance.WasmFunction -> {
            CallDispatcher(
                RuntimeFusedControlInstruction.WasmCall(
                    instance = instance,
                    resultSlots = instruction.resultSlots,
                    callFrameSlot = instruction.callFrameSlot,
                ),
            )
        }
    }
}

private fun strictReturnCallInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.ReturnCall,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.functionAddress(instruction.functionIndex).bind()
    val operands = strictCallOperands(instruction.operands)

    when (val instance = context.store.function(address)) {
        is FunctionInstance.HostFunction -> {
            ReturnCallDispatcher(
                RuntimeFusedControlInstruction.ReturnHostCall(
                    instance = instance,
                    operands = operands,
                ),
            )
        }

        is FunctionInstance.WasmFunction -> {
            ReturnCallDispatcher(
                RuntimeFusedControlInstruction.ReturnWasmCall(
                    instance = instance,
                    operands = operands,
                ),
            )
        }
    }
}

private fun strictCallIndirectInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.CallIndirect,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIndex).bind()
    val table = context.store.table(address)
    val type = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val elementImmediate = strictControlImmediate(instruction.elementIndex)?.toInt()
    val elementSlot = strictControlOperandSlot(instruction.elementIndex)

    when {
        elementImmediate != null -> {
            CallDispatcher(
                RuntimeFusedControlInstruction.CallIndirectI(
                    elementIndex = elementImmediate,
                    type = type,
                    table = table,
                    resultSlots = instruction.resultSlots,
                    callFrameSlot = instruction.callFrameSlot,
                ),
            )
        }

        elementSlot != null -> {
            CallDispatcher(
                RuntimeFusedControlInstruction.CallIndirectS(
                    elementIndexSlot = elementSlot,
                    type = type,
                    table = table,
                    resultSlots = instruction.resultSlots,
                    callFrameSlot = instruction.callFrameSlot,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictCallRefInstruction(
    instruction: ControlSuperInstruction.CallRef,
): DispatchableInstruction {
    val functionSlot = strictControlOperandSlot(instruction.functionReference)
        ?: error("control fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")

    return CallDispatcher(
        RuntimeFusedControlInstruction.CallRefS(
            functionSlot = functionSlot,
            resultSlots = instruction.resultSlots,
            callFrameSlot = instruction.callFrameSlot,
        ),
    )
}

private fun strictReturnCallIndirectInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.ReturnCallIndirect,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIndex).bind()
    val table = context.store.table(address)
    val type = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val operands = strictCallOperands(instruction.operands)
    val elementImmediate = strictControlImmediate(instruction.elementIndex)?.toInt()
    val elementSlot = strictControlOperandSlot(instruction.elementIndex)

    when {
        elementImmediate != null -> {
            ReturnCallDispatcher(
                RuntimeFusedControlInstruction.ReturnCallIndirectI(
                    elementIndex = elementImmediate,
                    operands = operands,
                    type = type,
                    table = table,
                ),
            )
        }

        elementSlot != null -> {
            ReturnCallDispatcher(
                RuntimeFusedControlInstruction.ReturnCallIndirectS(
                    elementIndexSlot = elementSlot,
                    operands = operands,
                    type = type,
                    table = table,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictReturnCallRefInstruction(
    instruction: ControlSuperInstruction.ReturnCallRef,
): DispatchableInstruction {
    val functionSlot = strictControlOperandSlot(instruction.functionReference)
        ?: error("control fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")

    return ReturnCallDispatcher(
        RuntimeFusedControlInstruction.ReturnCallRefS(
            functionSlot = functionSlot,
            operands = strictCallOperands(instruction.operands),
        ),
    )
}

private fun strictThrowInstruction(
    instruction: ControlSuperInstruction.Throw,
): DispatchableInstruction = ThrowDispatcher(
    RuntimeFusedControlInstruction.Throw(
        tagIndex = instruction.tagIndex,
        payloadSlots = strictControlOperandSlots(instruction.payloads, "throw"),
    ),
)

private fun strictThrowRefInstruction(
    instruction: ControlSuperInstruction.ThrowRef,
): DispatchableInstruction {
    val exceptionSlot = strictControlOperandSlot(instruction.exceptionReference)
        ?: error(
            "throw_ref operand must lower to a frame slot before predecode: ${instruction.exceptionReference}",
        )

    return ThrowRefDispatcher(
        RuntimeFusedControlInstruction.ThrowRefS(
            exceptionSlot = exceptionSlot,
        ),
    )
}

private fun strictBrIfInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.BrIf,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandImmediate = strictControlImmediate(instruction.operand)
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val takenInstructions = predecodeTakenInstructions(context, instruction.takenInstructions).bind()

    when {
        operandImmediate != null -> {
            BrIfDispatcher(
                RuntimeFusedControlInstruction.BrIfI(
                    operand = operandImmediate,
                    labelIndex = instruction.labelIndex,
                    takenInstructions = takenInstructions,
                ),
            )
        }

        operandSlot != null -> {
            BrIfDispatcher(
                RuntimeFusedControlInstruction.BrIfS(
                    operandSlot = operandSlot,
                    labelIndex = instruction.labelIndex,
                    takenInstructions = takenInstructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictBrTableInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.BrTable,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandImmediate = strictControlIndexImmediate(instruction.operand)
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val takenInstructions = predecodeTakenInstructionGroups(context, instruction.takenInstructions).bind()
    val defaultTakenInstructions = predecodeTakenInstructions(context, instruction.defaultTakenInstructions).bind()

    when {
        operandImmediate != null -> {
            BrTableDispatcher(
                RuntimeFusedControlInstruction.BrTableI(
                    operand = operandImmediate,
                    labelIndices = instruction.labelIndices,
                    defaultLabelIndex = instruction.defaultLabelIndex,
                    takenInstructions = takenInstructions,
                    defaultTakenInstructions = defaultTakenInstructions,
                ),
            )
        }

        operandSlot != null -> {
            BrTableDispatcher(
                RuntimeFusedControlInstruction.BrTableS(
                    operandSlot = operandSlot,
                    labelIndices = instruction.labelIndices,
                    defaultLabelIndex = instruction.defaultLabelIndex,
                    takenInstructions = takenInstructions,
                    defaultTakenInstructions = defaultTakenInstructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictBrOnNullInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.BrOnNull,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val takenInstructions = predecodeTakenInstructions(context, instruction.takenInstructions).bind()

    when {
        operandSlot != null -> {
            BrOnNullDispatcher(
                RuntimeFusedControlInstruction.BrOnNullS(
                    operandSlot = operandSlot,
                    labelIndex = instruction.labelIndex,
                    takenInstructions = takenInstructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictBrOnNonNullInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.BrOnNonNull,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val takenInstructions = predecodeTakenInstructions(context, instruction.takenInstructions).bind()

    when {
        operandSlot != null -> {
            BrOnNonNullDispatcher(
                RuntimeFusedControlInstruction.BrOnNonNullS(
                    operandSlot = operandSlot,
                    labelIndex = instruction.labelIndex,
                    takenInstructions = takenInstructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictBrOnCastInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.BrOnCast,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val takenInstructions = predecodeTakenInstructions(context, instruction.takenInstructions).bind()
    preResolveCastType(context, instruction.dstReferenceType)

    when {
        operandSlot != null -> {
            BrOnCastDispatcher(
                RuntimeFusedControlInstruction.BrOnCastS(
                    operandSlot = operandSlot,
                    labelIndex = instruction.labelIndex,
                    dstReferenceType = instruction.dstReferenceType,
                    takenInstructions = takenInstructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictBrOnCastFailInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.BrOnCastFail,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val takenInstructions = predecodeTakenInstructions(context, instruction.takenInstructions).bind()
    preResolveCastType(context, instruction.dstReferenceType)

    when {
        operandSlot != null -> {
            BrOnCastFailDispatcher(
                RuntimeFusedControlInstruction.BrOnCastFailS(
                    operandSlot = operandSlot,
                    labelIndex = instruction.labelIndex,
                    dstReferenceType = instruction.dstReferenceType,
                    takenInstructions = takenInstructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun strictIfInstruction(
    context: PredecodingContext,
    instruction: ControlSuperInstruction.If,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val operandImmediate = strictControlImmediate(instruction.operand)
    val operandSlot = strictControlOperandSlot(instruction.operand)
    val functionType = BlockTypeExpander(context.runtimeTypes, instruction.blockType)
        .toResultOr { InstantiationError.PredecodingError }
        .bind()
    val instructions = predecodeIfInstructions(context, instruction.thenInstructions, instruction.elseInstructions).bind()

    when {
        operandImmediate != null -> {
            IfDispatcher(
                RuntimeFusedControlInstruction.IfI(
                    operand = operandImmediate,
                    params = functionType.params.types.size,
                    results = functionType.results.types.size,
                    instructions = instructions,
                ),
            )
        }

        operandSlot != null -> {
            IfDispatcher(
                RuntimeFusedControlInstruction.IfS(
                    operandSlot = operandSlot,
                    params = functionType.params.types.size,
                    results = functionType.results.types.size,
                    instructions = instructions,
                ),
            )
        }

        else -> unsupportedUnloweredControlInstruction()
    }
}

private fun preResolveCastType(
    context: PredecodingContext,
    referenceType: io.github.charlietap.chasm.type.ReferenceType,
) {
    when (val heapType = referenceType.heapType) {
        is ConcreteHeapType.TypeIndex -> context.instance.runtimeTypes[heapType.index].hydrate()
        else -> Unit
    }
}

private fun predecodeIfInstructions(
    context: PredecodingContext,
    thenInstructions: List<Instruction>,
    elseInstructions: List<Instruction>?,
): Result<Array<Array<DispatchableInstruction>>, ModuleTrapError> = binding {
    val thenDispatchableInstructions = InstructionSequencePredecoder(context, thenInstructions).bind()
    val elseDispatchableInstructions = elseInstructions?.let { instructions ->
        InstructionSequencePredecoder(context, instructions).bind()
    } ?: emptyArray()

    arrayOf(elseDispatchableInstructions, thenDispatchableInstructions)
}

private fun strictControlImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun strictControlIndexImmediate(
    operand: FusedOperand,
): Int? = strictControlImmediate(operand)?.toInt()

private fun strictControlOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictCallOperands(
    operands: List<FusedOperand>,
): List<RuntimeFusedControlInstruction.CallOperand> = operands.map { operand ->
    val immediate = strictControlImmediate(operand)
    if (immediate != null) {
        RuntimeFusedControlInstruction.CallOperand.Immediate(immediate)
    } else {
        val slot = strictControlOperandSlot(operand) ?: error(
            "control fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode",
        )
        RuntimeFusedControlInstruction.CallOperand.Slot(slot)
    }
}

private fun strictControlOperandSlots(
    operands: List<FusedOperand>,
    instructionName: String,
): List<Int> = operands.map { operand ->
    strictControlOperandSlot(operand)
        ?: error("$instructionName operands must lower to frame slots before predecode: $operand")
}

private fun predecodeTakenInstructions(
    context: PredecodingContext,
    instructions: List<Instruction>,
): Result<List<DispatchableInstruction>, ModuleTrapError> = binding {
    InstructionSequencePredecoderList(context, instructions).bind()
}

private fun predecodeTakenInstructionGroups(
    context: PredecodingContext,
    instructionGroups: List<List<Instruction>>,
): Result<List<List<DispatchableInstruction>>, ModuleTrapError> = binding {
    instructionGroups.map { instructions ->
        predecodeTakenInstructions(context, instructions).bind()
    }
}

private fun unsupportedUnloweredControlInstruction(): DispatchableInstruction =
    error("control fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
