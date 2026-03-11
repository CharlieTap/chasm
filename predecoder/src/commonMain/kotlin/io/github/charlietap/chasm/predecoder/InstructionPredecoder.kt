package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
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
import io.github.charlietap.chasm.ir.instruction.VectorInstruction
import io.github.charlietap.chasm.predecoder.instruction.admin.AdminInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.aggregate.AggregateInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.aggregatefused.AggregateSuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.atomic.AtomicMemoryInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.control.ControlInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.controlfused.ControlSuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.memory.MemoryInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.memoryfused.MemorySuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.numeric.NumericInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.numericfused.NumericSuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.parametric.ParametricInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.parametricfused.ParametricSuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.reference.ReferenceInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.referencefused.ReferenceSuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.table.TableInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.tablefused.TableSuperInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.variable.VariableInstructionPredecoder
import io.github.charlietap.chasm.predecoder.instruction.variablefused.VariableSuperInstructionPredecoder
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
        adminInstructionPredecoder = ::AdminInstructionPredecoder,
        aggregateInstructionPredecoder = ::AggregateInstructionPredecoder,
        atomicMemoryInstructionPredecoder = ::AtomicMemoryInstructionPredecoder,
        controlInstructionPredecoder = ::ControlInstructionPredecoder,
        aggregateSuperInstruction = ::AggregateSuperInstructionPredecoder,
        controlSuperInstructionPredecoder = ::ControlSuperInstructionPredecoder,
        numericSuperInstructionPredecoder = ::NumericSuperInstructionPredecoder,
        memorySuperInstructionPredecoder = ::MemorySuperInstructionPredecoder,
        parametricSuperInstructionPredecoder = ::ParametricSuperInstructionPredecoder,
        referenceSuperInstructionPredecoder = ::ReferenceSuperInstructionPredecoder,
        tableSuperInstructionPredecoder = ::TableSuperInstructionPredecoder,
        variableSuperInstructionPredecoder = ::VariableSuperInstructionPredecoder,
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
    crossinline adminInstructionPredecoder: Predecoder<AdminInstruction, DispatchableInstruction>,
    crossinline aggregateInstructionPredecoder: Predecoder<AggregateInstruction, DispatchableInstruction>,
    crossinline atomicMemoryInstructionPredecoder: Predecoder<AtomicMemoryInstruction, DispatchableInstruction>,
    crossinline controlInstructionPredecoder: Predecoder<ControlInstruction, DispatchableInstruction>,
    crossinline aggregateSuperInstruction: Predecoder<AggregateSuperInstruction, DispatchableInstruction>,
    crossinline controlSuperInstructionPredecoder: Predecoder<ControlSuperInstruction, DispatchableInstruction>,
    crossinline numericSuperInstructionPredecoder: Predecoder<NumericSuperInstruction, DispatchableInstruction>,
    crossinline memorySuperInstructionPredecoder: Predecoder<MemorySuperInstruction, DispatchableInstruction>,
    crossinline parametricSuperInstructionPredecoder: Predecoder<ParametricSuperInstruction, DispatchableInstruction>,
    crossinline referenceSuperInstructionPredecoder: Predecoder<ReferenceSuperInstruction, DispatchableInstruction>,
    crossinline tableSuperInstructionPredecoder: Predecoder<TableSuperInstruction, DispatchableInstruction>,
    crossinline variableSuperInstructionPredecoder: Predecoder<VariableSuperInstruction, DispatchableInstruction>,
    crossinline memoryInstructionPredecoder: Predecoder<MemoryInstruction, DispatchableInstruction>,
    crossinline numericInstructionPredecoder: Predecoder<NumericInstruction, DispatchableInstruction>,
    crossinline parametricInstructionPredecoder: Predecoder<ParametricInstruction, DispatchableInstruction>,
    crossinline referenceInstructionPredecoder: Predecoder<ReferenceInstruction, DispatchableInstruction>,
    crossinline tableInstructionPredecoder: Predecoder<TableInstruction, DispatchableInstruction>,
    crossinline variableInstructionPredecoder: Predecoder<VariableInstruction, DispatchableInstruction>,
    crossinline vectorInstructionPredecoder: Predecoder<VectorInstruction, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val cacheKey = context.instructionCacheKey(instruction)

    var dispatchable = context.instructionCache[cacheKey]

    if (dispatchable == null) {
        dispatchable = when (instruction) {
            is AdminInstruction -> adminInstructionPredecoder(context, instruction).bind()
            is AggregateInstruction -> aggregateInstructionPredecoder(context, instruction).bind()
            is AtomicMemoryInstruction -> atomicMemoryInstructionPredecoder(context, instruction).bind()
            is ControlInstruction -> controlInstructionPredecoder(context, instruction).bind()
            is MemoryInstruction -> memoryInstructionPredecoder(context, instruction).bind()
            is NumericInstruction -> numericInstructionPredecoder(context, instruction).bind()
            is AggregateSuperInstruction -> aggregateSuperInstruction(context, instruction).bind()
            is ControlSuperInstruction -> controlSuperInstructionPredecoder(context, instruction).bind()
            is NumericSuperInstruction -> numericSuperInstructionPredecoder(context, instruction).bind()
            is MemorySuperInstruction -> memorySuperInstructionPredecoder(context, instruction).bind()
            is ParametricSuperInstruction -> parametricSuperInstructionPredecoder(context, instruction).bind()
            is ReferenceSuperInstruction -> referenceSuperInstructionPredecoder(context, instruction).bind()
            is TableSuperInstruction -> tableSuperInstructionPredecoder(context, instruction).bind()
            is VariableSuperInstruction -> variableSuperInstructionPredecoder(context, instruction).bind()
            is ParametricInstruction -> parametricInstructionPredecoder(context, instruction).bind()
            is ReferenceInstruction -> referenceInstructionPredecoder(context, instruction).bind()
            is TableInstruction -> tableInstructionPredecoder(context, instruction).bind()
            is VariableInstruction -> variableInstructionPredecoder(context, instruction).bind()
            is VectorInstruction -> vectorInstructionPredecoder(context, instruction).bind()
        }
        context.instructionCache[cacheKey] = dispatchable
    }

    dispatchable
}
