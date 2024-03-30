package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
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
        in CONCRETE_HEAP_TYPE_RANGE -> {
            val typeIndex = Index.TypeIndex(reader.s33().bind())
            ConcreteHeapType(typeIndex)
        }
        else -> Err(TypeDecodeError.InvalidHeapType(encoded)).bind<HeapType>()
    }
}

internal val ABSTRACT_HEAP_TYPE_RANGE = HEAP_TYPE_ARRAY..HEAP_TYPE_NO_FUNC
internal val CONCRETE_HEAP_TYPE_RANGE = 0x00u..0x7Fu // 0..127
