package io.github.charlietap.chasm.runtime.error

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.type.ValueType
import kotlin.jvm.JvmInline

sealed interface InvocationError : ModuleTrapError {

    data object UncaughtException : InvocationError

    data object InvocationOfADeinstantiatedInstance : InvocationError

    @JvmInline
    value class FunctionNotFound(val function: String) : InvocationError

    @JvmInline
    value class InstructionLookupFailed(val address: Address.Function) : InvocationError

    @JvmInline
    value class FunctionLookupFailed(val address: Address.Function) : InvocationError

    @JvmInline
    value class TableLookupFailed(val address: Address.Table) : InvocationError

    @JvmInline
    value class MemoryLookupFailed(val address: Address.Memory) : InvocationError

    @JvmInline
    value class TagLookupFailed(val address: Address.Tag) : InvocationError

    @JvmInline
    value class GlobalLookupFailed(val address: Address.Global) : InvocationError

    @JvmInline
    value class ElementLookupFailed(val address: Address.Element) : InvocationError

    @JvmInline
    value class DataLookupFailed(val address: Address.Data) : InvocationError

    @JvmInline
    value class ExceptionLookupFailed(val address: Address.Exception) : InvocationError

    @JvmInline
    value class StructLookupFailed(val address: Address.Struct) : InvocationError

    @JvmInline
    value class ArrayLookupFailed(val address: Address.Array) : InvocationError

    @JvmInline
    value class HostLookupFailed(val address: Address.Host) : InvocationError

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

    data object SegmentInitExpressionFailed : InvocationError

    data object MemoryGrowFailed : InvocationError

    data object MemoryOperationOutOfBounds : InvocationError

    data object InvalidPointer : InvocationError

    data object InvalidAddress : InvocationError

    data object MissingStackFrame : InvocationError

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

    data object ExceptionReferenceExpected : InvocationError

    data object FunctionReferenceExpected : InvocationError

    data object StructReferenceExpected : InvocationError

    data object ArrayReferenceExpected : InvocationError

    data object I31ReferenceExpected : InvocationError

    data object ExternReferenceExpected : InvocationError

    data object UnexpectedReferenceValue : InvocationError

    data object NullReferenceExpected : InvocationError

    data object NonNullReferenceExpected : InvocationError

    data object UndefinedDefaultBottomType : InvocationError

    data object FunctionCompositeTypeExpected : InvocationError

    data object StructCompositeTypeExpected : InvocationError

    data object ArrayCompositeTypeExpected : InvocationError

    data object PackedValueExpected : InvocationError

    data object UnobservableBitWidth : InvocationError

    data object FailedToGetTypeOfReferenceValue : InvocationError

    data object ArrayCopyOnAConstArray : InvocationError

    data object ArrayOperationOutOfBounds : InvocationError

    data object TableOperationOutOfBounds : InvocationError

    data object CannotDivideIntegerByZero : InvocationError

    data object ConvertOperationFailed : InvocationError

    data object IntegerOverflow : InvocationError

    data object FailedToCastReference : InvocationError

    data object CallStackExhausted : InvocationError

    @JvmInline
    value class UnimplementedInstruction(val instruction: LinkedInstruction) : InvocationError

    data object Unreachable : InvocationError

    data object FunctionInconsistentWithType : InvocationError

    data class HostFunctionInconsistentWithType(
        val expectedType: ValueType,
        val actualValue: ExecutionValue?,
    ) : InvocationError

    @JvmInline
    value class HostFunctionError(val error: String) : InvocationError

    data object ProgramFinishedInconsistentState : InvocationError

    @JvmInline
    value class GarbageCollectionFailed(val message: String) : InvocationError
}
