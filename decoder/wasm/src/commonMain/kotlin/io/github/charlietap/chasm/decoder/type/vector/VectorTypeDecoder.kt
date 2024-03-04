package io.github.charlietap.chasm.decoder.type.vector

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.error.TypeDecodeError

typealias VectorTypeDecoder = (UByte) -> Result<VectorType, TypeDecodeError.InvalidVectorType>
