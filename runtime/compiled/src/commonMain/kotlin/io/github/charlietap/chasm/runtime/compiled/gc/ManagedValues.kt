@file:JvmName("ManagedValues")

package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import kotlin.jvm.JvmName

fun referenceFromHost(
    context: ExecutionContext,
    encoded: Long,
): Any? {
    return checkNotNull(context.store.referenceBridge) { "store has no managed references" }.fromHost(encoded)
}

fun referenceToHost(
    context: ExecutionContext,
    value: Any?,
    extern: Boolean,
): Long {
    return checkNotNull(context.store.referenceBridge) { "store has no managed references" }.toHostResult(value, extern)
}
