package io.github.charlietap.chasm.decoder.wasm.decoder.type.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.BinaryHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryReferenceTypeDecoder(
    reader: WasmBinaryReader,
    encoded: UByte,
): Result<ReferenceType, WasmDecodeError> =
    BinaryReferenceTypeDecoder(
        reader = reader,
        encoded = encoded,
        heapTypeDecoder = ::BinaryHeapTypeDecoder,
    )

internal fun BinaryReferenceTypeDecoder(
    reader: WasmBinaryReader,
    encoded: UByte,
    heapTypeDecoder: HeapTypeDecoder,
): Result<ReferenceType, WasmDecodeError> = binding {
    when (encoded) {
        REFERENCE_TYPE_FUNC -> ReferenceType.RefNull(HeapType.Func)
        REFERENCE_TYPE_EXTERN -> ReferenceType.RefNull(HeapType.Extern)
        REFERENCE_TYPE_REF -> ReferenceType.Ref(heapTypeDecoder(reader).bind())
        REFERENCE_TYPE_REF_NULL -> ReferenceType.RefNull(heapTypeDecoder(reader).bind())
        else -> Err(TypeDecodeError.InvalidReferenceType(encoded)).bind<ReferenceType>()
    }
}

internal const val REFERENCE_TYPE_REF_NULL: UByte = 0x63u
internal const val REFERENCE_TYPE_REF: UByte = 0x64u
internal const val REFERENCE_TYPE_EXTERN: UByte = 0x6Fu
internal const val REFERENCE_TYPE_FUNC: UByte = 0x70u
