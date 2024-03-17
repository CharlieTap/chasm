package io.github.charlietap.chasm.decoder.wasm.decoder.type.vector

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError

internal typealias VectorTypeDecoder = (UByte) -> Result<VectorType, TypeDecodeError.InvalidVectorType>
