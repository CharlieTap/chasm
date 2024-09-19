package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AggregateInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ControlInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.NumericInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.ParametricInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.ReferenceInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variable.VariableInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ModuleInstructionExecutor = (ModuleInstruction, Store, Stack) -> Result<Unit, InvocationError>

internal fun ModuleInstructionExecutor(
    instruction: ModuleInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    ModuleInstructionExecutor(
        instruction = instruction,
        store = store,
        stack = stack,
        aggregateInstructionExecutor = ::AggregateInstructionExecutor,
        controlInstructionExecutor = ::ControlInstructionExecutor,
        memoryInstructionExecutor = ::MemoryInstructionExecutor,
        numericInstructionExecutor = ::NumericInstructionExecutor,
        parametricInstructionExecutor = ::ParametricInstructionExecutor,
        tableInstructionExecutor = ::TableInstructionExecutor,
        referenceInstructionExecutor = ::ReferenceInstructionExecutor,
        variableInstructionExecutor = ::VariableInstructionExecutor,
    )

internal fun ModuleInstructionExecutor(
    instruction: ModuleInstruction,
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
    when (val moduleInstruction = instruction.instruction) {
        is AggregateInstruction -> aggregateInstructionExecutor(moduleInstruction, store, stack).bind()
        is ControlInstruction -> controlInstructionExecutor(moduleInstruction, store, stack).bind()
        is MemoryInstruction -> memoryInstructionExecutor(moduleInstruction, store, stack).bind()
        is NumericInstruction -> numericInstructionExecutor(moduleInstruction, stack).bind()
        is ParametricInstruction -> parametricInstructionExecutor(moduleInstruction, stack).bind()
        is TableInstruction -> tableInstructionExecutor(moduleInstruction, store, stack).bind()
        is ReferenceInstruction -> referenceInstructionExecutor(moduleInstruction, store, stack).bind()
        is VariableInstruction -> variableInstructionExecutor(moduleInstruction, store, stack).bind()
        is VectorInstruction -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
