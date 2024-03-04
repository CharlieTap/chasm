package io.github.charlietap.chasm.reader

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import kotlin.test.fail

fun FakeWasmBinaryReader(
    fakeByteReader: (() -> Result<Byte, WasmDecodeError>)? = null,
    fakeUByteReader: (() -> Result<UByte, WasmDecodeError>)? = null,
    fakeBytesReader: ((Int) -> Result<ByteArray, WasmDecodeError>)? = null,
    fakeUBytesReader: ((UInt) -> Result<UByteArray, WasmDecodeError>)? = null,
    fakeIntReader: (() -> Result<Int, WasmDecodeError>)? = null,
    fakeUIntReader: (() -> Result<UInt, WasmDecodeError>)? = null,
    fakeLongReader: (() -> Result<Long, WasmDecodeError>)? = null,
    fakeFloatReader: (() -> Result<Float, WasmDecodeError>)? = null,
    fakeDoubleReader: (() -> Result<Double, WasmDecodeError>)? = null,
    fakeExhaustedReader: (() -> Result<Boolean, WasmDecodeError>)? = null,
    fakePositionReader: (() -> UInt)? = null,
    fakePeekReader: (() -> WasmBinaryReader)? = null,
    message: String? = null,
): WasmBinaryReader = object : WasmBinaryReader {

    val fail: () -> Nothing = {
        fail(message ?: "binary reader should never be called in this scenario")
    }

    override fun byte(): Result<Byte, WasmDecodeError> = (fakeByteReader ?: fail)()

    override fun ubyte(): Result<UByte, WasmDecodeError> = (fakeUByteReader ?: fail)()

    override fun bytes(amount: Int): Result<ByteArray, WasmDecodeError> = fakeBytesReader?.let {
        fakeBytesReader(amount)
    } ?: fail()

    override fun ubytes(amount: UInt): Result<UByteArray, WasmDecodeError> = fakeUBytesReader?.let {
        fakeUBytesReader(amount)
    } ?: fail()

    override fun int(): Result<Int, WasmDecodeError> = (fakeIntReader ?: fail)()

    override fun uint(): Result<UInt, WasmDecodeError> = (fakeUIntReader ?: fail)()

    override fun long(): Result<Long, WasmDecodeError> = (fakeLongReader ?: fail)()

    override fun float(): Result<Float, WasmDecodeError> = (fakeFloatReader ?: fail)()

    override fun double(): Result<Double, WasmDecodeError> = (fakeDoubleReader ?: fail)()

    override fun exhausted(): Result<Boolean, WasmDecodeError> = (fakeExhaustedReader ?: fail)()

    override fun position(): UInt = fakePositionReader?.invoke() ?: fail()

    override fun peek(): WasmBinaryReader = fakePeekReader?.invoke() ?: fail()
}

fun FakeByteReader(
    byte: () -> Result<Byte, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeByteReader = byte)

fun FakeUByteReader(
    byte: () -> Result<UByte, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUByteReader = byte)

fun FakeIntReader(
    int: () -> Result<Int, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeIntReader = int)

fun FakeUIntReader(
    uint: () -> Result<UInt, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUIntReader = uint)

fun FakeLongReader(
    long: () -> Result<Long, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeLongReader = long)

fun FakeFloatReader(
    float: () -> Result<Float, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeFloatReader = float)

fun FakeDoubleReader(
    double: () -> Result<Double, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeDoubleReader = double)

fun FakeUBytesReader(
    bytes: (UInt) -> Result<UByteArray, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUBytesReader = bytes)
