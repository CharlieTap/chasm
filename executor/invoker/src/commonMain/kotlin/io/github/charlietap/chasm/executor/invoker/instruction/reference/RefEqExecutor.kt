package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefEqExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefEq,
) {
    val store = context.store
    val stack = context.vstack

    val reference1 = stack.popReference()
    val reference2 = stack.popReference()

    val equal = when {
        reference1 is ReferenceValue.Array && reference2 is ReferenceValue.Array -> {
            val array1 = store.array(reference1.address)
            val array2 = store.array(reference2.address)

            array1 === array2
        }
        reference1 is ReferenceValue.I31 && reference2 is ReferenceValue.I31 -> reference1.value == reference2.value
        reference1 is ReferenceValue.Null && reference2 is ReferenceValue.Null -> true
        reference1 is ReferenceValue.Struct && reference2 is ReferenceValue.Struct -> {
            val struct1 = store.struct(reference1.address)
            val struct2 = store.struct(reference2.address)

            struct1 === struct2
        }
        else -> reference1 === reference2
    }

    if (equal) {
        stack.push(1L)
    } else {
        stack.push(0L)
    }
}
