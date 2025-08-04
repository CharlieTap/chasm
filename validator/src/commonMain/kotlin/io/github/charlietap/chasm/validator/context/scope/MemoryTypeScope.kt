package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.validator.context.TypeContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun MemoryTypeScope(
    context: ValidationContext,
    type: MemoryType,
): Result<ValidationContext, ModuleValidatorError> = context
    .copy(
        typeContext = TypeContextImpl(
            limitsMaximum = if (type.addressType == AddressType.I32) 65536uL else 281474976710656uL,
        ),
    ).let(::Ok)
