package io.github.charlietap.chasm.executor.runtime.error

import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmInline

sealed interface InvocationError : ModuleTrapError {

    data object UncaughtException : InvocationError

    data object InvocationOfADeinstantiatedInstance : InvocationError

    @JvmInline
    value class FunctionNotFound(val function: String) : InvocationError

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
    value class TableElementLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class ArrayFieldLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class StructFieldLookupFailed(val index: Int) : InvocationError

    @JvmInline
    value class TagAddressLookupFailed(val index: Int) : InvocationError

    data class MemoryGrowExceedsLimits(
        val pagesRequested: Int,
        val maxPages: Int,
    ) : InvocationError

    data object MemoryGrowFailed : InvocationError

    data object MemoryOperationOutOfBounds : InvocationError

    data object InvalidPointer : InvocationError

    data object InvalidAddress : InvocationError

    data object MissingStackFrame : InvocationError

    data object MissingStackHandler : InvocationError

    data object MissingInstruction : InvocationError

    data object MissingStackLabel : InvocationError

    data object MissingStackValue : InvocationError

    data object MissingLocal : InvocationError

    data object IndirectCallOnANonFunctionReference : InvocationError

    data object IndirectCallHasIncorrectFunctionType : InvocationError

    data object NumberValueExpected : InvocationError

    data object I32ValueExpected : InvocationError

    data object I64ValueExpected : InvocationError

    data object F32ValueExpected : InvocationError

    data object F64ValueExpected : InvocationError

    data object ReferenceValueExpected : InvocationError

    data object FunctionReferenceExpected : InvocationError

    data object StructReferenceExpected : InvocationError

    data object ArrayReferenceExpected : InvocationError

    data object UnexpectedReferenceValue : InvocationError

    data object UndefinedDefaultBottomType : InvocationError

    data object FunctionCompositeTypeExpected : InvocationError

    data object StructCompositeTypeExpected : InvocationError

    data object ArrayCompositeTypeExpected : InvocationError

    data object PackedValueExpected : InvocationError

    data object UnobservableBitWidth : InvocationError

    data object ArrayCopyOnAConstArray : InvocationError

    data object CannotDivideIntegerByZero : InvocationError

    data object IntegerOverflow : InvocationError

    data object CallStackExhausted : InvocationError

    @JvmInline
    value class UnimplementedInstruction(val instruction: ExecutionInstruction) : InvocationError

    data class HostFunctionInconsistentWithType(
        val expectedType: ValueType,
        val actualValue: ExecutionValue?,
    ) : InvocationError

    @JvmInline
    value class HostFunctionError(val error: String) : InvocationError

    data object ProgramFinishedInconsistentState : InvocationError

    sealed interface Trap : InvocationError {
        data object TrapEncountered : Trap
    }
}
