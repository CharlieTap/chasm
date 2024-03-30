package io.github.charlietap.chasm.decoder.wasm.reader

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal interface WasmBinaryReader {

    fun byte(): Result<Byte, WasmDecodeError>

    fun ubyte(): Result<UByte, WasmDecodeError>

    fun bytes(amount: Int): Result<ByteArray, WasmDecodeError>

    fun ubytes(amount: UInt): Result<UByteArray, WasmDecodeError>

    fun int(): Result<Int, WasmDecodeError>

    fun uint(): Result<UInt, WasmDecodeError>

    fun s33(): Result<UInt, WasmDecodeError>

    fun long(): Result<Long, WasmDecodeError>

    fun float(): Result<Float, WasmDecodeError>

    fun double(): Result<Double, WasmDecodeError>

    fun exhausted(): Result<Boolean, WasmDecodeError>

    fun position(): UInt

    fun peek(): WasmBinaryReader
}
