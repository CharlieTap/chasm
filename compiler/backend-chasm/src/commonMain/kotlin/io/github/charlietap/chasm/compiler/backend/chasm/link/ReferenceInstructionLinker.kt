package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.compiler.backend.chasm.ext.toRuntime
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefAsNonNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefCastSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefEqSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefFuncSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefIsNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.RefTestSDispatcher
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal fun linkReferenceInstruction(
    instruction: ReferenceInstruction,
    context: LinkContext,
): DispatchableInstruction = when (instruction) {
    is ReferenceInstruction.RefIsNullS -> RefIsNullSDispatcher(instruction.valueSlot, instruction.destinationSlot)
    is ReferenceInstruction.RefNullS -> RefNullSDispatcher(
        ReferenceValue.Null(instruction.heapType.toRuntime()).toLong(),
        instruction.destinationSlot,
    )
    is ReferenceInstruction.RefFuncS -> RefFuncSDispatcher(
        ReferenceValue.Function(Address.Function(context.functionAddresses[instruction.function.index])).toLong(),
        instruction.destinationSlot,
    )
    is ReferenceInstruction.RefAsNonNullS -> RefAsNonNullSDispatcher(instruction.valueSlot, instruction.destinationSlot)
    is ReferenceInstruction.RefEqSs -> RefEqSsDispatcher(
        instruction.reference1Slot,
        instruction.reference2Slot,
        instruction.destinationSlot,
    )
    is ReferenceInstruction.RefTestS -> RefTestSDispatcher(
        instruction.referenceSlot,
        instruction.destinationSlot,
        context.typeTest(instruction.typeTest),
    )
    is ReferenceInstruction.RefCastS -> RefCastSDispatcher(
        instruction.referenceSlot,
        instruction.destinationSlot,
        context.typeTest(instruction.typeTest),
    )
}
