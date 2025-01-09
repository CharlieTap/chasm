package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.context.scope.SectionScope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.decoder.magic.BinaryMagicNumberValidator
import io.github.charlietap.chasm.decoder.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.SectionTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.version.VersionDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecodeError
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.decoder.ext.section
import io.github.charlietap.chasm.decoder.reader.SourceWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.Section
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.stream.SourceReader

fun WasmModuleDecoder(
    config: ModuleConfig,
    source: SourceReader,
): Result<Module, ModuleDecoderError> =
    WasmModuleDecoder(
        source = source,
        config = config,
        readerFactory = ::SourceWasmBinaryReader,
        magicNumberValidator = ::BinaryMagicNumberValidator,
        versionDecoder = ::VersionDecoder,
        sectionTypeDecoder = ::SectionTypeDecoder,
        sectionDecoder = ::SectionDecoder,
        scope = ::SectionScope,
    )

internal fun WasmModuleDecoder(
    config: ModuleConfig,
    source: SourceReader,
    readerFactory: BinaryReaderFactory,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: Decoder<Version>,
    sectionTypeDecoder: Decoder<SectionType>,
    sectionDecoder: Decoder<Section>,
    scope: Scope<Pair<SectionSize, SectionType>>,
): Result<Module, ModuleDecoderError> = binding {

    val reader = readerFactory(source).apply {
        magicNumberValidator(this).bind()
    }
    val context = DecoderContext(config, reader)

    val version = versionDecoder(context).bind()
    val builder = ModuleBuilder(version)
    var previousSectionType = SectionType.Custom

    while (reader.exhausted().bind().not()) {

        val sectionType = sectionTypeDecoder(context).bind()
        if (sectionType.ordinal <= previousSectionType.ordinal && sectionType != SectionType.Custom) {
            Err(ModuleDecodeError.ModuleMalformed).bind()
        }
        val sectionSize = SectionSize(reader.uint().bind())

        val scopedContext = scope(context, sectionSize to sectionType).bind()
        val section = sectionDecoder(scopedContext).bind()

        builder.section(section)
        previousSectionType = sectionType
    }

    builder.build().bind()
}
