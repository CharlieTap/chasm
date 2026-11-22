package io.github.charlietap.chasm.embedding.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.global
import io.github.charlietap.chasm.embedding.internal.matchesType
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.type.Mutability

fun writeGlobal(
    store: Store,
    global: Global,
    value: ExecutionValue,
) {
    internalWriteGlobal(
        store = store,
        global = global,
        value = value,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalWriteGlobal(
    store: Store,
    global: Global,
    value: ExecutionValue,
): Result<Unit, ModuleTrapError> {
    return runCatching {

        val instance = store.global(global)
        require(instance.type.mutability == Mutability.Var) { "global is immutable" }
        require(matchesType(instance.type.valueType, value)) { "value $value does not match global type ${instance.type.valueType}" }

        instance.rawValue = value.toLongFromBoxed()
    }.mapError { failure ->
        when (failure) {
            is InvocationException -> failure.error
            else -> throw failure
        }
    }
}
