package io.github.charlietap.chasm.decoder.wasm.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.error.ValueDecodeError

internal fun ByteArray.toFloatLe(): Result<Float, ValueDecodeError.InvalidFloat> = binding {

    if (size != 4) {
        Err(ValueDecodeError.InvalidFloat(this@toFloatLe)).bind<ValueDecodeError.InvalidFloat>()
    }

    val intValue = foldIndexed(0) { index, acc, byte ->
        acc or (byte.toInt() and 0xFF shl (8 * index))
    }

    Float.fromBits(intValue)
}

internal fun ByteArray.toDoubleLe(): Result<Double, ValueDecodeError.InvalidDouble> = binding {

    if (size != 8) {
        Err(ValueDecodeError.InvalidDouble(this@toDoubleLe)).bind<ValueDecodeError.InvalidDouble>()
    }

    val longValue = foldIndexed(0L) { index, acc, byte ->
        acc or (byte.toLong() and 0xFF shl (8 * index))
    }

    Double.fromBits(longValue)
}
