package io.github.charlietap.chasm.decoder.wasm.reader

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun IOErrorWasmFileReader(err: Err<WasmDecodeError.IOError>): WasmBinaryReader = object : WasmBinaryReader {

    override fun byte(): Result<Byte, WasmDecodeError> = err

    override fun ubyte(): Result<UByte, WasmDecodeError> = err

    override fun bytes(amount: Int): Result<ByteArray, WasmDecodeError> = err

    override fun ubytes(amount: UInt): Result<UByteArray, WasmDecodeError> = err

    override fun int(): Result<Int, WasmDecodeError> = err

    override fun uint(): Result<UInt, WasmDecodeError> = err

    override fun long(): Result<Long, WasmDecodeError> = err

    override fun float(): Result<Float, WasmDecodeError> = err

    override fun double(): Result<Double, WasmDecodeError> = err

    override fun exhausted(): Result<Boolean, WasmDecodeError> = err

    override fun position(): UInt = 0u

    override fun peek(): WasmBinaryReader = IOErrorWasmFileReader(err)
}
