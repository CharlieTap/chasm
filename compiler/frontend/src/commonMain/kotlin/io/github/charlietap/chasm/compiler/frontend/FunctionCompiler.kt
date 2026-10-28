package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.frontend.context.CompilerContext
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.frontend.context.InstructionCursor
import io.github.charlietap.chasm.compiler.frontend.context.global
import io.github.charlietap.chasm.compiler.frontend.instruction.emitCheckpoint
import io.github.charlietap.chasm.compiler.frontend.instruction.emitCopy
import io.github.charlietap.chasm.compiler.frontend.instruction.emitDeferredBranchPaths
import io.github.charlietap.chasm.compiler.frontend.instruction.emitF32Constant
import io.github.charlietap.chasm.compiler.frontend.instruction.emitF64Constant
import io.github.charlietap.chasm.compiler.frontend.instruction.emitGlobalSet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitI32Constant
import io.github.charlietap.chasm.compiler.frontend.instruction.emitI64Constant
import io.github.charlietap.chasm.compiler.frontend.operand.DefinedLocalStorage
import io.github.charlietap.chasm.compiler.frontend.operand.FrameAllocator
import io.github.charlietap.chasm.compiler.frontend.operand.FunctionFrameLayout
import io.github.charlietap.chasm.compiler.frontend.operand.Operand
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.f32Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.f64Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.i32Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.i64Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot
import io.github.charlietap.chasm.compiler.frontend.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.frontend.program.SourceProvenanceRecorder
import io.github.charlietap.chasm.type.ValueType
import kotlin.jvm.JvmInline

internal typealias FunctionCompiler = (CompilerContext, Function, FunctionCompilerWorkspace) -> Result<CirFunction, CompilationError>

internal fun FunctionCompiler(
    context: CompilerContext,
    function: Function,
    workspace: FunctionCompilerWorkspace = FunctionCompilerWorkspace(),
    recordProvenance: Boolean = false,
): Result<CirFunction, CompilationError> {
    val functionType = context.functionTypeAt(function.typeIndex.toInt())
    val body = function.body.instructions
    val instructions = workspace.body(body)
    val definedLocalStorage = DefinedLocalStorage(instructions, body.size, function.locals, functionType.params.types.size)
    val layout = FunctionFrameLayout(
        functionType = functionType,
        definedLocals = function.locals,
        definedLocalStorage = definedLocalStorage,
    )
    val programBuilder = workspace.program.also(ProgramBuilder::reset)
    val provenance = if (recordProvenance) SourceProvenanceRecorder().also { programBuilder.provenance = it } else null
    val state = FunctionCompilationContext(
        compiler = context,
        workspace = workspace,
        layout = layout,
        frame = FrameAllocator(layout.temporarySlotBase),
        program = programBuilder,
        instructions = InstructionCursor(instructions, body.size),
    )
    beginFunctionControl(state)
    state.emitCheckpoint()

    val cursor = state.instructions
    while (cursor.advance()) {
        provenance?.beginStep(cursor.index)
        compileInstruction(state, cursor.current)?.let { error -> return Err(error) }
        state.rewindFrame()
        provenance?.endStep(cursor.nextIndex)
    }

    finishFunctionControl(state)

    state.emitDeferredBranchPaths()
    state.finishProgram()
    return Ok(
        CirFunction(
            index = FunctionIndex(function.idx.toInt()),
            type = TypeIndex(function.typeIndex.toInt()),
            frame = Frame(
                frameSlots = state.frame.maxSlotExclusive,
                activationHeaderSlot = layout.activationHeaderSlot,
                locals = layout.localTypes(),
                initialisedLocalCount = layout.initialisedLocalCount(),
            ),
            instructions = programBuilder.instructions(),
            checkpoints = programBuilder.checkpoints(),
            exceptions = state.exceptionTableBuilder?.finish(programBuilder),
            provenance = provenance?.provenance(programBuilder.size),
        ),
    )
}

private fun compileInstruction(
    state: FunctionCompilationContext,
    instruction: Any?,
): CompilationError? {
    if (compileInstructionChain(state, instruction)) return null
    if (!state.reachable && instruction !is ControlInstruction) return null
    when (instruction) {
        is VariableInstruction.LocalGet -> {
            val localIndex = instruction.localIdx.toInt()
            state.pushLocal(
                type = state.layout.localType(localIndex),
                reservedSlot = state.frame.allocate(),
                localIndex = localIndex,
                sourceSlot = state.layout.localSlot(localIndex),
            )
        }
        is NumericInstruction.Operator -> compileNumericInstruction(
            state = state,
            instruction = instruction,
        )
        is NumericInstruction.I32Const -> {
            state.pushI32(I32_TYPE, state.frame.allocate(), instruction.value)
        }
        is VariableInstruction.LocalSet -> {
            val localIndex = instruction.localIdx.toInt()
            val operand = state.pop()
            state.preserveLocal(localIndex)
            emitOperand(state, operand, state.layout.localSlot(localIndex))
        }
        is VariableInstruction.LocalTee -> {
            val localIndex = instruction.localIdx.toInt()
            val operand = state.pop()
            val destinationSlot = state.layout.localSlot(localIndex)
            state.preserveLocal(localIndex)
            emitOperand(state, operand, destinationSlot)
            state.pushLocal(
                type = operand.type,
                reservedSlot = state.frame.allocate(),
                localIndex = localIndex,
                sourceSlot = destinationSlot,
            )
        }
        is ControlInstruction -> compileControlInstruction(state, instruction)
        is MemoryInstruction -> compileMemoryInstruction(
            state = state,
            instruction = instruction,
        )
        is VariableInstruction.GlobalGet -> compileGlobalGetInstruction(
            state = state,
            instruction = instruction,
        )
        is VariableInstruction.GlobalSet -> {
            val operand = state.pop()
            state.emitGlobalSet(
                global = state.compiler.global(instruction.globalIdx),
                source = operand,
            )
        }
        is NumericInstruction.I64Const -> {
            state.pushI64(I64_TYPE, state.frame.allocate(), instruction.value)
        }
        is ParametricInstruction.Drop -> {
            state.pop()
        }
        is ParametricInstruction.Select,
        is ParametricInstruction.SelectWithType,
        -> compileSelectInstruction(
            state = state,
        )
        is NumericInstruction.F32Const -> {
            state.pushF32(F32_TYPE, state.frame.allocate(), instruction.value)
        }
        is NumericInstruction.F64Const -> {
            state.pushF64(F64_TYPE, state.frame.allocate(), instruction.value)
        }
        is ReferenceInstruction -> compileReferenceInstruction(
            state = state,
            instruction = instruction,
        )
        is AggregateInstruction -> compileAggregateInstruction(
            state = state,
            instruction = instruction,
        )
        is TableInstruction -> compileTableInstruction(
            state = state,
            instruction = instruction,
        )
        is AtomicMemoryInstruction -> return CompilationError.UnsupportedThreads
        is VectorInstruction -> return CompilationError.UnsupportedSimd
        else -> error("unknown instruction $instruction")
    }
    return null
}

private fun compileInstructionChain(
    state: FunctionCompilationContext,
    instruction: Any?,
): Boolean {
    if (!state.reachable) return false

    val cursor = state.instructions
    val nextInstruction = cursor.peek(1)
    if (instruction is NumericInstruction.Operator && nextInstruction is NumericInstruction.I32Const) {
        val second = cursor.peek(2)
        if (second is NumericInstruction.Operator && compileNumericChain(state, instruction, nextInstruction, second)) return true
    }

    if (instruction is ReferenceInstruction.RefFunc) {
        when (nextInstruction) {
            is ControlInstruction.CallRef -> {
                cursor.consume()
                compileKnownReferenceCall(state, instruction, nextInstruction)
                return true
            }
            is ControlInstruction.ReturnCallRef -> {
                cursor.consume()
                compileKnownReferenceReturnCall(state, instruction, nextInstruction)
                return true
            }
            else -> Unit
        }
    }

    if (nextInstruction is AggregateInstruction.StructGet && compileAggregateAccessChain(state, instruction, nextInstruction)) {
        return true
    }

    val conditionInstruction = instruction as? NumericInstruction.Operator ?: return false
    if (
        (conditionInstruction.opcode.isCondition || conditionInstruction.opcode == NumericOpcode.I32And) &&
        (nextInstruction is ControlInstruction.BrIf || nextInstruction is ControlInstruction.If)
    ) {
        cursor.consume()
        val condition = if (conditionInstruction.opcode == NumericOpcode.I32And) {
            state.popI32AndCondition()
        } else {
            state.popNumericCondition(conditionInstruction)
        }
        when (nextInstruction) {
            is ControlInstruction.BrIf -> compileBranchIfCondition(state, nextInstruction, condition)
            is ControlInstruction.If -> compileIfCondition(state, nextInstruction, condition)
        }
        return true
    }
    return false
}

@JvmInline
internal value class Destination private constructor(
    private val encoded: Long,
) {
    val slot: Int
        get() = encoded.toInt()

    val localIndex: Int
        get() = ((encoded ushr LOCAL_INDEX_SHIFT).toInt() and LOCAL_INDEX_MASK) - 1

    val retainsValue: Boolean
        get() = encoded < 0

    val isLocal: Boolean
        get() = localIndex >= 0

    companion object {
        fun frame(
            slot: Int,
        ): Destination = Destination(slot.toLong() and SLOT_MASK)

        fun local(
            slot: Int,
            localIndex: Int,
            retainsValue: Boolean,
        ): Destination {
            check(localIndex in 0 until LOCAL_INDEX_MASK)
            val encodedLocalIndex = (localIndex + 1).toLong() shl LOCAL_INDEX_SHIFT
            val retainedValueFlag = if (retainsValue) RETAINED_VALUE_MASK else 0L
            return Destination((slot.toLong() and SLOT_MASK) or encodedLocalIndex or retainedValueFlag)
        }

        private const val LOCAL_INDEX_SHIFT = Int.SIZE_BITS
        private const val LOCAL_INDEX_MASK = Int.MAX_VALUE
        private const val SLOT_MASK = 0xFFFF_FFFFL
        private const val RETAINED_VALUE_MASK = Long.MIN_VALUE
    }
}

internal fun destination(
    state: FunctionCompilationContext,
    reusableOperand: Operand?,
    allowRootResult: Boolean = true,
): Destination {
    when (val nextInstruction = state.instructions.next()) {
        is VariableInstruction.LocalSet -> {
            val localIndex = nextInstruction.localIdx.toInt()
            state.instructions.consume()
            state.preserveLocal(localIndex)
            return Destination.local(
                slot = state.layout.localSlot(localIndex),
                localIndex = localIndex,
                retainsValue = false,
            )
        }
        is VariableInstruction.LocalTee -> {
            val localIndex = nextInstruction.localIdx.toInt()
            state.instructions.consume()
            state.preserveLocal(localIndex)
            return Destination.local(
                slot = state.layout.localSlot(localIndex),
                localIndex = localIndex,
                retainsValue = true,
            )
        }
        null -> if (allowRootResult && state.controls.isEmpty()) {
            rootResultDestination(state)?.let { return it }
        }
        else -> Unit
    }

    if (reusableOperand != null) {
        val reusableSlot = reusableOperand.reservedSlot
        if (state.frame.isTemporary(reusableSlot) && !state.operands.containsReservedSlot(reusableSlot)) {
            return Destination.frame(reusableSlot)
        }
    }
    return Destination.frame(state.frame.allocate())
}

private fun rootResultDestination(
    state: FunctionCompilationContext,
): Destination? {
    val root = state.rootControl ?: return null
    val resultIndex = state.operands.size - root.baseHeight
    if (resultIndex !in root.resultSlots.indices) return null
    val resultSlot = root.resultSlots[resultIndex]
    if (resultSlot < state.layout.parameterTypes.size) {
        state.preserveLocal(resultSlot)
    }
    return Destination.frame(resultSlot)
}

internal fun completeDestination(
    state: FunctionCompilationContext,
    type: ValueType,
    destination: Destination,
) {
    if (destination.isLocal && !destination.retainsValue) return

    if (destination.retainsValue) {
        state.pushLocal(type, state.frame.allocate(), destination.localIndex, destination.slot)
    } else {
        state.pushFrame(type, destination.slot)
    }
}

internal fun emitOperand(
    state: FunctionCompilationContext,
    operand: Operand,
    destinationSlot: Int,
) {
    when (operand.sourceKind) {
        OperandSourceKind.I32Immediate -> state.emitI32Constant(operand.i32Immediate, destinationSlot)
        OperandSourceKind.I64Immediate -> state.emitI64Constant(operand.i64Immediate, destinationSlot)
        OperandSourceKind.F32Immediate -> state.emitF32Constant(operand.sourceBits.toInt(), destinationSlot)
        OperandSourceKind.F64Immediate -> state.emitF64Constant(operand.sourceBits, destinationSlot)
        OperandSourceKind.Local,
        OperandSourceKind.Frame,
        -> state.emitCopy(operand.sourceSlot, destinationSlot)
    }
}
