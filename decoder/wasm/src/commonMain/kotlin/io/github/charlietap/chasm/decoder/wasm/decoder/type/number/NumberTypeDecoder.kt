package io.github.charlietap.chasm.decoder.wasm.decoder.type.number

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError

internal typealias NumberTypeDecoder = (UByte) -> Result<NumberType, TypeDecodeError.InvalidNumberType>
