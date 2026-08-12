package io.github.charlietap.chasm.validator.context

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal interface CoreTypeValidationContext : TypeMatcherContext {

    var limitsMaximum: ULong

    val definedTypeCount: Int

    fun definedType(index: Int): Result<DefinedType, ModuleValidatorError>
}
