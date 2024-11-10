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

internal inline fun TableInstructionExecutor(
    context: ExecutionContext,
    instruction: TableInstruction,
    crossinline tableInitExecutor: Executor<TableInstruction.TableInit>,
    crossinline elementDropExecutor: Executor<TableInstruction.ElemDrop>,
    crossinline tableCopyExecutor: Executor<TableInstruction.TableCopy>,
    crossinline tableFillExecutor: Executor<TableInstruction.TableFill>,
    crossinline tableGetExecutor: Executor<TableInstruction.TableGet>,
    crossinline tableGrowExecutor: Executor<TableInstruction.TableGrow>,
    crossinline tableSetExecutor: Executor<TableInstruction.TableSet>,
    crossinline tableSizeExecutor: Executor<TableInstruction.TableSize>,
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
