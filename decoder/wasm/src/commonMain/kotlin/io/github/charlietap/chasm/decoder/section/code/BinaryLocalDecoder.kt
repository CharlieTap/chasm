package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.section.index.BinaryLocalIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.LocalIndexDecoder
import io.github.charlietap.chasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryLocalDecoder(
    reader: WasmBinaryReader,
): Result<Local, WasmDecodeError> =
    BinaryLocalDecoder(
        reader = reader,
        localIndexDecoder = ::BinaryLocalIndexDecoder,
        valueTypeDecoder = ::BinaryValueTypeDecoder,
    )

fun BinaryLocalDecoder(
    reader: WasmBinaryReader,
    localIndexDecoder: LocalIndexDecoder,
    valueTypeDecoder: ValueTypeDecoder,
): Result<Local, WasmDecodeError> = binding {

    val index = localIndexDecoder(reader).bind()
    val valueType = valueTypeDecoder(reader).bind()

    Local(index, valueType)
}
