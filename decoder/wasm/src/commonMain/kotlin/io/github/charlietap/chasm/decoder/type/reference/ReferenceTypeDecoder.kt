package io.github.charlietap.chasm.decoder.type.reference

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.error.TypeDecodeError

typealias ReferenceTypeDecoder = (UByte) -> Result<ReferenceType, TypeDecodeError.InvalidReferenceType>
