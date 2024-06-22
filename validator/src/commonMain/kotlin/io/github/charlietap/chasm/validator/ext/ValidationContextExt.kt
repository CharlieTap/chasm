package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.FunctionValidatorError
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ValidationContext.type(index: Index.TypeIndex): Result<DefinedType, ModuleValidatorError> {
    return types.getOrNull(index.idx.toInt()).toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal fun ValidationContext.functionType(index: Index.TypeIndex): Result<FunctionType, ModuleValidatorError> {
    return types.getOrNull(index.idx.toInt())?.functionType().toResultOr {
        FunctionValidatorError.UnknownType
    }
}

internal fun ValidationContext.global(index: Index.GlobalIndex): Result<GlobalType, ModuleValidatorError> {
    return globals.getOrNull(index.idx.toInt()).toResultOr {
        InstructionValidatorError.UnknownGlobal
    }
}
