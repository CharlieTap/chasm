package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal inline fun StructType.fieldType(
    idx: Index.FieldIndex,
): Result<FieldType, ModuleValidatorError> = fields.getOrNull(idx.idx.toInt()).toResultOr { TypeValidatorError.TypeMismatch }
