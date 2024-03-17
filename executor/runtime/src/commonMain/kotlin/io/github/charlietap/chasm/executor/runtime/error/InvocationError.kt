package io.github.charlietap.chasm.executor.runtime.error

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmInline

sealed interface InvocationError : ModuleRuntimeError {

    @JvmInline
    value class StoreLookupFailed(val address: Address) : InvocationError

    @JvmInline
    value class FunctionTypeLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class FunctionAddressLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class TableAddressLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class MemoryAddressLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class GlobalAddressLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class ElementAddressLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class DataAddressLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class ExportInstanceLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class ElementReferenceLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class TableElementLookupFailed(val index: Int) : InvocationError

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

    data object MissingLocal : InvocationError

    data object InstructionFailure : InvocationError

    data object IndirectCallOnANonFunctionReference : InvocationError

    data object IndirectCallHasIncorrectFunctionType : InvocationError

    data object ReferenceValueExpected : InvocationError

    data object FunctionReferenceExpected : InvocationError

    data object UndefinedDefaultReferenceType : InvocationError

    @JvmInline
    value class UnimplementedInstruction(val instruction: Instruction) : InvocationError

    data class HostFunctionInconsistentWithType(
        val expectedType: ValueType,
        val actualValue: ExecutionValue?,
    ) : InvocationError

    data object ProgramFinishedInconsistentState : InvocationError

    sealed interface Trap : InvocationError {
        data object TrapEncountered : Trap
    }
}
