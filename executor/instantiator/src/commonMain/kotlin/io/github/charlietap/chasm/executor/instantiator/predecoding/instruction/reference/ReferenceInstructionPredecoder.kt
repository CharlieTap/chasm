package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefAsNonNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefEqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefFuncDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefIsNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefTestDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefAsNonNull
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefCast
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefEq
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefFunc
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefIsNull
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefNull
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction.RefTest
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ReferenceInstructionPredecoder(
    context: InstantiationContext,
    instruction: ReferenceInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    ReferenceInstructionPredecoder(
        context = context,
        instruction = instruction,
        refNullDispatcher = ::RefNullDispatcher,
        refIsNullDispatcher = ::RefIsNullDispatcher,
        refAsNonNullDispatcher = ::RefAsNonNullDispatcher,
        refFuncDispatcher = ::RefFuncDispatcher,
        refEqDispatcher = ::RefEqDispatcher,
        refTestDispatcher = ::RefTestDispatcher,
        refCastDispatcher = ::RefCastDispatcher,
    )

internal inline fun ReferenceInstructionPredecoder(
    context: InstantiationContext,
    instruction: ReferenceInstruction,
    crossinline refNullDispatcher: Dispatcher<RefNull>,
    crossinline refIsNullDispatcher: Dispatcher<RefIsNull>,
    crossinline refAsNonNullDispatcher: Dispatcher<RefAsNonNull>,
    crossinline refFuncDispatcher: Dispatcher<RefFunc>,
    crossinline refEqDispatcher: Dispatcher<RefEq>,
    crossinline refTestDispatcher: Dispatcher<RefTest>,
    crossinline refCastDispatcher: Dispatcher<RefCast>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ReferenceInstruction.RefNull -> {
            val reference = ReferenceValue.Null(instruction.type).toLong()
            refNullDispatcher(RefNull(reference))
        }
        is ReferenceInstruction.RefIsNull -> refIsNullDispatcher(RefIsNull)
        is ReferenceInstruction.RefAsNonNull -> refAsNonNullDispatcher(RefAsNonNull)
        is ReferenceInstruction.RefFunc -> refFuncDispatcher(RefFunc(instruction.funcIdx))
        is ReferenceInstruction.RefEq -> refEqDispatcher(RefEq)
        is ReferenceInstruction.RefTest -> refTestDispatcher(RefTest(instruction.referenceType))
        is ReferenceInstruction.RefCast -> refCastDispatcher(RefCast(instruction.referenceType))
    }
}
