package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryHeapTypeDecoder(
    reader: WasmBinaryReader,
): Result<HeapType, WasmDecodeError> =
    BinaryHeapTypeDecoder(
        reader = reader,
        abstractHeapTypeDecoder = ::BinaryAbstractHeapTypeDecoder,
    )

internal fun BinaryHeapTypeDecoder(
    reader: WasmBinaryReader,
    abstractHeapTypeDecoder: AbstractHeapTypeDecoder,
): Result<HeapType, WasmDecodeError> = binding {
    when (val encoded = reader.peek().ubyte().bind()) {
        in ABSTRACT_HEAP_TYPE_RANGE -> {
            reader.ubyte().bind() // consume byte
            abstractHeapTypeDecoder(encoded).bind()
        }
        else -> {
            val typeIndex = Index.TypeIndex(reader.s33().bind())
            ConcreteHeapType.TypeIndex(typeIndex)
        }
    }
}

internal val ABSTRACT_HEAP_TYPE_RANGE = HEAP_TYPE_ARRAY..HEAP_TYPE_NO_FUNC
