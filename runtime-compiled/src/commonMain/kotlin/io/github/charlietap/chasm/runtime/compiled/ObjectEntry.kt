package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.CompiledFunction

@InternalChasmApi
abstract class ObjectEntry : CompiledFunction {

    abstract fun callObjects(
        context: ExecutionContext,
        values: LongArray,
        references: Array<Any?>,
        depth: Int,
    )

    final override fun invoke(
        context: ExecutionContext,
        frame: LongArray,
        fp: Int,
    ) {
        throw UnsupportedOperationException("compiled entries are entered through callObjects")
    }

    final override fun call(
        context: ExecutionContext,
        values: LongArray,
        depth: Int,
    ) {
        throw UnsupportedOperationException("compiled entries are entered through callObjects")
    }
}
