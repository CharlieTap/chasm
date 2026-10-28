@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.executor.invoker.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.function.exceptionalCallSiteIp
import io.github.charlietap.chasm.executor.invoker.function.initializeLocals
import io.github.charlietap.chasm.executor.invoker.function.returnToCaller
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

class CompiledFunctionInstallation(
    val function: FunctionInstance.WasmFunction,
    val entry: CompiledFunction,
)

fun installCompiledFunctions(
    store: Store,
    installations: List<CompiledFunctionInstallation>,
) {
    val seen = HashSet<WasmFunctionCallStrategy>()
    installations.forEach { installation ->
        val function = installation.function
        require(!function.callStrategy.isInstalled && function.callStrategy.compiledEntry == null) {
            "function already has a body"
        }
        require(seen.add(function.callStrategy)) { "function installed twice" }
    }
    val program = store.program
    program.reserve(installations.size)
    installations.forEach { installation ->
        val function = installation.function
        val strategy = function.callStrategy
        val entry = CompiledEntry(
            compiled = installation.entry,
            resultCount = function.functionType.results.types.size,
            activationHeaderSlot = strategy.interfaceSlotCount,
            frameSlots = strategy.interfaceSlotCount + 1,
        )
        strategy.frameSlots = strategy.interfaceSlotCount + 1
        strategy.localInitialization = LocalInitialization.None
        strategy.compiledEntry = installation.entry
        strategy.entryIp = program.append(entry)
    }
}

@OptIn(UnsafeHostApi::class)
private class CompiledEntry(
    private val compiled: CompiledFunction,
    private val resultCount: Int,
    private val activationHeaderSlot: Int,
    private val frameSlots: Int,
) : DispatchableInstruction() {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        val fp = vstack.fp
        try {
            compiled.invoke(context, vstack.unsafeElements(), fp)
        } catch (exception: InvocationException) {
            if (exception.error != InvocationError.ThrownException) throw exception
            return resumeUnwinding(vstack, context, fp)
        } catch (_: HostRaisedWasmException) {
            return resumeUnwinding(vstack, context, fp)
        }
        if (context.compiledUnwinding) {
            context.compiledUnwinding = false
            return resumeUnwinding(vstack, context, fp)
        }
        if (context.pendingTailFunction != null) {
            return try {
                tailCall(vstack, context, fp)
            } catch (exception: InvocationException) {
                if (exception.error != InvocationError.ThrownException) throw exception
                resumeUnwinding(vstack, context, fp)
            } catch (_: HostRaisedWasmException) {
                resumeUnwinding(vstack, context, fp)
            }
        }
        return returnToCaller(vstack, context.store, resultCount, activationHeaderSlot)
    }

    private fun resumeUnwinding(
        vstack: ValueStack,
        context: ExecutionContext,
        fp: Int,
    ): Int {
        vstack.activateFrameAtDepth(fp, fp + frameSlots)
        val reference = context.heap.takePendingExceptionReference()
        val returnIp = vstack.unwindCallerFrame(activationHeaderSlot, allowNestedExit = context.compiledCallDepth > 0)
        if (returnIp == EXIT_IP) {
            context.heap.setPendingException(reference)
            throw InvocationException(InvocationError.ThrownException)
        }
        return ThrowRefValueExecutor(vstack, context, reference, exceptionalCallSiteIp(returnIp, resultCount))
    }

    private fun tailCall(
        vstack: ValueStack,
        context: ExecutionContext,
        fp: Int,
    ): Int {
        val header = vstack.getFrameSlot(fp, activationHeaderSlot)
        while (true) {
            val function = checkNotNull(context.pendingTailFunction)
            val caller = checkNotNull(context.pendingTailCaller)
            var arguments = checkNotNull(context.pendingTailArguments)
            clearPendingTailCall(context)
            val results = function.functionType.results.types.size
            if (arguments.size < results) arguments = arguments.copyOf(results)
            val compiled = (function as? FunctionInstance.WasmFunction)?.callStrategy?.compiledEntry
            when {
                compiled != null -> {
                    compiled.call(context, arguments, context.compiledCallDepth + COMPILED_CALL_COST)
                    if (context.compiledUnwinding) raiseUnwinding(context)
                }
                function is FunctionInstance.WasmFunction -> {
                    if (context.nestedInterpreter == null) throw InvocationException(InvocationError.InterpreterUnavailable)
                    val strategy = function.callStrategy
                    vstack.ensureCapacity(fp + strategy.frameSlots)
                    val parameters = function.functionType.params.types.size
                    arguments.copyInto(vstack.unsafeElements(), fp, 0, parameters)
                    initializeLocals(vstack, strategy, fp)
                    vstack.setFrameSlot(fp, strategy.interfaceSlotCount, header)
                    vstack.activateFrame(fp, strategy.frameSlots)
                    return strategy.entryIp
                }
                else -> interpretFunctionAt(context, caller, function, arguments, context.compiledCallDepth)
            }
            if (context.pendingTailFunction != null) continue
            arguments.copyInto(vstack.unsafeElements(), fp, 0, results)
            return returnToCaller(vstack, context.store, resultCount, activationHeaderSlot)
        }
    }
}
