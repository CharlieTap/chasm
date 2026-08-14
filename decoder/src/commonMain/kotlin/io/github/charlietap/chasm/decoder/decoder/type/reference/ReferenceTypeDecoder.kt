package io.github.charlietap.chasm.decoder.decoder.type.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.AbstractHeapTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

internal fun ReferenceTypeDecoder(
    context: CodeBodyDecoderContext,
): Result<ReferenceType, WasmDecodeError> =
    ReferenceTypeDecoder(
        context = context,
        heapTypeDecoder = ::HeapTypeDecoder,
        abstractHeapTypeDecoder = ::AbstractHeapTypeDecoder,
    )

internal inline fun ReferenceTypeDecoder(
    context: CodeBodyDecoderContext,
    crossinline heapTypeDecoder: CodeBodyDecoder<HeapType>,
    crossinline abstractHeapTypeDecoder: CodeBodyDecoder<AbstractHeapType>,
): Result<ReferenceType, WasmDecodeError> = binding {
    when (
        context.reader
            .peekUByte()
    ) {
        REFERENCE_TYPE_REF -> {
            context.reader.ubyte() // consume byte
            ReferenceType.Ref(heapTypeDecoder(context).bind())
        }
        REFERENCE_TYPE_REF_NULL -> {
            context.reader.ubyte() // consume byte
            ReferenceType.RefNull(heapTypeDecoder(context).bind())
        }
        else -> ReferenceType.RefNull(abstractHeapTypeDecoder(context).bind())
    }
}

internal const val REFERENCE_TYPE_REF_NULL: UByte = 0x63u
internal const val REFERENCE_TYPE_REF: UByte = 0x64u
