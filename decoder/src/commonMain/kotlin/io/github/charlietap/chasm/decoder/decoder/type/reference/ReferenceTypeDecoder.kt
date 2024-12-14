package io.github.charlietap.chasm.decoder.decoder.type.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.AbstractHeapTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ReferenceTypeDecoder(
    context: DecoderContext,
): Result<ReferenceType, WasmDecodeError> =
    ReferenceTypeDecoder(
        context = context,
        heapTypeDecoder = ::HeapTypeDecoder,
        abstractHeapTypeDecoder = ::AbstractHeapTypeDecoder,
    )

internal inline fun ReferenceTypeDecoder(
    context: DecoderContext,
    crossinline heapTypeDecoder: Decoder<HeapType>,
    crossinline abstractHeapTypeDecoder: Decoder<AbstractHeapType>,
): Result<ReferenceType, WasmDecodeError> = binding {
    when (
        context.reader
            .peek()
            .ubyte()
            .bind()
    ) {
        REFERENCE_TYPE_REF -> {
            context.reader.ubyte().bind() // consume byte
            ReferenceType.Ref(heapTypeDecoder(context).bind())
        }
        REFERENCE_TYPE_REF_NULL -> {
            context.reader.ubyte().bind() // consume byte
            ReferenceType.RefNull(heapTypeDecoder(context).bind())
        }
        else -> ReferenceType.RefNull(abstractHeapTypeDecoder(context).bind())
    }
}

internal const val REFERENCE_TYPE_REF_NULL: UByte = 0x63u
internal const val REFERENCE_TYPE_REF: UByte = 0x64u
