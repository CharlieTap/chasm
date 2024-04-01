package io.github.charlietap.chasm.decoder.wasm.decoder.type.composite

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate.ArrayTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate.BinaryArrayTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate.BinaryStructTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate.StructTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.function.BinaryFunctionTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.function.FunctionTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryCompositeTypeDecoder(
    reader: WasmBinaryReader,
): Result<CompositeType, WasmDecodeError> =
    BinaryCompositeTypeDecoder(
        reader = reader,
        functionTypeDecoder = ::BinaryFunctionTypeDecoder,
        structTypeDecoder = ::BinaryStructTypeDecoder,
        arrayTypeDecoder = ::BinaryArrayTypeDecoder,
    )

internal fun BinaryCompositeTypeDecoder(
    reader: WasmBinaryReader,
    functionTypeDecoder: FunctionTypeDecoder,
    structTypeDecoder: StructTypeDecoder,
    arrayTypeDecoder: ArrayTypeDecoder,
): Result<CompositeType, WasmDecodeError> = binding {
    when (val compositeTypeByte = reader.ubyte().bind()) {
        ARRAY_COMPOSITE_TYPE -> CompositeType.Array(arrayTypeDecoder(reader).bind())
        STRUCT_COMPOSITE_TYPE -> CompositeType.Struct(structTypeDecoder(reader).bind())
        FUNCTION_COMPOSITE_TYPE -> CompositeType.Function(functionTypeDecoder(reader).bind())
        else -> Err(TypeDecodeError.InvalidCompositeType(compositeTypeByte)).bind<CompositeType>()
    }
}

internal const val ARRAY_COMPOSITE_TYPE: UByte = 0x5Eu
internal const val STRUCT_COMPOSITE_TYPE: UByte = 0x5Fu
internal const val FUNCTION_COMPOSITE_TYPE: UByte = 0x60u
