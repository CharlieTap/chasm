package io.github.charlietap.chasm.decoder.decoder.type.heap

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType

internal fun HeapTypeDecoder(
    context: DecoderContext,
): Result<HeapType, WasmDecodeError> =
    HeapTypeDecoder(
        context = context,
        abstractHeapTypeDecoder = ::AbstractHeapTypeDecoder,
    )

internal inline fun HeapTypeDecoder(
    context: DecoderContext,
    crossinline abstractHeapTypeDecoder: Decoder<AbstractHeapType>,
): Result<HeapType, WasmDecodeError> = binding {
    when (
        context.reader
            .peek()
            .ubyte()
            .bind()
    ) {
        in ABSTRACT_HEAP_TYPE_RANGE -> {
            abstractHeapTypeDecoder(context).bind()
        }
        else -> {
            val typeIndex = Index.TypeIndex(context.reader.s33().bind())
            ConcreteHeapType.TypeIndex(typeIndex.idx.toInt())
        }
    }
}

internal val ABSTRACT_HEAP_TYPE_RANGE = HEAP_TYPE_EXCEPTION..HEAP_TYPE_NO_FUNC
