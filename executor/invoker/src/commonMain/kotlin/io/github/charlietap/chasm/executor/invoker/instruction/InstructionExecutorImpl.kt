package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AggregateInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AggregateInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.control.ControlInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ControlInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.NumericInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.NumericInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.ParametricInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.ParametricInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.reference.ReferenceInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.ReferenceInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInstructionExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.variable.VariableInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variable.VariableInstructionExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun InstructionExecutorImpl(
    instruction: Instruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    InstructionExecutorImpl(
        instruction = instruction,
        store = store,
        stack = stack,
        aggregateInstructionExecutor = ::AggregateInstructionExecutorImpl,
        controlInstructionExecutor = ::ControlInstructionExecutorImpl,
        memoryInstructionExecutor = ::MemoryInstructionExecutorImpl,
        numericInstructionExecutor = ::NumericInstructionExecutorImpl,
        parametricInstructionExecutor = ::ParametricInstructionExecutorImpl,
        tableInstructionExecutor = ::TableInstructionExecutorImpl,
        referenceInstructionExecutor = ::ReferenceInstructionExecutorImpl,
        variableInstructionExecutor = ::VariableInstructionExecutorImpl,
    )

internal fun InstructionExecutorImpl(
    instruction: Instruction,
    store: Store,
    stack: Stack,
    aggregateInstructionExecutor: AggregateInstructionExecutor,
    controlInstructionExecutor: ControlInstructionExecutor,
    memoryInstructionExecutor: MemoryInstructionExecutor,
    numericInstructionExecutor: NumericInstructionExecutor,
    parametricInstructionExecutor: ParametricInstructionExecutor,
    tableInstructionExecutor: TableInstructionExecutor,
    referenceInstructionExecutor: ReferenceInstructionExecutor,
    variableInstructionExecutor: VariableInstructionExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AggregateInstruction -> aggregateInstructionExecutor(instruction, store, stack).bind()
        is ControlInstruction -> controlInstructionExecutor(instruction, store, stack).bind()
        is MemoryInstruction -> memoryInstructionExecutor(instruction, store, stack).bind()
        is NumericInstruction -> numericInstructionExecutor(instruction, stack).bind()
        is ParametricInstruction -> parametricInstructionExecutor(instruction, stack).bind()
        is TableInstruction -> tableInstructionExecutor(instruction, store, stack).bind()
        is ReferenceInstruction -> referenceInstructionExecutor(instruction, store, stack).bind()
        is VariableInstruction -> variableInstructionExecutor(instruction, store, stack).bind()
        else -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
