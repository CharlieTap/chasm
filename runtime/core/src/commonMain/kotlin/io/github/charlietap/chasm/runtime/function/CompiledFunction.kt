package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext

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
