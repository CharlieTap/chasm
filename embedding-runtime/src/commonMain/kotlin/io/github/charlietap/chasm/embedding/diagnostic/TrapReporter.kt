@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmTrap
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

/**
 * A failure while resolving yields the error without a trap, never a
 * different error. [instantiation] describes an instantiation whose start
 * function trapped, before its instance could be registered.
 */
internal fun executionError(
    store: Store,
    error: ModuleTrapError,
    instantiation: Instantiation? = null,
): ChasmError.ExecutionError {
    val message = error.toString()
    if (error !is InvocationError.Trapped) return ChasmError.ExecutionError(message)
    val trap = try {
        TrapReporter(store, error.snapshot, instantiation).report(error.error, message)
    } catch (_: Exception) {
        null
    }
    return ChasmError.ExecutionError(message, trap)
}

/** Function addresses from [firstFunctionAddress] were allocated by this instantiation. */
internal class Instantiation(
    val firstFunctionAddress: Int,
    val source: (ModuleInstance) -> TrapSource?,
)

private class TrapReporter(
    private val store: Store,
    private val snapshot: TrapSnapshot,
    private val instantiation: Instantiation?,
) {
    private val runtime = store.store
    private val modules = ArrayList<Pair<ModuleInstance, ModuleFrames?>>()

    fun report(error: InvocationError, message: String): WasmTrap {
        var faultSite: TrapSite? = null
        val frames = List(snapshot.frameCount) { index ->
            val (frame, site) = frame(index)
            if (index == 0) faultSite = site
            frame
        }
        val reason = reasonOf(error)
        return WasmTrap(
            reason = reason,
            message = message,
            frames = frames,
            omittedFrames = snapshot.omittedFrames,
            traceComplete = snapshot.complete,
            memoryAccess = if (reason == TrapReason.MEMORY_OUT_OF_BOUNDS) {
                faultSite?.memoryAccess()
            } else {
                null
            },
            omissionIndex = snapshot.omissionIndex,
        )
    }

    private fun frame(
        index: Int,
    ): Pair<WasmFrame, TrapSite?> {
        val address = snapshot.functionAddresses[index]
        val function = runtime.functions[address] as FunctionInstance.WasmFunction
        val instance = function.module
        val functionIndex = instance.functionAddresses.indexOfFirst { candidate -> candidate.address == address }
        val module = moduleFrames(instance)
        val site = module?.site(address, functionIndex, index)
        val frame = WasmFrame(
            moduleName = module?.moduleName,
            functionIndex = functionIndex,
            functionName = module?.functionName(functionIndex) ?: exportName(instance, address),
            wasmOffset = site?.offset,
        )
        return frame to site
    }

    private fun moduleFrames(
        instance: ModuleInstance,
    ): ModuleFrames? {
        modules.firstOrNull { (candidate, _) -> candidate === instance }?.let { (_, frames) -> return frames }
        val source = store.diagnostics.find(instance) ?: startSource(instance)
        val frames = source?.frames(runtime, snapshot)
        modules.add(instance to frames)
        return frames
    }

    // The instance being created is the only one owning functions allocated
    // by this instantiation; an imported start function belongs elsewhere.
    private fun startSource(instance: ModuleInstance): TrapSource? {
        val pending = instantiation ?: return null
        val functions = runtime.functions
        for (address in pending.firstFunctionAddress until functions.size) {
            val function = functions[address]
            if (function is FunctionInstance.WasmFunction && function.module === instance) {
                return pending.source(instance)
            }
        }
        return null
    }
}

private fun exportName(instance: ModuleInstance, address: Int): String? = instance.exports.firstOrNull { export ->
    (export.value as? ExternalValue.Function)?.address?.address == address
}?.name?.name

private fun reasonOf(error: InvocationError): TrapReason = when (error) {
    InvocationError.Unreachable -> TrapReason.UNREACHABLE
    InvocationError.MemoryOperationOutOfBounds -> TrapReason.MEMORY_OUT_OF_BOUNDS
    InvocationError.CannotDivideIntegerByZero -> TrapReason.INTEGER_DIVIDE_BY_ZERO
    InvocationError.IntegerOverflow -> TrapReason.INTEGER_OVERFLOW
    InvocationError.ConvertOperationFailed -> TrapReason.INVALID_CONVERSION
    InvocationError.TableOperationOutOfBounds -> TrapReason.TABLE_OUT_OF_BOUNDS
    InvocationError.IndirectCallHasIncorrectFunctionType -> TrapReason.INDIRECT_CALL_TYPE_MISMATCH
    InvocationError.NullReferenceExpected,
    InvocationError.NonNullReferenceExpected,
    InvocationError.UnexpectedReferenceValue,
    InvocationError.IndirectCallOnANonFunctionReference,
    -> TrapReason.NULL_REFERENCE
    InvocationError.FailedToCastReference -> TrapReason.CAST_FAILURE
    InvocationError.ArrayOperationOutOfBounds -> TrapReason.ARRAY_OUT_OF_BOUNDS
    InvocationError.CallStackExhausted -> TrapReason.CALL_STACK_EXHAUSTED
    InvocationError.GuestHeapOutOfMemory -> TrapReason.GUEST_HEAP_EXHAUSTED
    InvocationError.MemoryGrowFailed -> TrapReason.MEMORY_GROW_FAILED
    InvocationError.TableGrowFailed -> TrapReason.TABLE_GROW_FAILED
    InvocationError.FuelExhausted -> TrapReason.FUEL_EXHAUSTED
    InvocationError.Interrupted -> TrapReason.INTERRUPTED
    is InvocationError.HostFunctionError,
    is InvocationError.HostFunctionInconsistentWithType,
    -> TrapReason.HOST_FUNCTION_FAILURE
    InvocationError.ThrownException,
    InvocationError.UncaughtException,
    -> TrapReason.UNCAUGHT_EXCEPTION
    else -> TrapReason.OTHER
}
