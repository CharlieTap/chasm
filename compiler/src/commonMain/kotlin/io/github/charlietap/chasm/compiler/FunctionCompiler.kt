package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
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
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.context.global
import io.github.charlietap.chasm.compiler.instruction.emitCopy
import io.github.charlietap.chasm.compiler.instruction.emitDeferredBranchPaths
import io.github.charlietap.chasm.compiler.instruction.emitF32Constant
import io.github.charlietap.chasm.compiler.instruction.emitF64Constant
import io.github.charlietap.chasm.compiler.instruction.emitGlobalSet
import io.github.charlietap.chasm.compiler.instruction.emitI32Constant
import io.github.charlietap.chasm.compiler.instruction.emitI64Constant
import io.github.charlietap.chasm.compiler.operand.FrameAllocator
import io.github.charlietap.chasm.compiler.operand.FunctionFrameLayout
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.f32Immediate
import io.github.charlietap.chasm.compiler.operand.f64Immediate
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.compiler.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.program.ProgramFragment
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.type.ValueType
import kotlin.jvm.JvmInline
import io.github.charlietap.chasm.runtime.function.Function as RuntimeFunction

internal fun FunctionCompiler(
    context: CompilerContext,
    function: Function,
    program: Program,
    workspace: FunctionCompilerWorkspace = FunctionCompilerWorkspace(),
    programBuilder: ProgramBuilder = ProgramBuilder(program),
): Result<RuntimeFunction, ModuleTrapError> {
    val baseIp = program.size
    val result: Result<RuntimeFunction, ModuleTrapError> = run compile@{
        val functionType = context.types.functionType(function.typeIndex)
        val layout = FunctionFrameLayout(
            functionType = functionType,
            definedLocals = function.locals,
        )
        val state = FunctionCompilationContext(
            compiler = context,
            workspace = workspace,
            layout = layout,
            frame = FrameAllocator(layout.temporarySlotBase),
            program = programBuilder,
        )
        beginFunctionControl(state)

        var index = 0
        while (index < function.body.instructions.size) {
            val instruction = function.body.instructions[index]
            val nextInstruction = function.body.instructions.getOrNull(index + 1)
            val consumedInstructionCount = compileInstructionChain(context, state, function, index) ?: run {
                val consumesNextInstruction = if (!state.reachable && instruction !is ControlInstruction) {
                    false
                } else {
                    when (instruction) {
                        is NumericInstruction.I32Const -> {
                            state.pushI32(I32_TYPE, state.frame.allocate(), instruction.value)
                            false
                        }
                        is NumericInstruction.I64Const -> {
                            state.pushI64(I64_TYPE, state.frame.allocate(), instruction.value)
                            false
                        }
                        is NumericInstruction.F32Const -> {
                            state.pushF32(F32_TYPE, state.frame.allocate(), instruction.value)
                            false
                        }
                        is NumericInstruction.F64Const -> {
                            state.pushF64(F64_TYPE, state.frame.allocate(), instruction.value)
                            false
                        }
                        is NumericInstruction.Operator -> compileNumericInstruction(
                            state = state,
                            instruction = instruction,
                            nextInstruction = nextInstruction,
                        )
                        ParametricInstruction.Drop -> {
                            state.pop()
                            false
                        }
                        ParametricInstruction.Select,
                        is ParametricInstruction.SelectWithType,
                        -> compileSelectInstruction(
                            state = state,
                            nextInstruction = nextInstruction,
                        )
                        is VariableInstruction.LocalGet -> {
                            val localIndex = instruction.localIdx.toInt()
                            state.pushLocal(
                                type = layout.localType(localIndex),
                                reservedSlot = state.frame.allocate(),
                                localIndex = localIndex,
                                sourceSlot = layout.localSlot(localIndex),
                            )
                            false
                        }
                        is VariableInstruction.LocalSet -> {
                            val localIndex = instruction.localIdx.toInt()
                            val operand = state.pop()
                            state.preserveLocal(localIndex)
                            emitOperand(state, operand, layout.localSlot(localIndex))
                            false
                        }
                        is VariableInstruction.LocalTee -> {
                            val localIndex = instruction.localIdx.toInt()
                            val operand = state.pop()
                            val destinationSlot = layout.localSlot(localIndex)
                            state.preserveLocal(localIndex)
                            emitOperand(state, operand, destinationSlot)
                            state.pushLocal(
                                type = operand.type,
                                reservedSlot = state.frame.allocate(),
                                localIndex = localIndex,
                                sourceSlot = destinationSlot,
                            )
                            false
                        }
                        is VariableInstruction.GlobalGet -> compileGlobalGetInstruction(
                            state = state,
                            instruction = instruction,
                            nextInstruction = nextInstruction,
                        )
                        is VariableInstruction.GlobalSet -> {
                            val operand = state.pop()
                            state.emitGlobalSet(
                                global = context.global(instruction.globalIdx),
                                source = operand,
                            )
                            false
                        }
                        is ReferenceInstruction -> compileReferenceInstruction(
                            state = state,
                            instruction = instruction,
                            nextInstruction = nextInstruction,
                        )
                        is MemoryInstruction -> compileMemoryInstruction(
                            state = state,
                            instruction = instruction,
                            nextInstruction = nextInstruction,
                        )
                        is TableInstruction -> compileTableInstruction(
                            state = state,
                            instruction = instruction,
                            nextInstruction = nextInstruction,
                        )
                        is AggregateInstruction -> compileAggregateInstruction(
                            state = state,
                            instruction = instruction,
                            nextInstruction = nextInstruction,
                        )
                        is ControlInstruction -> compileControlInstruction(state, instruction, nextInstruction)
                        is AtomicMemoryInstruction -> return@compile Err(InstantiationError.UnsupportedThreadsModule)
                        is VectorInstruction -> return@compile Err(InstantiationError.UnsupportedSIMDModule)
                    }
                }
                if (consumesNextInstruction) 2 else 1
            }

            state.rewindFrame()
            index += consumedInstructionCount
        }

        finishFunctionControl(state)

        state.emitDeferredBranchPaths()
        state.finishProgram()
        Ok(
            RuntimeFunction(
                idx = function.idx,
                typeIndex = function.typeIndex,
                locals = LongArray(function.locals.size) { localIndex ->
                    function.locals[localIndex].type.default()
                },
                body = Expression(baseIp),
                frameSlots = state.frame.maxSlotExclusive,
                returnSlots = layout.returnSlots,
            ),
        )
    }
    if (result.isErr) {
        program.truncate(baseIp)
    }
    return result
}

internal fun FunctionCompiler(
    context: CompilerContext,
    function: Function,
    workspace: FunctionCompilerWorkspace,
): Result<FunctionCompilation, ModuleTrapError> = binding {
    val program = Program(maxOf(function.body.instructions.size, 1))
    val builder = ProgramBuilder(program, recordRelocations = true)
    val compiled = FunctionCompiler(context, function, program, workspace, builder).bind()
    FunctionCompilation(
        function = compiled,
        program = builder.fragment(),
    )
}

internal class FunctionCompilation(
    val function: RuntimeFunction,
    val program: ProgramFragment,
)

private fun compileInstructionChain(
    context: CompilerContext,
    state: FunctionCompilationContext,
    function: Function,
    index: Int,
): Int? {
    if (!state.reachable) return null

    val instructions = function.body.instructions
    val instruction = instructions[index]
    val nextInstruction = instructions.getOrNull(index + 1)
    if (
        instruction is NumericInstruction.Operator &&
        nextInstruction is NumericInstruction.I32Const &&
        instructions.getOrNull(index + 2) is NumericInstruction.Operator
    ) {
        compileNumericChain(
            state = state,
            first = instruction,
            immediate = nextInstruction,
            second = instructions[index + 2] as NumericInstruction.Operator,
            nextInstruction = instructions.getOrNull(index + 3),
        )?.let { return it }
    }

    if (instruction is ReferenceInstruction.RefFunc) {
        when (nextInstruction) {
            is ControlInstruction.CallRef -> {
                val consumesNextInstruction = compileKnownReferenceCall(
                    state,
                    instruction,
                    nextInstruction,
                    instructions.getOrNull(index + 2),
                )
                return if (consumesNextInstruction) 3 else 2
            }
            is ControlInstruction.ReturnCallRef -> {
                compileKnownReferenceReturnCall(state, instruction, nextInstruction)
                return 2
            }
            else -> Unit
        }
    }

    if (nextInstruction is AggregateInstruction.StructGet) {
        compileAggregateAccessChain(
            state = state,
            first = instruction,
            second = nextInstruction,
            nextInstruction = instructions.getOrNull(index + 2),
        )?.let { return it }
    }

    val conditionInstruction = instruction as? NumericInstruction.Operator ?: return null
    if (
        (conditionInstruction.opcode.isCondition || conditionInstruction.opcode == NumericOpcode.I32And) &&
        (nextInstruction is ControlInstruction.BrIf || nextInstruction is ControlInstruction.If)
    ) {
        val condition = if (conditionInstruction.opcode == NumericOpcode.I32And) {
            state.popI32AndCondition()
        } else {
            state.popNumericCondition(conditionInstruction)
        }
        when (nextInstruction) {
            is ControlInstruction.BrIf -> compileBranchIfCondition(state, nextInstruction, condition)
            is ControlInstruction.If -> compileIfCondition(state, nextInstruction, condition)
        }
        return 2
    }
    return null
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

    val consumesNextInstruction: Boolean
        get() = localIndex >= 0

    companion object {
        fun frame(slot: Int): Destination = Destination(slot.toLong() and SLOT_MASK)

        fun local(slot: Int, localIndex: Int, retainsValue: Boolean): Destination {
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
    nextInstruction: io.github.charlietap.chasm.ast.instruction.Instruction?,
): Destination {
    when (nextInstruction) {
        is VariableInstruction.LocalSet -> {
            val localIndex = nextInstruction.localIdx.toInt()
            state.preserveLocal(localIndex)
            return Destination.local(
                slot = state.layout.localSlot(localIndex),
                localIndex = localIndex,
                retainsValue = false,
            )
        }
        is VariableInstruction.LocalTee -> {
            val localIndex = nextInstruction.localIdx.toInt()
            state.preserveLocal(localIndex)
            return Destination.local(
                slot = state.layout.localSlot(localIndex),
                localIndex = localIndex,
                retainsValue = true,
            )
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

internal fun completeDestination(
    state: FunctionCompilationContext,
    type: ValueType,
    destination: Destination,
) {
    if (destination.consumesNextInstruction && !destination.retainsValue) return

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
