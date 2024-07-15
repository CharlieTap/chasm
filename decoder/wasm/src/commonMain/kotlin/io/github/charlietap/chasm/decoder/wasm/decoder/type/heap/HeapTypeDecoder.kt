package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun HeapTypeDecoder(
    context: DecoderContext,
): Result<HeapType, WasmDecodeError> =
    HeapTypeDecoder(
        context = context,
        abstractHeapTypeDecoder = ::AbstractHeapTypeDecoder,
    )

internal fun HeapTypeDecoder(
    context: DecoderContext,
    abstractHeapTypeDecoder: Decoder<AbstractHeapType>,
): Result<HeapType, WasmDecodeError> = binding {
    when (context.reader.peek().ubyte().bind()) {
        in ABSTRACT_HEAP_TYPE_RANGE -> {
            abstractHeapTypeDecoder(context).bind()
        }
        else -> {
            val typeIndex = Index.TypeIndex(context.reader.s33().bind())
            ConcreteHeapType.TypeIndex(typeIndex)
        }
    }
}

internal val ABSTRACT_HEAP_TYPE_RANGE = HEAP_TYPE_ARRAY..HEAP_TYPE_NO_FUNC
