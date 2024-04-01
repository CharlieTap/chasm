package io.github.charlietap.chasm.decoder.wasm.decoder.type.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.AbstractHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.BinaryAbstractHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.BinaryHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
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
        abstractHeapTypeDecoder = ::BinaryAbstractHeapTypeDecoder,
    )

internal fun BinaryReferenceTypeDecoder(
    reader: WasmBinaryReader,
    encoded: UByte,
    heapTypeDecoder: HeapTypeDecoder,
    abstractHeapTypeDecoder: AbstractHeapTypeDecoder,
): Result<ReferenceType, WasmDecodeError> = binding {
    when (encoded) {
        REFERENCE_TYPE_REF -> ReferenceType.Ref(heapTypeDecoder(reader).bind())
        REFERENCE_TYPE_REF_NULL -> ReferenceType.RefNull(heapTypeDecoder(reader).bind())
        else -> ReferenceType.RefNull(abstractHeapTypeDecoder(encoded).bind())
    }
}

internal const val REFERENCE_TYPE_REF_NULL: UByte = 0x63u
internal const val REFERENCE_TYPE_REF: UByte = 0x64u
