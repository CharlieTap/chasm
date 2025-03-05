package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.ir.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VectorInstruction
import io.github.charlietap.chasm.predecoder.instruction.aggregate.AggregateInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.aggregatefused.FusedAggregateInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.atomic.AtomicMemoryInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.control.ControlInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.controlfused.FusedControlInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.memory.MemoryInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.memoryfused.FusedMemoryInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.numeric.NumericInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.numericfused.FusedNumericInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.parametric.ParametricInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.parametricfused.FusedParametricInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.reference.ReferenceInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.referencefused.FusedReferenceInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.table.TableInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.tablefused.FusedTableInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.variable.VariableInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.variablefused.FusedVariableInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.vector.VectorInstructionPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun InstructionPredecoder(
    context: PredecodingContext,
    instruction: Instruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    InstructionPredecoder(
        context = context,
        instruction = instruction,
        aggregateInstructionPredecoder = ::AggregateInstructionPredecoder,
        atomicMemoryInstructionPredecoder = ::AtomicMemoryInstructionPredecoder,
        controlInstructionPredecoder = ::ControlInstructionPredecoder,
        fusedAggregateInstruction = ::FusedAggregateInstructionPredecoder,
        fusedControlInstructionPredecoder = ::FusedControlInstructionPredecoder,
        fusedNumericInstructionPredecoder = ::FusedNumericInstructionPredecoder,
        fusedMemoryInstructionPredecoder = ::FusedMemoryInstructionPredecoder,
        fusedParametricInstructionPredecoder = ::FusedParametricInstructionPredecoder,
        fusedReferenceInstructionPredecoder = ::FusedReferenceInstructionPredecoder,
        fusedTableInstructionPredecoder = ::FusedTableInstructionPredecoder,
        fusedVariableInstructionPredecoder = ::FusedVariableInstructionPredecoder,
        numericInstructionPredecoder = ::NumericInstructionPredecoder,
        memoryInstructionPredecoder = ::MemoryInstructionPredecoder,
        parametricInstructionPredecoder = ::ParametricInstructionPredecoder,
        referenceInstructionPredecoder = ::ReferenceInstructionPredecoder,
        tableInstructionPredecoder = ::TableInstructionPredecoder,
        variableInstructionPredecoder = ::VariableInstructionPredecoder,
        vectorInstructionPredecoder = ::VectorInstructionPredecoder,
    )

internal inline fun InstructionPredecoder(
    context: PredecodingContext,
    instruction: Instruction,
    crossinline aggregateInstructionPredecoder: Predecoder<AggregateInstruction, DispatchableInstruction>,
    crossinline atomicMemoryInstructionPredecoder: Predecoder<AtomicMemoryInstruction, DispatchableInstruction>,
    crossinline controlInstructionPredecoder: Predecoder<ControlInstruction, DispatchableInstruction>,
    crossinline fusedAggregateInstruction: Predecoder<FusedAggregateInstruction, DispatchableInstruction>,
    crossinline fusedControlInstructionPredecoder: Predecoder<FusedControlInstruction, DispatchableInstruction>,
    crossinline fusedNumericInstructionPredecoder: Predecoder<FusedNumericInstruction, DispatchableInstruction>,
    crossinline fusedMemoryInstructionPredecoder: Predecoder<FusedMemoryInstruction, DispatchableInstruction>,
    crossinline fusedParametricInstructionPredecoder: Predecoder<FusedParametricInstruction, DispatchableInstruction>,
    crossinline fusedReferenceInstructionPredecoder: Predecoder<FusedReferenceInstruction, DispatchableInstruction>,
    crossinline fusedTableInstructionPredecoder: Predecoder<FusedTableInstruction, DispatchableInstruction>,
    crossinline fusedVariableInstructionPredecoder: Predecoder<FusedVariableInstruction, DispatchableInstruction>,
    crossinline memoryInstructionPredecoder: Predecoder<MemoryInstruction, DispatchableInstruction>,
    crossinline numericInstructionPredecoder: Predecoder<NumericInstruction, DispatchableInstruction>,
    crossinline parametricInstructionPredecoder: Predecoder<ParametricInstruction, DispatchableInstruction>,
    crossinline referenceInstructionPredecoder: Predecoder<ReferenceInstruction, DispatchableInstruction>,
    crossinline tableInstructionPredecoder: Predecoder<TableInstruction, DispatchableInstruction>,
    crossinline variableInstructionPredecoder: Predecoder<VariableInstruction, DispatchableInstruction>,
    crossinline vectorInstructionPredecoder: Predecoder<VectorInstruction, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    var dispatchable = context.instructionCache[instruction]

    if (dispatchable == null) {
        dispatchable = when (instruction) {
            is AggregateInstruction -> aggregateInstructionPredecoder(context, instruction).bind()
            is AtomicMemoryInstruction -> atomicMemoryInstructionPredecoder(context, instruction).bind()
            is ControlInstruction -> controlInstructionPredecoder(context, instruction).bind()
            is MemoryInstruction -> memoryInstructionPredecoder(context, instruction).bind()
            is NumericInstruction -> numericInstructionPredecoder(context, instruction).bind()
            is FusedAggregateInstruction -> fusedAggregateInstruction(context, instruction).bind()
            is FusedControlInstruction -> fusedControlInstructionPredecoder(context, instruction).bind()
            is FusedNumericInstruction -> fusedNumericInstructionPredecoder(context, instruction).bind()
            is FusedMemoryInstruction -> fusedMemoryInstructionPredecoder(context, instruction).bind()
            is FusedParametricInstruction -> fusedParametricInstructionPredecoder(context, instruction).bind()
            is FusedReferenceInstruction -> fusedReferenceInstructionPredecoder(context, instruction).bind()
            is FusedTableInstruction -> fusedTableInstructionPredecoder(context, instruction).bind()
            is FusedVariableInstruction -> fusedVariableInstructionPredecoder(context, instruction).bind()
            is ParametricInstruction -> parametricInstructionPredecoder(context, instruction).bind()
            is ReferenceInstruction -> referenceInstructionPredecoder(context, instruction).bind()
            is TableInstruction -> tableInstructionPredecoder(context, instruction).bind()
            is VariableInstruction -> variableInstructionPredecoder(context, instruction).bind()
            is VectorInstruction -> vectorInstructionPredecoder(context, instruction).bind()
        }
        context.instructionCache[instruction] = dispatchable
    }

    dispatchable
}
