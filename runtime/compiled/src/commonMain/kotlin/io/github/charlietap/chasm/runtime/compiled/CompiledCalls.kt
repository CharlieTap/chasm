@file:JvmName("CompiledCalls")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.executor.invoker.compiled.callFunction
import io.github.charlietap.chasm.executor.invoker.compiled.requestTailCall
import io.github.charlietap.chasm.executor.invoker.compiled.resolveIndirectFunction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.type.RTT
import kotlin.jvm.JvmName
import io.github.charlietap.chasm.executor.invoker.compiled.clearRoots as clearRootSlots
import io.github.charlietap.chasm.executor.invoker.compiled.closeFrame as closeValueFrame
import io.github.charlietap.chasm.executor.invoker.compiled.completeTailCall as completePendingTailCall
import io.github.charlietap.chasm.executor.invoker.compiled.frame as valueFrame
import io.github.charlietap.chasm.executor.invoker.compiled.openFrame as openValueFrame
import io.github.charlietap.chasm.executor.invoker.compiled.openRoots as openRootFrame
import io.github.charlietap.chasm.executor.invoker.compiled.raiseUnwinding as raisePendingUnwinding
import io.github.charlietap.chasm.executor.invoker.compiled.saveStack as saveValueStack

fun call(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
    depth: Int,
) = callFunction(context, caller, function, values, depth)

fun callIndirect(
    context: ExecutionContext,
    caller: ModuleInstance,
    table: TableInstance,
    type: Int,
    elementIndex: Int,
    values: LongArray,
    depth: Int,
) = callFunction(context, caller, resolveIndirectFunction(context, table, RTT(type), elementIndex), values, depth)

fun callRef(
    context: ExecutionContext,
    caller: ModuleInstance,
    reference: Long,
    values: LongArray,
    depth: Int,
) = callFunction(context, caller, context.store.function(reference.toFunctionAddress()), values, depth)

fun indirectTarget(
    context: ExecutionContext,
    table: TableInstance,
    type: Int,
    elementIndex: Int,
): Any? = resolveIndirectFunction(context, table, RTT(type), elementIndex).compiledCode()

fun refTarget(
    context: ExecutionContext,
    reference: Long,
): Any? = context.store.function(reference.toFunctionAddress()).compiledCode()

private fun FunctionInstance.compiledCode(): CompiledFunction? = (this as? FunctionInstance.WasmFunction)?.callStrategy?.compiledEntry

fun depth(
    context: ExecutionContext,
): Int = context.compiledCallDepth

fun isNull(
    reference: Long,
): Boolean = reference.isNullableReference()

fun tailCall(
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance,
    values: LongArray,
) = requestTailCall(context, caller, function, values)

fun tailCallIndirect(
    context: ExecutionContext,
    caller: ModuleInstance,
    table: TableInstance,
    type: Int,
    elementIndex: Int,
    values: LongArray,
) = requestTailCall(context, caller, resolveIndirectFunction(context, table, RTT(type), elementIndex), values)

fun tailCallRef(
    context: ExecutionContext,
    caller: ModuleInstance,
    reference: Long,
    values: LongArray,
) = requestTailCall(context, caller, context.store.function(reference.toFunctionAddress()), values)

fun completeTailCall(
    context: ExecutionContext,
    values: LongArray,
    depth: Int,
) = completePendingTailCall(context, values, depth)

fun raiseUnwinding(
    context: ExecutionContext,
): Unit = raisePendingUnwinding(context)

fun frame(
    context: ExecutionContext,
): LongArray = valueFrame(context)

fun openFrame(
    context: ExecutionContext,
    frameSlots: Int,
): Long = openValueFrame(context, frameSlots)

fun openRoots(
    context: ExecutionContext,
    size: Int,
): Long = openRootFrame(context, size)

fun clearRoots(
    context: ExecutionContext,
    start: Int,
    size: Int,
) = clearRootSlots(context, start, size)

fun closeFrame(
    context: ExecutionContext,
    saved: Long,
) = closeValueFrame(context, saved)

fun saveStack(
    context: ExecutionContext,
): Long = saveValueStack(context)
