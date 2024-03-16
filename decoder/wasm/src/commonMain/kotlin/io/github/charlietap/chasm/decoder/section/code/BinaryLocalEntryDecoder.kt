package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryLocalEntryDecoder(
    reader: WasmBinaryReader,
): Result<LocalEntry, WasmDecodeError> =
    BinaryLocalEntryDecoder(
        reader = reader,
        valueTypeDecoder = ::BinaryValueTypeDecoder,
    )

fun BinaryLocalEntryDecoder(
    reader: WasmBinaryReader,
    valueTypeDecoder: ValueTypeDecoder,
): Result<LocalEntry, WasmDecodeError> = binding {

    val count = reader.uint().bind()
    val valueType = valueTypeDecoder(reader).bind()

    LocalEntry(count, valueType)
}
