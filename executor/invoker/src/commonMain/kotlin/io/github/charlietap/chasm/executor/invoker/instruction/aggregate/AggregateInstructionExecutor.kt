@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias AggregateInstructionExecutor = (AggregateInstruction, Store, Stack) -> Result<Unit, InvocationError>

internal fun AggregateInstructionExecutor(
    instruction: AggregateInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    AggregateInstructionExecutor(
        instruction = instruction,
        store = store,
        stack = stack,
        structNewExecutor = ::StructNewExecutor,
        structNewDefaultExecutor = ::StructNewDefaultExecutor,
        structGetExecutor = ::StructGetExecutor,
        structSetExecutor = ::StructSetExecutor,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
        arrayNewExecutor = ::ArrayNewExecutor,
        arrayNewDefaultExecutor = ::ArrayNewDefaultExecutor,
        arrayNewDataExecutor = ::ArrayNewDataExecutor,
        arrayNewElementExecutor = ::ArrayNewElementExecutor,
        arrayGetExecutor = ::ArrayGetExecutor,
        arraySetExecutor = ::ArraySetExecutor,
        arrayLenExecutor = ::ArrayLenExecutor,
        refI31Executor = ::RefI31Executor,
        i31GetExecutor = ::I31GetExecutor,
        arrayFillExecutor = ::ArrayFillExecutor,
        arrayCopyExecutor = ::ArrayCopyExecutor,
        arrayInitDataExecutor = ::ArrayInitDataExecutor,
        arrayInitElementExecutor = ::ArrayInitElementExecutor,
        anyConvertExternExecutor = ::AnyConvertExternExecutor,
        externConvertAnyExecutor = ::ExternConvertAnyExecutor,
    )

internal inline fun AggregateInstructionExecutor(
    instruction: AggregateInstruction,
    store: Store,
    stack: Stack,
    crossinline structNewExecutor: StructNewExecutor,
    crossinline structNewDefaultExecutor: StructNewDefaultExecutor,
    crossinline structGetExecutor: StructGetExecutor,
    crossinline structSetExecutor: StructSetExecutor,
    crossinline arrayNewFixedExecutor: ArrayNewFixedExecutor,
    crossinline arrayNewExecutor: ArrayNewExecutor,
    crossinline arrayNewDefaultExecutor: ArrayNewDefaultExecutor,
    crossinline arrayNewDataExecutor: ArrayNewDataExecutor,
    crossinline arrayNewElementExecutor: ArrayNewElementExecutor,
    crossinline arrayGetExecutor: ArrayGetExecutor,
    crossinline arraySetExecutor: ArraySetExecutor,
    crossinline arrayLenExecutor: ArrayLenExecutor,
    crossinline refI31Executor: RefI31Executor,
    crossinline i31GetExecutor: I31GetExecutor,
    crossinline arrayFillExecutor: ArrayFillExecutor,
    crossinline arrayCopyExecutor: ArrayCopyExecutor,
    crossinline arrayInitDataExecutor: ArrayInitDataExecutor,
    crossinline arrayInitElementExecutor: ArrayInitElementExecutor,
    crossinline anyConvertExternExecutor: AnyConvertExternExecutor,
    crossinline externConvertAnyExecutor: ExternConvertAnyExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AggregateInstruction.StructNew -> structNewExecutor(store, stack, instruction.typeIndex).bind()
        is AggregateInstruction.StructNewDefault -> structNewDefaultExecutor(store, stack, instruction.typeIndex).bind()
        is AggregateInstruction.StructGet -> structGetExecutor(store, stack, instruction.typeIndex, instruction.fieldIndex, true).bind()
        is AggregateInstruction.StructGetSigned ->
            structGetExecutor(store, stack, instruction.typeIndex, instruction.fieldIndex, true).bind()
        is AggregateInstruction.StructGetUnsigned ->
            structGetExecutor(store, stack, instruction.typeIndex, instruction.fieldIndex, false).bind()
        is AggregateInstruction.StructSet -> structSetExecutor(store, stack, instruction).bind()
        is AggregateInstruction.ArrayNewFixed -> arrayNewFixedExecutor(store, stack, instruction.typeIndex, instruction.size).bind()
        is AggregateInstruction.ArrayNew -> arrayNewExecutor(store, stack, instruction.typeIndex).bind()
        is AggregateInstruction.ArrayNewDefault -> arrayNewDefaultExecutor(store, stack, instruction.typeIndex).bind()
        is AggregateInstruction.ArrayNewData -> arrayNewDataExecutor(store, stack, instruction.typeIndex, instruction.dataIndex).bind()
        is AggregateInstruction.ArrayNewElement ->
            arrayNewElementExecutor(store, stack, instruction.typeIndex, instruction.elementIndex).bind()
        is AggregateInstruction.ArrayGet -> arrayGetExecutor(store, stack, instruction.typeIndex, true).bind()
        is AggregateInstruction.ArrayGetSigned -> arrayGetExecutor(store, stack, instruction.typeIndex, true).bind()
        is AggregateInstruction.ArrayGetUnsigned -> arrayGetExecutor(store, stack, instruction.typeIndex, false).bind()
        is AggregateInstruction.ArraySet -> arraySetExecutor(store, stack, instruction.typeIndex).bind()
        is AggregateInstruction.ArrayLen -> arrayLenExecutor(store, stack).bind()
        is AggregateInstruction.RefI31 -> refI31Executor(stack).bind()
        is AggregateInstruction.I31GetSigned -> i31GetExecutor(stack, true).bind()
        is AggregateInstruction.I31GetUnsigned -> i31GetExecutor(stack, false).bind()
        is AggregateInstruction.ArrayFill -> arrayFillExecutor(store, stack, instruction.typeIndex).bind()
        is AggregateInstruction.ArrayCopy ->
            arrayCopyExecutor(store, stack, instruction.sourceTypeIndex, instruction.destinationTypeIndex).bind()
        is AggregateInstruction.ArrayInitData ->
            arrayInitDataExecutor(store, stack, instruction.typeIndex, instruction.dataIndex).bind()
        is AggregateInstruction.ArrayInitElement ->
            arrayInitElementExecutor(store, stack, instruction.typeIndex, instruction.elementIndex).bind()
        is AggregateInstruction.AnyConvertExtern -> anyConvertExternExecutor(stack).bind()
        is AggregateInstruction.ExternConvertAny -> externConvertAnyExecutor(stack).bind()
    }
}
