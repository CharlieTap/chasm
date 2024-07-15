package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ImportDecoder(
    context: DecoderContext,
): Result<Import, WasmDecodeError> =
    ImportDecoder(
        context = context,
        nameValueDecoder = ::NameValueDecoder,
        importDescriptorDecoder = ::ImportDescriptorDecoder,
    )

internal fun ImportDecoder(
    context: DecoderContext,
    nameValueDecoder: Decoder<NameValue>,
    importDescriptorDecoder: Decoder<Import.Descriptor>,
): Result<Import, WasmDecodeError> = binding {

    val moduleName = nameValueDecoder(context).bind()
    val entityName = nameValueDecoder(context).bind()
    val descriptor = importDescriptorDecoder(context).bind()

    Import(moduleName, entityName, descriptor)
}
