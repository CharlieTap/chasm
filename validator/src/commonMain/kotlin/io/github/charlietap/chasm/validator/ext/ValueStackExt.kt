package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.stack.peekNth
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.BottomType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.matching.ValueTypeMatcher
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation

private val I32_VALUE_TYPE: ValueType = ValueType.Number(NumberType.I32)
private val I64_VALUE_TYPE: ValueType = ValueType.Number(NumberType.I64)
private val F32_VALUE_TYPE: ValueType = ValueType.Number(NumberType.F32)
private val F64_VALUE_TYPE: ValueType = ValueType.Number(NumberType.F64)
private val V128_VALUE_TYPE: ValueType = ValueType.Vector(VectorType.V128)
private val BOTTOM_VALUE_TYPE: ValueType = ValueType.Bottom(BottomType)

internal inline fun ModuleValidationContext.pop(): Result<ValueType, ModuleValidatorError> = binding {

    val label = labels.peekOrNull() ?: Label.DEFAULT

    if (operands.depth() == label.operandsDepth && !label.unreachable) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    if (operands.depth() == label.operandsDepth && label.unreachable) {
        BOTTOM_VALUE_TYPE
    } else {
        operands
            .popOrNull()
            .toResultOr {
                TypeValidatorError.TypeMismatch
            }.bind()
    }
}

internal fun ModuleValidationContext.popOrThrow(): ValueType {
    val label = labels.peekOrNull() ?: Label.DEFAULT
    val depth = operands.depth()

    if (depth == label.operandsDepth) {
        if (label.unreachable) return BOTTOM_VALUE_TYPE
        throw ModuleValidationException(TypeValidatorError.TypeMismatch)
    }

    return operands.popOrNull()
        ?: throw ModuleValidationException(TypeValidatorError.TypeMismatch)
}

internal fun ModuleValidationContext.popOrThrow(expected: ValueType): ValueType {
    val actual = popOrThrow()
    if (!ValueTypeMatcher(actual, expected, this)) {
        throw ModuleValidationException(TypeValidatorError.TypeMismatch)
    }
    return actual
}

internal inline fun ModuleValidationContext.pop(
    expected: ValueType,
    crossinline typeMatcher: TypeMatcher<ValueType> = ::ValueTypeMatcher,
) = binding {
    val actual = pop().bind()
    if (!typeMatcher(actual, expected, this@pop)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }
    actual
}

internal inline fun ModuleValidationContext.push(
    valueType: ValueType,
) = operands.push(valueType)

internal inline fun ModuleValidationContext.popReference(): Result<ReferenceType, ModuleValidatorError> {
    val t = pop().get() ?: return Err(TypeValidatorError.TypeMismatch)

    return when (t) {
        is ValueType.Reference -> Ok(t.referenceType)
        is ValueType.Bottom -> Ok(ReferenceType.Ref(AbstractHeapType.Bottom(BottomType)))
        is ValueType.Number,
        is ValueType.Vector,
        -> Err(TypeValidatorError.TypeMismatch)
    }
}

internal inline fun ModuleValidationContext.popI32(): Result<ValueType, ModuleValidatorError> = pop(I32_VALUE_TYPE)

internal inline fun ModuleValidationContext.popI64(): Result<ValueType, ModuleValidatorError> = pop(I64_VALUE_TYPE)

internal inline fun ModuleValidationContext.popF32(): Result<ValueType, ModuleValidatorError> = pop(F32_VALUE_TYPE)

internal inline fun ModuleValidationContext.popF64(): Result<ValueType, ModuleValidatorError> = pop(F64_VALUE_TYPE)

internal fun ModuleValidationContext.popI32OrThrow(): ValueType = popOrThrow(I32_VALUE_TYPE)

internal fun ModuleValidationContext.popI64OrThrow(): ValueType = popOrThrow(I64_VALUE_TYPE)

internal fun ModuleValidationContext.popF32OrThrow(): ValueType = popOrThrow(F32_VALUE_TYPE)

internal fun ModuleValidationContext.popF64OrThrow(): ValueType = popOrThrow(F64_VALUE_TYPE)

internal inline fun ModuleValidationContext.pushI32() = operands.push(I32_VALUE_TYPE)

internal inline fun ModuleValidationContext.pushI64() = operands.push(I64_VALUE_TYPE)

internal inline fun ModuleValidationContext.pushF32() = operands.push(F32_VALUE_TYPE)

internal inline fun ModuleValidationContext.pushF64() = operands.push(F64_VALUE_TYPE)

internal inline fun ModuleValidationContext.popV128(): Result<ValueType, ModuleValidatorError> = pop(V128_VALUE_TYPE)

internal fun ModuleValidationContext.popV128OrThrow(): ValueType = popOrThrow(V128_VALUE_TYPE)

internal inline fun ModuleValidationContext.pushV128() = operands.push(V128_VALUE_TYPE)

internal inline fun ModuleValidationContext.pushRef(
    heapType: HeapType,
) = operands.push(ValueType.Reference(ReferenceType.Ref(heapType)))

internal inline fun ModuleValidationContext.pushRefNull(
    heapType: HeapType,
) = operands.push(ValueType.Reference(ReferenceType.RefNull(heapType)))

internal inline fun ModuleValidationContext.popRef(): Result<ReferenceType.Ref, ModuleValidatorError> = popReference().flatMap { reference ->
    if (reference is ReferenceType.Ref) {
        Ok(reference)
    } else {
        Err(TypeValidatorError.TypeMismatch)
    }
}

internal inline fun ModuleValidationContext.popRefNull(): Result<ReferenceType.RefNull, ModuleValidatorError> = popReference().flatMap { reference ->
    if (reference is ReferenceType.RefNull) {
        Ok(reference)
    } else {
        Err(TypeValidatorError.TypeMismatch)
    }
}

internal inline fun ModuleValidationContext.popMemoryAddress(
    index: Index.MemoryIndex,
): Result<ValueType, ModuleValidatorError> = binding {
    val memory = memoryType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> popI32().bind()
        AddressType.I64 -> popI64().bind()
    }
}

internal fun ModuleValidationContext.popMemoryAddressOrThrow(
    index: Index.MemoryIndex,
) {
    when (memoryType(index).getOrThrowValidation().addressType) {
        AddressType.I32 -> popI32OrThrow()
        AddressType.I64 -> popI64OrThrow()
    }
}

internal inline fun ModuleValidationContext.pushMemoryAddress(
    index: Index.MemoryIndex,
): Result<Unit, ModuleValidatorError> = binding {
    val memory = memoryType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> pushI32()
        AddressType.I64 -> pushI64()
    }
}

internal inline fun ModuleValidationContext.popTableAddress(
    index: Index.TableIndex,
): Result<ValueType, ModuleValidatorError> = binding {
    val memory = tableType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> popI32().bind()
        AddressType.I64 -> popI64().bind()
    }
}

internal inline fun ModuleValidationContext.pushTableAddress(
    index: Index.TableIndex,
): Result<Unit, ModuleValidatorError> = binding {
    val memory = tableType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> pushI32()
        AddressType.I64 -> pushI64()
    }
}

internal fun ModuleValidationContext.peekValues(
    expected: List<ValueType>,
    typeMatcher: TypeMatcher<ValueType> = ::ValueTypeMatcher,
): Result<Unit, ModuleValidatorError> {
    val label = labels.peekOrNull() ?: Label.DEFAULT
    val available = operands.depth() - label.operandsDepth
    var depth = 0
    var index = expected.lastIndex
    while (index >= 0) {
        val actual = if (depth < available) {
            operands.peekNthOrNull(depth) ?: return Err(TypeValidatorError.TypeMismatch)
        } else {
            if (!label.unreachable) return Err(TypeValidatorError.TypeMismatch)
            BOTTOM_VALUE_TYPE
        }
        if (!typeMatcher(actual, expected[index], this)) {
            return Err(TypeValidatorError.TypeMismatch)
        }
        depth++
        index--
    }
    return Ok(Unit)
}

internal fun ModuleValidationContext.popValues(
    expected: List<ValueType>,
): Result<Unit, ModuleValidatorError> {
    var index = expected.lastIndex
    while (index >= 0) {
        pop(expected[index]).getOrElse { error ->
            return Err(error)
        }
        index--
    }
    return Ok(Unit)
}

internal fun ModuleValidationContext.pushValues(
    values: List<ValueType>,
) {
    var index = 0
    while (index < values.size) {
        operands.push(values[index])
        index++
    }
}

internal fun ModuleValidationContext.popAndReplaceValues(
    expected: List<ValueType>,
): Result<Unit, ModuleValidatorError> {
    var index = expected.lastIndex
    while (index >= 0) {
        pop(expected[index]).getOrElse { error ->
            return Err(error)
        }
        index--
    }
    pushValues(expected)
    return Ok(Unit)
}

internal fun ModuleValidationContext.popValuesForward(
    expected: List<ValueType>,
): Result<Unit, ModuleValidatorError> {
    var index = 0
    while (index < expected.size) {
        pop(expected[index]).getOrElse { error ->
            return Err(error)
        }
        index++
    }
    return Ok(Unit)
}

internal inline fun ModuleValidationContext.unreachable() = binding {
    val label = labels.peek().bind()
    while (operands.depth() > label.operandsDepth) {
        pop().bind()
    }
    label.unreachable = true
}
