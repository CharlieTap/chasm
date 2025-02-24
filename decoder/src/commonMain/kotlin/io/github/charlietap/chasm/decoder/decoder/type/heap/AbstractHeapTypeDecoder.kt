package io.github.charlietap.chasm.decoder.decoder.type.heap

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.AbstractHeapType

internal fun AbstractHeapTypeDecoder(
    context: DecoderContext,
): Result<AbstractHeapType, WasmDecodeError> = binding {
    when (val encoded = context.reader.ubyte().bind()) {
        HEAP_TYPE_EXCEPTION -> AbstractHeapType.Exception
        HEAP_TYPE_ARRAY -> AbstractHeapType.Array
        HEAP_TYPE_STRUCT -> AbstractHeapType.Struct
        HEAP_TYPE_I31 -> AbstractHeapType.I31
        HEAP_TYPE_EQ -> AbstractHeapType.Eq
        HEAP_TYPE_ANY -> AbstractHeapType.Any
        HEAP_TYPE_EXTERN -> AbstractHeapType.Extern
        HEAP_TYPE_FUNC -> AbstractHeapType.Func
        HEAP_TYPE_NONE -> AbstractHeapType.None
        HEAP_TYPE_NO_EXTERN -> AbstractHeapType.NoExtern
        HEAP_TYPE_NO_FUNC -> AbstractHeapType.NoFunc
        HEAP_TYPE_NO_EXCEPTION -> AbstractHeapType.NoException
        else -> Err(TypeDecodeError.InvalidHeapType(encoded)).bind<AbstractHeapType>()
    }
}

internal const val HEAP_TYPE_EXCEPTION: UByte = 0x69u
internal const val HEAP_TYPE_ARRAY: UByte = 0x6Au
internal const val HEAP_TYPE_STRUCT: UByte = 0x6Bu
internal const val HEAP_TYPE_I31: UByte = 0x6Cu
internal const val HEAP_TYPE_EQ: UByte = 0x6Du
internal const val HEAP_TYPE_ANY: UByte = 0x6Eu
internal const val HEAP_TYPE_EXTERN: UByte = 0x6Fu
internal const val HEAP_TYPE_FUNC: UByte = 0x70u
internal const val HEAP_TYPE_NONE: UByte = 0x71u
internal const val HEAP_TYPE_NO_EXTERN: UByte = 0x72u
internal const val HEAP_TYPE_NO_FUNC: UByte = 0x73u
internal const val HEAP_TYPE_NO_EXCEPTION: UByte = 0x74u
