package io.github.charlietap.chasm.decoder.type.number

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.error.TypeDecodeError

typealias NumberTypeDecoder = (UByte) -> Result<NumberType, TypeDecodeError.InvalidNumberType>
