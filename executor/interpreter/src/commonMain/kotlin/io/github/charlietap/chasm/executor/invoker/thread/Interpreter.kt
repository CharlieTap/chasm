package io.github.charlietap.chasm.executor.invoker.thread

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.NestedInterpreter

@InternalChasmApi
object Interpreter : NestedInterpreter {
    override fun interpret(
        entryIp: Int,
        context: ExecutionContext,
    ) = if (context.config.debugInfo) dispatchTraceable(entryIp, context) else dispatch(entryIp, context)
}

// Keep dispatch separate so HotSpot OSR need not preserve invocation setup
// and result processing state across every handler call
private fun dispatch(
    entryIp: Int,
    context: ExecutionContext,
) {
    var ip = entryIp
    val vstack = context.vstack
    val instructions = context.store.program.instructions
    dispatch@ while (true) {
        // Dispatch three instructions per iteration to amortise the loop branch.
        // Larger unrolls add indirect-call sites and safepoint metadata.
        ip = instructions[ip](vstack, context, ip + 1)
        if (ip < 0 || ip >= instructions.size) {
            break@dispatch
        }
        ip = instructions[ip](vstack, context, ip + 1)
        if (ip < 0 || ip >= instructions.size) {
            break@dispatch
        }
        ip = instructions[ip](vstack, context, ip + 1)
        if (ip < 0 || ip >= instructions.size) {
            break@dispatch
        }
    }
}

// Keeping the IP live for the handler costs a store per dispatch on HotSpot,
// so this copy of interpret only runs when trap diagnostics are enabled.
private fun dispatchTraceable(
    entryIp: Int,
    context: ExecutionContext,
) {
    var ip = entryIp
    val vstack = context.vstack
    val instructions = context.store.program.instructions
    try {
        dispatch@ while (true) {
            ip = instructions[ip](vstack, context, ip + 1)
            if (ip < 0 || ip >= instructions.size) {
                break@dispatch
            }
            ip = instructions[ip](vstack, context, ip + 1)
            if (ip < 0 || ip >= instructions.size) {
                break@dispatch
            }
            ip = instructions[ip](vstack, context, ip + 1)
            if (ip < 0 || ip >= instructions.size) {
                break@dispatch
            }
        }
    } catch (exception: InvocationException) {
        throw exception.at(ip)
    } catch (_: GuestHeapOutOfMemoryException) {
        throw InvocationException(InvocationError.GuestHeapOutOfMemory).at(ip)
    }
}
