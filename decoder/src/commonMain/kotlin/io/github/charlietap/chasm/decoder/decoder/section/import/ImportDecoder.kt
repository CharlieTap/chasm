package io.github.charlietap.chasm.decoder.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ImportDecoder(
    context: DecoderContext,
): Result<Import, WasmDecodeError> =
    ImportDecoder(
        context = context,
        nameValueDecoder = ::NameValueDecoder,
        importDescriptorDecoder = ::ImportDescriptorDecoder,
    )

internal inline fun ImportDecoder(
    context: DecoderContext,
    crossinline nameValueDecoder: Decoder<NameValue>,
    crossinline importDescriptorDecoder: Decoder<Import.Descriptor>,
): Result<Import, WasmDecodeError> = binding {

    val moduleName = nameValueDecoder(context).bind()
    val entityName = nameValueDecoder(context).bind()
    val descriptor = importDescriptorDecoder(context).bind()

    Import(moduleName, entityName, descriptor)
}
