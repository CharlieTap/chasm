@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.jit.internal.REFERENCE_ARRAY
import io.github.charlietap.chasm.jit.internal.REFERENCE_EXCEPTION
import io.github.charlietap.chasm.jit.internal.REFERENCE_FUNCTION
import io.github.charlietap.chasm.jit.internal.REFERENCE_HOST_VALUE
import io.github.charlietap.chasm.jit.internal.REFERENCE_I31
import io.github.charlietap.chasm.jit.internal.REFERENCE_STRUCT
import io.github.charlietap.chasm.jit.internal._externReference
import io.github.charlietap.chasm.jit.internal._functionReference
import io.github.charlietap.chasm.jit.internal._hostValue
import io.github.charlietap.chasm.jit.internal._referenceKind
import io.github.charlietap.chasm.jit.internal._referenceValue
import io.github.charlietap.chasm.jit.internal._wasmReference
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.jvm.JvmInline

@ExperimentalChasmApi
@JvmInline
value class WasmReference internal constructor(
    internal val value: Any?,
) {

    val isNull: Boolean
        get() = value == null

    val kind: WasmReferenceKind
        get() = when (_referenceKind(value)) {
            REFERENCE_HOST_VALUE -> WasmReferenceKind.HostValue
            REFERENCE_FUNCTION -> WasmReferenceKind.Function
            REFERENCE_I31 -> WasmReferenceKind.I31
            REFERENCE_STRUCT -> WasmReferenceKind.Struct
            REFERENCE_ARRAY -> WasmReferenceKind.Array
            REFERENCE_EXCEPTION -> WasmReferenceKind.Exception
            else -> WasmReferenceKind.Null
        }

    companion object {
        val Null = WasmReference(null)
    }
}

@ExperimentalChasmApi
enum class WasmReferenceKind {
    Null,
    HostValue,
    Function,
    I31,
    Struct,
    Array,
    Exception,
}

@ExperimentalChasmApi
fun externReference(
    value: Any,
): WasmReference {
    return WasmReference(_externReference(value))
}

@ExperimentalChasmApi
fun hostValue(
    store: Store,
    reference: WasmReference,
): Any? {
    return _hostValue(store, reference.value)
}

@ExperimentalChasmApi
fun functionReference(
    store: Store,
    function: Function,
): WasmReference {
    return WasmReference(_functionReference(store, function))
}

@ExperimentalChasmApi
fun referenceValue(
    store: Store,
    reference: WasmReference,
    extern: Boolean,
): ReferenceValue {
    return _referenceValue(store, reference.value, extern)
}

@ExperimentalChasmApi
fun wasmReference(
    store: Store,
    value: ReferenceValue,
): WasmReference {
    return WasmReference(_wasmReference(store, value))
}
