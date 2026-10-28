package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefCastExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefTestExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.executeRefEq
import io.github.charlietap.chasm.executor.invoker.instruction.reference.executeRefIsNull
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

fun RefCastSDispatcher(
    referenceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
) = dispatchInstruction { vstack, context ->
    RefCastExecutor(
        vstack = vstack,
        context = context,
        referenceValue = vstack.getFrameSlot(referenceSlot),
        typeTest = typeTest,
        destinationSlot = destinationSlot,
    )
}

fun RefEqSsDispatcher(
    reference1Slot: Int,
    reference2Slot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeRefEq(
        vstack = vstack,
        reference1 = vstack.getFrameSlot(reference1Slot),
        reference2 = vstack.getFrameSlot(reference2Slot),
        destinationSlot = destinationSlot,
    )
}

fun RefIsNullSDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeRefIsNull(
        vstack = vstack,
        value = vstack.getFrameSlot(valueSlot),
        destinationSlot = destinationSlot,
    )
}

fun RefAsNonNullSDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        val value = vstack.getFrameSlot(valueSlot)
        if (value.isNullableReference()) {
            throw InvocationException(InvocationError.NonNullReferenceExpected)
        }
        vstack.setFrameSlot(destinationSlot, value)
    }
}

fun RefNullSDispatcher(
    reference: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, reference)
    }
}

fun RefFuncSDispatcher(
    reference: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, reference)
    }
}

fun RefTestSDispatcher(
    referenceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
) = dispatchInstruction { vstack, context ->
    RefTestExecutor(
        vstack = vstack,
        context = context,
        referenceValue = vstack.getFrameSlot(referenceSlot),
        typeTest = typeTest,
        destinationSlot = destinationSlot,
    )
}
