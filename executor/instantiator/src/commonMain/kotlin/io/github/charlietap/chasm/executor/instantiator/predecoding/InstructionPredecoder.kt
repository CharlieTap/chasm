package io.github.charlietap.chasm.executor.instantiator.predecoding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.aggregate.AggregateInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.atomic.AtomicMemoryInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.control.ControlInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.MemoryInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.numeric.NumericInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.parametric.ParametricInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.reference.ReferenceInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table.TableInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.variable.VariableInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.vector.VectorInstructionPredecoder
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError

internal fun InstructionPredecoder(
    context: InstantiationContext,
    instruction: Instruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    InstructionPredecoder(
        context = context,
        instruction = instruction,
        aggregateInstructionPredecoder = ::AggregateInstructionPredecoder,
        atomicMemoryInstructionPredecoder = ::AtomicMemoryInstructionPredecoder,
        controlInstructionPredecoder = ::ControlInstructionPredecoder,
        numericInstructionPredecoder = ::NumericInstructionPredecoder,
        memoryInstructionPredecoder = ::MemoryInstructionPredecoder,
        parametricInstructionPredecoder = ::ParametricInstructionPredecoder,
        referenceInstructionPredecoder = ::ReferenceInstructionPredecoder,
        tableInstructionPredecoder = ::TableInstructionPredecoder,
        variableInstructionPredecoder = ::VariableInstructionPredecoder,
        vectorInstructionPredecoder = ::VectorInstructionPredecoder,
    )

internal inline fun InstructionPredecoder(
    context: InstantiationContext,
    instruction: Instruction,
    crossinline aggregateInstructionPredecoder: Predecoder<AggregateInstruction, DispatchableInstruction>,
    crossinline atomicMemoryInstructionPredecoder: Predecoder<AtomicMemoryInstruction, DispatchableInstruction>,
    crossinline controlInstructionPredecoder: Predecoder<ControlInstruction, DispatchableInstruction>,
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
