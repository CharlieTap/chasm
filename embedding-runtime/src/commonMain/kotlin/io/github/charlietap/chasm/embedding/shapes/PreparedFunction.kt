package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

class PreparedFunction internal constructor(
    private val invocation: (List<ExecutionValue>) -> ChasmResult<List<ExecutionValue>, InvocationError<ReferenceValue.Exception>>,
) {
    operator fun invoke(
        args: List<ExecutionValue> = emptyList(),
    ): ChasmResult<List<ExecutionValue>, InvocationError<ReferenceValue.Exception>> {
        return invocation(args)
    }
}
