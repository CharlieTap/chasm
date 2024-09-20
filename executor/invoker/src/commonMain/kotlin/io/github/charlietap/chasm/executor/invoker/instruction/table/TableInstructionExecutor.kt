package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
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
    tableInitExecutor: Executor<TableInstruction.TableInit>,
    elementDropExecutor: Executor<TableInstruction.ElemDrop>,
    tableCopyExecutor: Executor<TableInstruction.TableCopy>,
    tableFillExecutor: Executor<TableInstruction.TableFill>,
    tableGetExecutor: Executor<TableInstruction.TableGet>,
    tableGrowExecutor: Executor<TableInstruction.TableGrow>,
    tableSetExecutor: Executor<TableInstruction.TableSet>,
    tableSizeExecutor: Executor<TableInstruction.TableSize>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is TableInstruction.TableInit -> tableInitExecutor(context, instruction).bind()
        is TableInstruction.TableCopy -> tableCopyExecutor(context, instruction).bind()
        is TableInstruction.TableFill -> tableFillExecutor(context, instruction).bind()
        is TableInstruction.TableGet -> tableGetExecutor(context, instruction).bind()
        is TableInstruction.TableGrow -> tableGrowExecutor(context, instruction).bind()
        is TableInstruction.TableSet -> tableSetExecutor(context, instruction).bind()
        is TableInstruction.TableSize -> tableSizeExecutor(context, instruction).bind()
        is TableInstruction.ElemDrop -> elementDropExecutor(context, instruction).bind()
    }
}
