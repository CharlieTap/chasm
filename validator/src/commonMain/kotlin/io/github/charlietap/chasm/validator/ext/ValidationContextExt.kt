@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander
import io.github.charlietap.chasm.type.expansion.BlockTypeExpanderImpl
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.FunctionValidatorError
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal inline fun ValidationContext.type(
    index: Index.FunctionIndex,
): Result<DefinedType, ModuleValidatorError> {
    return types.getOrNull(index.idx.toInt()).toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal inline fun ValidationContext.type(
    index: Index.TypeIndex,
): Result<DefinedType, ModuleValidatorError> {
    return types.getOrNull(index.idx.toInt()).toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal inline fun ValidationContext.functionType(
    index: Index.FunctionIndex,
): Result<FunctionType, ModuleValidatorError> {
    return functions.getOrNull(index.idx.toInt()).toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal inline fun ValidationContext.functionType(
    index: Index.TypeIndex,
): Result<FunctionType, ModuleValidatorError> {
    return types.getOrNull(index.idx.toInt())?.functionType().toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal inline fun ValidationContext.functionType(
    blockType: ControlInstruction.BlockType,
    blockTypeExpander: BlockTypeExpander = ::BlockTypeExpanderImpl,
): Result<FunctionType, ModuleValidatorError> {
    return blockTypeExpander(types, blockType).toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal inline fun ValidationContext.globalType(
    index: Index.GlobalIndex,
): Result<GlobalType, ModuleValidatorError> {
    return globals.getOrNull(index.idx.toInt()).toResultOr {
        InstructionValidatorError.UnknownGlobal
    }
}

internal inline fun ValidationContext.elementSegmentType(): Result<ReferenceType, ModuleValidatorError> {
    return elementSegmentType.toResultOr {
        InstructionValidatorError.UnknownElementSegment
    }
}

internal inline fun ValidationContext.localType(
    index: Index.LocalIndex,
): Result<ValueType, ModuleValidatorError> {
    return locals.getOrNull(index.idx.toInt()).toResultOr {
        InstructionValidatorError.UnknownLocal
    }
}

internal inline fun ValidationContext.referenceType(
    index: Index.ElementIndex,
): Result<ReferenceType, ModuleValidatorError> {
    return elems.getOrNull(index.idx.toInt()).toResultOr {
        InstructionValidatorError.UnknownElementSegment
    }
}

internal inline fun ValidationContext.expressionResultType(): Result<ResultType, ModuleValidatorError> {
    return expressionResultType.toResultOr {
        TypeValidatorError.TypeMismatch
    }
}

internal inline fun ValidationContext.resultType(): Result<ResultType, ModuleValidatorError> {
    return result.toResultOr {
        TypeValidatorError.TypeMismatch
    }
}

internal inline fun ValidationContext.tableType(
    index: Index.TableIndex,
): Result<TableType, ModuleValidatorError> {
    return tables.getOrNull(index.idx.toInt()).toResultOr {
        InstructionValidatorError.UnknownTable
    }
}
