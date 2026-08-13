package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.runtime.value.ExecutionValue

class PreparedFunction internal constructor(
    private val invocation: (List<ExecutionValue>) -> ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError>,
) {
    operator fun invoke(
        args: List<ExecutionValue> = emptyList(),
    ): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> = invocation(args)
}
