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
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AggregateInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ControlInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.NumericInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.ParametricInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.ReferenceInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variable.VariableInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction

internal fun ModuleInstructionExecutor(
    context: ExecutionContext,
    instruction: ModuleInstruction,
): Result<Unit, InvocationError> =
    ModuleInstructionExecutor(
        context = context,
        instruction = instruction,
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
    context: ExecutionContext,
    instruction: ModuleInstruction,
    aggregateInstructionExecutor: Executor<AggregateInstruction>,
    controlInstructionExecutor: Executor<ControlInstruction>,
    memoryInstructionExecutor: Executor<MemoryInstruction>,
    numericInstructionExecutor: Executor<NumericInstruction>,
    parametricInstructionExecutor: Executor<ParametricInstruction>,
    tableInstructionExecutor: Executor<TableInstruction>,
    referenceInstructionExecutor: Executor<ReferenceInstruction>,
    variableInstructionExecutor: Executor<VariableInstruction>,
): Result<Unit, InvocationError> = binding {
    when (val moduleInstruction = instruction.instruction) {
        is AggregateInstruction -> aggregateInstructionExecutor(context, moduleInstruction).bind()
        is ControlInstruction -> controlInstructionExecutor(context, moduleInstruction).bind()
        is MemoryInstruction -> memoryInstructionExecutor(context, moduleInstruction).bind()
        is NumericInstruction -> numericInstructionExecutor(context, moduleInstruction).bind()
        is ParametricInstruction -> parametricInstructionExecutor(context, moduleInstruction).bind()
        is TableInstruction -> tableInstructionExecutor(context, moduleInstruction).bind()
        is ReferenceInstruction -> referenceInstructionExecutor(context, moduleInstruction).bind()
        is VariableInstruction -> variableInstructionExecutor(context, moduleInstruction).bind()
        is VectorInstruction -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
