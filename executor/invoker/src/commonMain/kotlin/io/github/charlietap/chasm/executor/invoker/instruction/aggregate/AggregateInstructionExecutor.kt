package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun AggregateInstructionExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction,
): Result<Unit, InvocationError> =
    AggregateInstructionExecutor(
        context = context,
        instruction = instruction,
        structNewExecutor = ::StructNewExecutor,
        structNewDefaultExecutor = ::StructNewDefaultExecutor,
        structGetExecutor = ::StructGetExecutor,
        structGetSignedExecutor = ::StructGetSignedExecutor,
        structGetUnsignedExecutor = ::StructGetUnsignedExecutor,
        structSetExecutor = ::StructSetExecutor,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
        arrayNewExecutor = ::ArrayNewExecutor,
        arrayNewDefaultExecutor = ::ArrayNewDefaultExecutor,
        arrayNewDataExecutor = ::ArrayNewDataExecutor,
        arrayNewElementExecutor = ::ArrayNewElementExecutor,
        arrayGetExecutor = ::ArrayGetExecutor,
        arrayGetSignedExecutor = ::ArrayGetSignedExecutor,
        arrayGetUnsignedExecutor = ::ArrayGetUnsignedExecutor,
        arraySetExecutor = ::ArraySetExecutor,
        arrayLenExecutor = ::ArrayLenExecutor,
        refI31Executor = ::RefI31Executor,
        i31GetSignedExecutor = ::I31GetSignedExecutor,
        i31GetUnsignedExecutor = ::I31GetUnsignedExecutor,
        arrayFillExecutor = ::ArrayFillExecutor,
        arrayCopyExecutor = ::ArrayCopyExecutor,
        arrayInitDataExecutor = ::ArrayInitDataExecutor,
        arrayInitElementExecutor = ::ArrayInitElementExecutor,
        anyConvertExternExecutor = ::AnyConvertExternExecutor,
        externConvertAnyExecutor = ::ExternConvertAnyExecutor,
    )

internal inline fun AggregateInstructionExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction,
    crossinline structNewExecutor: Executor<AggregateInstruction.StructNew>,
    crossinline structNewDefaultExecutor: Executor<AggregateInstruction.StructNewDefault>,
    crossinline structGetExecutor: Executor<AggregateInstruction.StructGet>,
    crossinline structGetSignedExecutor: Executor<AggregateInstruction.StructGetSigned>,
    crossinline structGetUnsignedExecutor: Executor<AggregateInstruction.StructGetUnsigned>,
    crossinline structSetExecutor: Executor<AggregateInstruction.StructSet>,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
    crossinline arrayNewExecutor: Executor<AggregateInstruction.ArrayNew>,
    crossinline arrayNewDefaultExecutor: Executor<AggregateInstruction.ArrayNewDefault>,
    crossinline arrayNewDataExecutor: Executor<AggregateInstruction.ArrayNewData>,
    crossinline arrayNewElementExecutor: Executor<AggregateInstruction.ArrayNewElement>,
    crossinline arrayGetExecutor: Executor<AggregateInstruction.ArrayGet>,
    crossinline arrayGetSignedExecutor: Executor<AggregateInstruction.ArrayGetSigned>,
    crossinline arrayGetUnsignedExecutor: Executor<AggregateInstruction.ArrayGetUnsigned>,
    crossinline arraySetExecutor: Executor<AggregateInstruction.ArraySet>,
    crossinline arrayLenExecutor: Executor<AggregateInstruction.ArrayLen>,
    crossinline refI31Executor: Executor<AggregateInstruction.RefI31>,
    crossinline i31GetSignedExecutor: Executor<AggregateInstruction.I31GetSigned>,
    crossinline i31GetUnsignedExecutor: Executor<AggregateInstruction.I31GetUnsigned>,
    crossinline arrayFillExecutor: Executor<AggregateInstruction.ArrayFill>,
    crossinline arrayCopyExecutor: Executor<AggregateInstruction.ArrayCopy>,
    crossinline arrayInitDataExecutor: Executor<AggregateInstruction.ArrayInitData>,
    crossinline arrayInitElementExecutor: Executor<AggregateInstruction.ArrayInitElement>,
    crossinline anyConvertExternExecutor: Executor<AggregateInstruction.AnyConvertExtern>,
    crossinline externConvertAnyExecutor: Executor<AggregateInstruction.ExternConvertAny>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AggregateInstruction.StructNew -> structNewExecutor(context, instruction).bind()
        is AggregateInstruction.StructNewDefault -> structNewDefaultExecutor(context, instruction).bind()
        is AggregateInstruction.StructGet -> structGetExecutor(context, instruction).bind()
        is AggregateInstruction.StructGetSigned -> structGetSignedExecutor(context, instruction).bind()
        is AggregateInstruction.StructGetUnsigned -> structGetUnsignedExecutor(context, instruction).bind()
        is AggregateInstruction.StructSet -> structSetExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayNewFixed -> arrayNewFixedExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayNew -> arrayNewExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayNewDefault -> arrayNewDefaultExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayNewData -> arrayNewDataExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayNewElement -> arrayNewElementExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayGet -> arrayGetExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayGetSigned -> arrayGetSignedExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayGetUnsigned -> arrayGetUnsignedExecutor(context, instruction).bind()
        is AggregateInstruction.ArraySet -> arraySetExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayLen -> arrayLenExecutor(context, instruction).bind()
        is AggregateInstruction.RefI31 -> refI31Executor(context, instruction).bind()
        is AggregateInstruction.I31GetSigned -> i31GetSignedExecutor(context, instruction).bind()
        is AggregateInstruction.I31GetUnsigned -> i31GetUnsignedExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayFill -> arrayFillExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayCopy -> arrayCopyExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayInitData -> arrayInitDataExecutor(context, instruction).bind()
        is AggregateInstruction.ArrayInitElement -> arrayInitElementExecutor(context, instruction).bind()
        is AggregateInstruction.AnyConvertExtern -> anyConvertExternExecutor(context, instruction).bind()
        is AggregateInstruction.ExternConvertAny -> externConvertAnyExecutor(context, instruction).bind()
    }
}
