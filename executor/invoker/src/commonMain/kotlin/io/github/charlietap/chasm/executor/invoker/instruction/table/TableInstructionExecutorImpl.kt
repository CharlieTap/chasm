package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun TableInstructionExecutorImpl(
    instruction: TableInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    TableInstructionExecutorImpl(
        instruction = instruction,
        store = store,
        stack = stack,
        tableInitExecutor = ::TableInitExecutorImpl,
        elementDropExecutor = ::ElementDropExecutorImpl,
        tableCopyExecutor = ::TableCopyExecutorImpl,
        tableFillExecutor = ::TableFillExecutorImpl,
        tableGrowExecutor = ::TableGrowExecutorImpl,
        tableGetExecutor = ::TableGetExecutorImpl,
        tableSetExecutor = ::TableSetExecutorImpl,
        tableSizeExecutor = ::TableSizeExecutorImpl,
    )

internal fun TableInstructionExecutorImpl(
    instruction: TableInstruction,
    store: Store,
    stack: Stack,
    tableInitExecutor: TableInitExecutor,
    elementDropExecutor: ElementDropExecutor,
    tableCopyExecutor: TableCopyExecutor,
    tableFillExecutor: TableFillExecutor,
    tableGetExecutor: TableGetExecutor,
    tableGrowExecutor: TableGrowExecutor,
    tableSetExecutor: TableSetExecutor,
    tableSizeExecutor: TableSizeExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is TableInstruction.TableInit -> tableInitExecutor(store, stack, instruction).bind()
        is TableInstruction.TableCopy -> tableCopyExecutor(store, stack, instruction).bind()
        is TableInstruction.TableFill -> tableFillExecutor(store, stack, instruction).bind()
        is TableInstruction.TableGet -> tableGetExecutor(store, stack, instruction).bind()
        is TableInstruction.TableGrow -> tableGrowExecutor(store, stack, instruction).bind()
        is TableInstruction.TableSet -> tableSetExecutor(store, stack, instruction).bind()
        is TableInstruction.TableSize -> tableSizeExecutor(store, stack, instruction).bind()
        is TableInstruction.ElemDrop -> elementDropExecutor(store, stack, instruction).bind()

        else -> Err(InvocationError.UnimplementedInstruction(ModuleInstruction(instruction))).bind<Unit>()
    }
}
