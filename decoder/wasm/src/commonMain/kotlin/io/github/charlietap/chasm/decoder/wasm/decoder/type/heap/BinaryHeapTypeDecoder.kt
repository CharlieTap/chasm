package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryHeapTypeDecoder(
    reader: WasmBinaryReader,
): Result<HeapType, WasmDecodeError> = binding {

    val encoded = reader.peek().ubyte().bind()

    when (encoded) {
        HEAP_TYPE_FUNC -> HeapType.Func.also { reader.ubyte() }
        HEAP_TYPE_EXTERN -> HeapType.Extern.also { reader.ubyte() }
        else -> {
            // Not using TypeIndexDecoder here as it's stored in a signed leb s33
            val typeIndex = Index.TypeIndex(reader.long().bind().toUInt())
            HeapType.TypeIndex(typeIndex)
        }
    }
}

internal const val HEAP_TYPE_EXTERN: UByte = 0x6Fu
internal const val HEAP_TYPE_FUNC: UByte = 0x70u
