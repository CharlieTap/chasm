package io.github.charlietap.chasm.executor.runtime.error

import io.github.charlietap.chasm.executor.runtime.store.Address
import kotlin.jvm.JvmInline

sealed interface InvocationError : ModuleRuntimeError {

    data class MemoryGrowExceedsLimits(
        val pagesRequested: Int,
        val maxPages: Int,
    ) : InvocationError

    data object MemoryOperationOutOfBounds : InvocationError

    @JvmInline
    value class TableGrowExceedsLimits(val max: UInt) : InvocationError

    data object InvalidAddress : InvocationError

    data object NonWasmFunctionInvocation : InvocationError

    data object FunctionReturnArityMismatch : InvocationError

    data object MissingStackFrame : InvocationError

    data object MissingStackLabel : InvocationError

    data object MissingStackValue : InvocationError

    @JvmInline
    value class StoreLookupFailed(val address: Address) : InvocationError

    data object MissingLocal : InvocationError

    data object InstructionFailure : InvocationError

    data object UnimplementedInstruction : InvocationError

    sealed interface Trap : InvocationError {
        data object TrapEncountered : Trap
    }
}
