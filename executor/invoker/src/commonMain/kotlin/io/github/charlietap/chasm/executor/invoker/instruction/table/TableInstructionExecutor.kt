package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun TableInstructionExecutor(
    context: ExecutionContext,
    instruction: TableInstruction,
): Result<Unit, InvocationError> =
    TableInstructionExecutor(
        context = context,
        instruction = instruction,
        tableInitExecutor = ::TableInitExecutor,
        elementDropExecutor = ::ElementDropExecutor,
        tableCopyExecutor = ::TableCopyExecutor,
        tableFillExecutor = ::TableFillExecutor,
        tableGrowExecutor = ::TableGrowExecutor,
        tableGetExecutor = ::TableGetExecutor,
        tableSetExecutor = ::TableSetExecutor,
        tableSizeExecutor = ::TableSizeExecutor,
    )

internal fun TableInstructionExecutor(
    context: ExecutionContext,
    instruction: TableInstruction,
    tableInitExecutor: TableInitExecutor,
    elementDropExecutor: ElementDropExecutor,
    tableCopyExecutor: TableCopyExecutor,
    tableFillExecutor: TableFillExecutor,
    tableGetExecutor: TableGetExecutor,
    tableGrowExecutor: TableGrowExecutor,
    tableSetExecutor: TableSetExecutor,
    tableSizeExecutor: TableSizeExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    when (instruction) {
        is TableInstruction.TableInit -> tableInitExecutor(store, stack, instruction).bind()
        is TableInstruction.TableCopy -> tableCopyExecutor(store, stack, instruction).bind()
        is TableInstruction.TableFill -> tableFillExecutor(store, stack, instruction).bind()
        is TableInstruction.TableGet -> tableGetExecutor(store, stack, instruction).bind()
        is TableInstruction.TableGrow -> tableGrowExecutor(store, stack, instruction).bind()
        is TableInstruction.TableSet -> tableSetExecutor(store, stack, instruction).bind()
        is TableInstruction.TableSize -> tableSizeExecutor(store, stack, instruction).bind()
        is TableInstruction.ElemDrop -> elementDropExecutor(store, stack, instruction).bind()
    }
}
