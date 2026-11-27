package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.ast.component.Version
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.decoder.builder.ComponentBuilder
import io.github.charlietap.chasm.decoder.component.section.ComponentSection
import io.github.charlietap.chasm.decoder.component.section.ComponentSectionType
import io.github.charlietap.chasm.decoder.context.ComponentDecoderContext
import io.github.charlietap.chasm.decoder.context.scope.ComponentScopedDecoder
import io.github.charlietap.chasm.decoder.context.scope.ComponentSectionScope
import io.github.charlietap.chasm.decoder.decoder.component.ComponentLayerValidator
import io.github.charlietap.chasm.decoder.decoder.component.ComponentVersionDecoder
import io.github.charlietap.chasm.decoder.decoder.component.section.ComponentSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.component.section.ComponentSectionTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.decoder.magic.BinaryMagicNumberValidator
import io.github.charlietap.chasm.decoder.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.error.ComponentDecoderError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.stream.SourceReader
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder as ComponentContextDecoder

@InternalChasmApi
fun WasmComponentDecoder(
    config: ComponentConfig,
    bytes: ByteArray,
): Result<Component, ComponentDecoderError> {
    val context = ComponentDecoderContext(
        config = config,
        reader = BinaryReader(bytes),
    )
    return WasmComponentDecoder(context)
}

@InternalChasmApi
fun WasmComponentDecoder(
    config: ComponentConfig,
    source: SourceReader,
): Result<Component, ComponentDecoderError> {
    val context = ComponentDecoderContext(
        config = config,
        reader = BinaryReader(source),
    )
    return WasmComponentDecoder(context)
}

internal fun WasmComponentDecoder(
    context: ComponentDecoderContext,
): Result<Component, WasmDecodeError> = WasmComponentDecoder(
    context = context,
    magicNumberValidator = ::BinaryMagicNumberValidator,
    versionDecoder = ::ComponentVersionDecoder,
    layerValidator = ::ComponentLayerValidator,
    sectionTypeDecoder = ::ComponentSectionTypeDecoder,
    sectionDecoder = ::ComponentSectionDecoder,
    scope = ::ComponentSectionScope,
)

internal fun WasmComponentDecoder(
    config: ComponentConfig,
    source: SourceReader,
    readerFactory: BinaryReaderFactory,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: ComponentContextDecoder<Version>,
    layerValidator: ComponentContextDecoder<Unit>,
    sectionTypeDecoder: ComponentContextDecoder<ComponentSectionType>,
    sectionDecoder: ComponentContextDecoder<ComponentSection>,
    scope: ComponentScopedDecoder<Pair<SectionSize, ComponentSectionType>, ComponentSection>,
): Result<Component, WasmDecodeError> {
    val context = ComponentDecoderContext(config, readerFactory(source))
    return WasmComponentDecoder(
        context = context,
        magicNumberValidator = magicNumberValidator,
        versionDecoder = versionDecoder,
        layerValidator = layerValidator,
        sectionTypeDecoder = sectionTypeDecoder,
        sectionDecoder = sectionDecoder,
        scope = scope,
    )
}

internal fun WasmComponentDecoder(
    context: ComponentDecoderContext,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: ComponentContextDecoder<Version>,
    layerValidator: ComponentContextDecoder<Unit>,
    sectionTypeDecoder: ComponentContextDecoder<ComponentSectionType>,
    sectionDecoder: ComponentContextDecoder<ComponentSection>,
    scope: ComponentScopedDecoder<Pair<SectionSize, ComponentSectionType>, ComponentSection>,
): Result<Component, WasmDecodeError> = try {
    binding {
        magicNumberValidator(context.reader).bind()
        val version = versionDecoder(context).bind()
        layerValidator(context).bind()
        val builder = ComponentBuilder(version)

        while (context.reader.exhausted().not()) {
            val sectionType = sectionTypeDecoder(context).bind()
            val sectionSize = SectionSize(context.reader.uint())
            val section = scope(
                context,
                sectionSize to sectionType,
                sectionDecoder,
            ).bind()
            builder.section(section)
        }

        builder.build()
    }
} catch (error: WasmDecodeException) {
    Err(error.error)
} catch (error: Throwable) {
    Err(WasmDecodeError.IOError(error))
}
