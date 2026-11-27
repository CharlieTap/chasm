package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.execution.ExecutionContext

@InternalChasmApi
interface CompiledFunction {

    fun invoke(
        context: ExecutionContext,
        frame: LongArray,
        fp: Int,
    )

    fun call(
        context: ExecutionContext,
        values: LongArray,
        depth: Int,
    )
}
