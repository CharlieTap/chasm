package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.decoder.error.ComponentDecoderError
import io.github.charlietap.chasm.ast.component.Component as AstComponent

@InternalChasmApi
fun interface ComponentDecoder<Source> :
    (ComponentConfig, Source) -> Result<AstComponent, ComponentDecoderError>
