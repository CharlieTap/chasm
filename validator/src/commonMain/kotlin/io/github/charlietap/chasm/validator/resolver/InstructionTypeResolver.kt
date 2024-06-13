package io.github.charlietap.chasm.validator.resolver

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.InstructionType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal typealias InstructionTypeResolver<T> = (ValidationContext, T) -> Result<InstructionType, ModuleValidatorError>
