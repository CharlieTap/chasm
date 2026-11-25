@file:JvmName("CompiledCalls")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import kotlin.jvm.JvmName
import io.github.charlietap.chasm.executor.invoker.compiled.closeFrame as closeValueFrame
import io.github.charlietap.chasm.executor.invoker.compiled.frame as valueFrame
import io.github.charlietap.chasm.executor.invoker.compiled.openFrame as openValueFrame
import io.github.charlietap.chasm.executor.invoker.compiled.raiseUnwinding as raisePendingUnwinding
import io.github.charlietap.chasm.executor.invoker.compiled.saveStack as saveValueStack

@InternalChasmApi
fun raiseUnwinding(
    context: ExecutionContext,
): Unit = raisePendingUnwinding(context)

@InternalChasmApi
fun frame(
    context: ExecutionContext,
): LongArray = valueFrame(context)

@InternalChasmApi
fun openFrame(
    context: ExecutionContext,
    frameSlots: Int,
): Long = openValueFrame(context, frameSlots)

@InternalChasmApi
fun closeFrame(
    context: ExecutionContext,
    saved: Long,
) = closeValueFrame(context, saved)

@InternalChasmApi
fun saveStack(
    context: ExecutionContext,
): Long = saveValueStack(context)
