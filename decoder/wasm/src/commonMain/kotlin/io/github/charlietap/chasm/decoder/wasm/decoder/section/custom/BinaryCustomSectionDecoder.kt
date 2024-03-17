package io.github.charlietap.chasm.decoder.wasm.decoder.section.custom

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.BinaryNameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.trackBytes
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.CustomSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryCustomSectionDecoder(
    private val nameValueDecoder: NameValueDecoder = ::BinaryNameValueDecoder,
) : CustomSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<CustomSection, WasmDecodeError> = binding {

        val (nameResult, bytesConsumed) = reader.trackBytes {
            nameValueDecoder(this)
        }

        val nameValue = nameResult.bind()
        val payloadSize = size.size - bytesConsumed
        val payload = reader.ubytes(payloadSize).bind()

        CustomSection(Custom(nameValue, payload))
    }
}
