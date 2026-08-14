@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.decoder.reader

import io.github.charlietap.chasm.decoder.error.ReaderDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException

internal class BufferedWasmBinaryReader private constructor(
    private val source: ByteSource?,
    private var buffer: ByteArray,
    private var cursor: Int,
    private var end: Int,
    private var bufferOffset: ULong,
    private var logicalLimit: ULong,
    private var sourceExhausted: Boolean,
) : WasmBinaryReader {

    constructor(bytes: ByteArray) : this(
        bytes = bytes,
        start = 0,
        end = bytes.size,
    )

    constructor(
        bytes: ByteArray,
        start: Int,
        end: Int,
    ) : this(
        source = null,
        buffer = bytes,
        cursor = start,
        end = end,
        bufferOffset = 0u,
        logicalLimit = end.toULong(),
        sourceExhausted = true,
    ) {
        require(start in 0..end)
        require(end <= bytes.size)
    }

    constructor(
        source: ByteSource,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
    ) : this(
        source = source,
        buffer = ByteArray(bufferSize.coerceAtLeast(MAX_PRIMITIVE_SIZE)),
        cursor = 0,
        end = 0,
        bufferOffset = 0u,
        logicalLimit = ULong.MAX_VALUE,
        sourceExhausted = false,
    )

    override fun byte(): Byte {
        if (cursor < end && (logicalLimit == ULong.MAX_VALUE || absolutePosition() < logicalLimit)) {
            return buffer[cursor++]
        }
        return byteSlow()
    }

    override fun ubyte(): UByte = byte().toUByte()

    override fun bytes(amount: Int): ByteArray {
        if (amount < 0 || amount.toULong() > remainingInLimit()) noMoreElements()
        if (amount == 0) return ByteArray(0)

        val available = contiguousAvailable()
        if (amount <= available) {
            val result = buffer.copyOfRange(cursor, cursor + amount)
            cursor += amount
            return result
        }

        val result = ByteArray(amount)
        var copied = 0
        while (copied < amount) {
            if (contiguousAvailable() == 0 && request(1).not()) noMoreElements()

            val count = minOf(contiguousAvailable(), amount - copied)
            buffer.copyInto(result, copied, cursor, cursor + count)
            cursor += count
            copied += count
        }
        return result
    }

    override fun ubytes(amount: UInt): UByteArray = bytes(amount.toInt()).asUByteArray()

    override fun float(): Float {
        if (request(Float.SIZE_BYTES).not()) noMoreElements()

        val start = cursor
        val value =
            (buffer[start].toInt() and BYTE_MASK) or
                ((buffer[start + 1].toInt() and BYTE_MASK) shl 8) or
                ((buffer[start + 2].toInt() and BYTE_MASK) shl 16) or
                ((buffer[start + 3].toInt() and BYTE_MASK) shl 24)
        cursor = start + Float.SIZE_BYTES
        return Float.fromBits(value)
    }

    override fun double(): Double {
        if (request(Double.SIZE_BYTES).not()) noMoreElements()

        val start = cursor
        val value =
            (buffer[start].toLong() and BYTE_MASK_LONG) or
                ((buffer[start + 1].toLong() and BYTE_MASK_LONG) shl 8) or
                ((buffer[start + 2].toLong() and BYTE_MASK_LONG) shl 16) or
                ((buffer[start + 3].toLong() and BYTE_MASK_LONG) shl 24) or
                ((buffer[start + 4].toLong() and BYTE_MASK_LONG) shl 32) or
                ((buffer[start + 5].toLong() and BYTE_MASK_LONG) shl 40) or
                ((buffer[start + 6].toLong() and BYTE_MASK_LONG) shl 48) or
                ((buffer[start + 7].toLong() and BYTE_MASK_LONG) shl 56)
        cursor = start + Double.SIZE_BYTES
        return Double.fromBits(value)
    }

    override fun exhausted(): Boolean =
        absolutePosition() >= logicalLimit ||
            (contiguousAvailable() == 0 && request(1).not())

    override fun position(): UInt = absolutePosition().toUInt()

    override fun peekUByte(): UByte {
        if (request(1).not()) noMoreElements()
        return buffer[cursor].toUByte()
    }

    override fun peekUInt(): UInt {
        request(U32_MAX_BYTES)
        val currentPosition = cursor
        val value = uint()
        cursor = currentPosition
        return value
    }

    override fun pushLimit(size: UInt): ULong {
        val position = absolutePosition()
        if (size.toULong() > logicalLimit - position) noMoreElements()

        return logicalLimit.also {
            logicalLimit = position + size
        }
    }

    override fun restoreLimit(limit: ULong) {
        logicalLimit = limit
    }

    fun skip(amount: Int) {
        require(source == null)
        if (amount < 0 || amount > contiguousAvailable()) noMoreElements()
        cursor += amount
    }

    private fun byteSlow(): Byte {
        if (request(1).not()) noMoreElements()
        return buffer[cursor++]
    }

    private fun request(amount: Int): Boolean {
        require(amount >= 0)
        if (amount > buffer.size) return false
        if (amount.toULong() > remainingInLimit()) return false
        if (end - cursor >= amount) return true
        if (sourceExhausted || source == null) return false

        if (cursor > 0) {
            val remaining = end - cursor
            buffer.copyInto(buffer, 0, cursor, end)
            bufferOffset += cursor.toULong()
            cursor = 0
            end = remaining
        }

        while (end < amount && sourceExhausted.not()) {
            val bytesRead = readSource()
            when {
                bytesRead < 0 -> sourceExhausted = true
                bytesRead == 0 -> throw WasmDecodeException(
                    WasmDecodeError.IOError(IllegalStateException("ByteSource made no progress")),
                )
                else -> end += bytesRead
            }
        }

        return end >= amount
    }

    private fun readSource(): Int {
        val capacity = buffer.size - end
        val bytesRead = try {
            source!!.readAtMostTo(buffer, end, buffer.size)
        } catch (error: WasmDecodeException) {
            throw error
        } catch (error: Throwable) {
            throw WasmDecodeException(WasmDecodeError.IOError(error))
        }

        if (bytesRead !in -1..capacity) {
            throw WasmDecodeException(
                WasmDecodeError.IOError(
                    IllegalStateException("ByteSource returned $bytesRead for a $capacity byte destination"),
                ),
            )
        }
        return bytesRead
    }

    private inline fun contiguousAvailable(): Int = if (logicalLimit == ULong.MAX_VALUE) {
        end - cursor
    } else {
        minOf(
            end - cursor,
            remainingInLimit().coerceAtMost(Int.MAX_VALUE.toULong()).toInt(),
        )
    }

    private inline fun remainingInLimit(): ULong = if (logicalLimit == ULong.MAX_VALUE) {
        ULong.MAX_VALUE
    } else {
        logicalLimit - absolutePosition()
    }

    private inline fun absolutePosition(): ULong = bufferOffset + cursor.toULong()

    private fun noMoreElements(): Nothing = throw NoSuchElementException("Not enough elements")

    override fun short(): Short {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            var value = byte0 and PAYLOAD_MASK
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                if (byte0 and SIGN_BIT != 0) value = value or (-1 shl 7)
                return value.toShort()
            }

            if (contiguousAvailable() >= I16_MAX_BYTES) {
                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK) shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    if (byte1 and SIGN_BIT != 0) value = value or (-1 shl 14)
                    return value.toShort()
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                cursor = start + I16_MAX_BYTES
                if (byte2 !in 0x00..0x01 && byte2 !in 0x7E..0x7F) {
                    invalidIntegerEncoding(Short.SIZE_BITS)
                }
                return (value or ((byte2 and 0x03) shl 14)).toShort()
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        var value = byte0 and PAYLOAD_MASK
        if (byte0 < CONTINUATION_BIT) {
            if (byte0 and SIGN_BIT != 0) value = value or (-1 shl 7)
            return value.toShort()
        }

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK) shl 7)
        if (byte1 < CONTINUATION_BIT) {
            if (byte1 and SIGN_BIT != 0) value = value or (-1 shl 14)
            return value.toShort()
        }

        val byte2 = byte().toInt() and BYTE_MASK
        if (byte2 !in 0x00..0x01 && byte2 !in 0x7E..0x7F) {
            invalidIntegerEncoding(Short.SIZE_BITS)
        }
        return (value or ((byte2 and 0x03) shl 14)).toShort()
    }

    override fun ushort(): UShort {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                return byte0.toUShort()
            }

            if (contiguousAvailable() >= U16_MAX_BYTES) {
                var value = byte0 and PAYLOAD_MASK

                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK) shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    return value.toUShort()
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                cursor = start + U16_MAX_BYTES
                if (byte2 > 0x03) invalidIntegerEncoding(UShort.SIZE_BITS)
                return (value or (byte2 shl 14)).toUShort()
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        if (byte0 < CONTINUATION_BIT) return byte0.toUShort()

        var value = byte0 and PAYLOAD_MASK

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK) shl 7)
        if (byte1 < CONTINUATION_BIT) return value.toUShort()

        val byte2 = byte().toInt() and BYTE_MASK
        if (byte2 > 0x03) invalidIntegerEncoding(UShort.SIZE_BITS)
        return (value or (byte2 shl 14)).toUShort()
    }

    override fun int(): Int {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            var value = byte0 and PAYLOAD_MASK
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                if (byte0 and SIGN_BIT != 0) value = value or (-1 shl 7)
                return value
            }

            if (contiguousAvailable() >= I32_MAX_BYTES) {
                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK) shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    if (byte1 and SIGN_BIT != 0) value = value or (-1 shl 14)
                    return value
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                value = value or ((byte2 and PAYLOAD_MASK) shl 14)
                if (byte2 < CONTINUATION_BIT) {
                    cursor = start + 3
                    if (byte2 and SIGN_BIT != 0) value = value or (-1 shl 21)
                    return value
                }

                val byte3 = buffer[start + 3].toInt() and BYTE_MASK
                value = value or ((byte3 and PAYLOAD_MASK) shl 21)
                if (byte3 < CONTINUATION_BIT) {
                    cursor = start + 4
                    if (byte3 and SIGN_BIT != 0) value = value or (-1 shl 28)
                    return value
                }

                val byte4 = buffer[start + 4].toInt() and BYTE_MASK
                cursor = start + I32_MAX_BYTES
                if (byte4 !in 0x00..0x07 && byte4 !in 0x78..0x7F) {
                    invalidIntegerEncoding(Int.SIZE_BITS)
                }
                return value or ((byte4 and 0x0F) shl 28)
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        var value = byte0 and PAYLOAD_MASK
        if (byte0 < CONTINUATION_BIT) {
            if (byte0 and SIGN_BIT != 0) value = value or (-1 shl 7)
            return value
        }

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK) shl 7)
        if (byte1 < CONTINUATION_BIT) {
            if (byte1 and SIGN_BIT != 0) value = value or (-1 shl 14)
            return value
        }

        val byte2 = byte().toInt() and BYTE_MASK
        value = value or ((byte2 and PAYLOAD_MASK) shl 14)
        if (byte2 < CONTINUATION_BIT) {
            if (byte2 and SIGN_BIT != 0) value = value or (-1 shl 21)
            return value
        }

        val byte3 = byte().toInt() and BYTE_MASK
        value = value or ((byte3 and PAYLOAD_MASK) shl 21)
        if (byte3 < CONTINUATION_BIT) {
            if (byte3 and SIGN_BIT != 0) value = value or (-1 shl 28)
            return value
        }

        val byte4 = byte().toInt() and BYTE_MASK
        if (byte4 !in 0x00..0x07 && byte4 !in 0x78..0x7F) {
            invalidIntegerEncoding(Int.SIZE_BITS)
        }
        return value or ((byte4 and 0x0F) shl 28)
    }

    override fun uint(): UInt {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                return byte0.toUInt()
            }

            if (contiguousAvailable() >= U32_MAX_BYTES) {
                var value = byte0 and PAYLOAD_MASK

                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK) shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    return value.toUInt()
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                value = value or ((byte2 and PAYLOAD_MASK) shl 14)
                if (byte2 < CONTINUATION_BIT) {
                    cursor = start + 3
                    return value.toUInt()
                }

                val byte3 = buffer[start + 3].toInt() and BYTE_MASK
                value = value or ((byte3 and PAYLOAD_MASK) shl 21)
                if (byte3 < CONTINUATION_BIT) {
                    cursor = start + 4
                    return value.toUInt()
                }

                val byte4 = buffer[start + 4].toInt() and BYTE_MASK
                cursor = start + U32_MAX_BYTES
                if (byte4 > 0x0F) invalidIntegerEncoding(UInt.SIZE_BITS)
                return (value or (byte4 shl 28)).toUInt()
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        if (byte0 < CONTINUATION_BIT) return byte0.toUInt()

        var value = byte0 and PAYLOAD_MASK

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK) shl 7)
        if (byte1 < CONTINUATION_BIT) return value.toUInt()

        val byte2 = byte().toInt() and BYTE_MASK
        value = value or ((byte2 and PAYLOAD_MASK) shl 14)
        if (byte2 < CONTINUATION_BIT) return value.toUInt()

        val byte3 = byte().toInt() and BYTE_MASK
        value = value or ((byte3 and PAYLOAD_MASK) shl 21)
        if (byte3 < CONTINUATION_BIT) return value.toUInt()

        val byte4 = byte().toInt() and BYTE_MASK
        if (byte4 > 0x0F) invalidIntegerEncoding(UInt.SIZE_BITS)
        return (value or (byte4 shl 28)).toUInt()
    }

    override fun s33(): Long {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            var value = (byte0 and PAYLOAD_MASK).toLong()
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                if (byte0 and SIGN_BIT != 0) value = value or (-1L shl 7)
                return value
            }

            if (contiguousAvailable() >= I33_MAX_BYTES) {
                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK).toLong() shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    if (byte1 and SIGN_BIT != 0) value = value or (-1L shl 14)
                    return value
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                value = value or ((byte2 and PAYLOAD_MASK).toLong() shl 14)
                if (byte2 < CONTINUATION_BIT) {
                    cursor = start + 3
                    if (byte2 and SIGN_BIT != 0) value = value or (-1L shl 21)
                    return value
                }

                val byte3 = buffer[start + 3].toInt() and BYTE_MASK
                value = value or ((byte3 and PAYLOAD_MASK).toLong() shl 21)
                if (byte3 < CONTINUATION_BIT) {
                    cursor = start + 4
                    if (byte3 and SIGN_BIT != 0) value = value or (-1L shl 28)
                    return value
                }

                val byte4 = buffer[start + 4].toInt() and BYTE_MASK
                cursor = start + I33_MAX_BYTES
                if (byte4 !in 0x00..0x0F && byte4 !in 0x70..0x7F) {
                    invalidIntegerEncoding(I33_BITS)
                }

                value = value or ((byte4 and 0x1F).toLong() shl 28)
                if (byte4 and 0x10 != 0) value = value or (-1L shl I33_BITS)
                return value
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        var value = (byte0 and PAYLOAD_MASK).toLong()
        if (byte0 < CONTINUATION_BIT) {
            if (byte0 and SIGN_BIT != 0) value = value or (-1L shl 7)
            return value
        }

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK).toLong() shl 7)
        if (byte1 < CONTINUATION_BIT) {
            if (byte1 and SIGN_BIT != 0) value = value or (-1L shl 14)
            return value
        }

        val byte2 = byte().toInt() and BYTE_MASK
        value = value or ((byte2 and PAYLOAD_MASK).toLong() shl 14)
        if (byte2 < CONTINUATION_BIT) {
            if (byte2 and SIGN_BIT != 0) value = value or (-1L shl 21)
            return value
        }

        val byte3 = byte().toInt() and BYTE_MASK
        value = value or ((byte3 and PAYLOAD_MASK).toLong() shl 21)
        if (byte3 < CONTINUATION_BIT) {
            if (byte3 and SIGN_BIT != 0) value = value or (-1L shl 28)
            return value
        }

        val byte4 = byte().toInt() and BYTE_MASK
        if (byte4 !in 0x00..0x0F && byte4 !in 0x70..0x7F) {
            invalidIntegerEncoding(I33_BITS)
        }

        value = value or ((byte4 and 0x1F).toLong() shl 28)
        if (byte4 and 0x10 != 0) value = value or (-1L shl I33_BITS)
        return value
    }

    override fun long(): Long {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            var value = (byte0 and PAYLOAD_MASK).toLong()
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                if (byte0 and SIGN_BIT != 0) value = value or (-1L shl 7)
                return value
            }

            if (contiguousAvailable() >= I64_MAX_BYTES) {
                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK).toLong() shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    if (byte1 and SIGN_BIT != 0) value = value or (-1L shl 14)
                    return value
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                value = value or ((byte2 and PAYLOAD_MASK).toLong() shl 14)
                if (byte2 < CONTINUATION_BIT) {
                    cursor = start + 3
                    if (byte2 and SIGN_BIT != 0) value = value or (-1L shl 21)
                    return value
                }

                val byte3 = buffer[start + 3].toInt() and BYTE_MASK
                value = value or ((byte3 and PAYLOAD_MASK).toLong() shl 21)
                if (byte3 < CONTINUATION_BIT) {
                    cursor = start + 4
                    if (byte3 and SIGN_BIT != 0) value = value or (-1L shl 28)
                    return value
                }

                val byte4 = buffer[start + 4].toInt() and BYTE_MASK
                value = value or ((byte4 and PAYLOAD_MASK).toLong() shl 28)
                if (byte4 < CONTINUATION_BIT) {
                    cursor = start + 5
                    if (byte4 and SIGN_BIT != 0) value = value or (-1L shl 35)
                    return value
                }

                val byte5 = buffer[start + 5].toInt() and BYTE_MASK
                value = value or ((byte5 and PAYLOAD_MASK).toLong() shl 35)
                if (byte5 < CONTINUATION_BIT) {
                    cursor = start + 6
                    if (byte5 and SIGN_BIT != 0) value = value or (-1L shl 42)
                    return value
                }

                val byte6 = buffer[start + 6].toInt() and BYTE_MASK
                value = value or ((byte6 and PAYLOAD_MASK).toLong() shl 42)
                if (byte6 < CONTINUATION_BIT) {
                    cursor = start + 7
                    if (byte6 and SIGN_BIT != 0) value = value or (-1L shl 49)
                    return value
                }

                val byte7 = buffer[start + 7].toInt() and BYTE_MASK
                value = value or ((byte7 and PAYLOAD_MASK).toLong() shl 49)
                if (byte7 < CONTINUATION_BIT) {
                    cursor = start + 8
                    if (byte7 and SIGN_BIT != 0) value = value or (-1L shl 56)
                    return value
                }

                val byte8 = buffer[start + 8].toInt() and BYTE_MASK
                value = value or ((byte8 and PAYLOAD_MASK).toLong() shl 56)
                if (byte8 < CONTINUATION_BIT) {
                    cursor = start + 9
                    if (byte8 and SIGN_BIT != 0) value = value or (-1L shl 63)
                    return value
                }

                val byte9 = buffer[start + 9].toInt() and BYTE_MASK
                cursor = start + I64_MAX_BYTES
                if (byte9 != 0x00 && byte9 != 0x7F) invalidIntegerEncoding(Long.SIZE_BITS)
                return value or ((byte9 and 0x01).toLong() shl 63)
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        var value = (byte0 and PAYLOAD_MASK).toLong()
        if (byte0 < CONTINUATION_BIT) {
            if (byte0 and SIGN_BIT != 0) value = value or (-1L shl 7)
            return value
        }

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK).toLong() shl 7)
        if (byte1 < CONTINUATION_BIT) {
            if (byte1 and SIGN_BIT != 0) value = value or (-1L shl 14)
            return value
        }

        val byte2 = byte().toInt() and BYTE_MASK
        value = value or ((byte2 and PAYLOAD_MASK).toLong() shl 14)
        if (byte2 < CONTINUATION_BIT) {
            if (byte2 and SIGN_BIT != 0) value = value or (-1L shl 21)
            return value
        }

        val byte3 = byte().toInt() and BYTE_MASK
        value = value or ((byte3 and PAYLOAD_MASK).toLong() shl 21)
        if (byte3 < CONTINUATION_BIT) {
            if (byte3 and SIGN_BIT != 0) value = value or (-1L shl 28)
            return value
        }

        val byte4 = byte().toInt() and BYTE_MASK
        value = value or ((byte4 and PAYLOAD_MASK).toLong() shl 28)
        if (byte4 < CONTINUATION_BIT) {
            if (byte4 and SIGN_BIT != 0) value = value or (-1L shl 35)
            return value
        }

        val byte5 = byte().toInt() and BYTE_MASK
        value = value or ((byte5 and PAYLOAD_MASK).toLong() shl 35)
        if (byte5 < CONTINUATION_BIT) {
            if (byte5 and SIGN_BIT != 0) value = value or (-1L shl 42)
            return value
        }

        val byte6 = byte().toInt() and BYTE_MASK
        value = value or ((byte6 and PAYLOAD_MASK).toLong() shl 42)
        if (byte6 < CONTINUATION_BIT) {
            if (byte6 and SIGN_BIT != 0) value = value or (-1L shl 49)
            return value
        }

        val byte7 = byte().toInt() and BYTE_MASK
        value = value or ((byte7 and PAYLOAD_MASK).toLong() shl 49)
        if (byte7 < CONTINUATION_BIT) {
            if (byte7 and SIGN_BIT != 0) value = value or (-1L shl 56)
            return value
        }

        val byte8 = byte().toInt() and BYTE_MASK
        value = value or ((byte8 and PAYLOAD_MASK).toLong() shl 56)
        if (byte8 < CONTINUATION_BIT) {
            if (byte8 and SIGN_BIT != 0) value = value or (-1L shl 63)
            return value
        }

        val byte9 = byte().toInt() and BYTE_MASK
        if (byte9 != 0x00 && byte9 != 0x7F) invalidIntegerEncoding(Long.SIZE_BITS)
        return value or ((byte9 and 0x01).toLong() shl 63)
    }

    override fun ulong(): ULong {
        val start = cursor
        if (contiguousAvailable() > 0) {
            val byte0 = buffer[start].toInt() and BYTE_MASK
            if (byte0 < CONTINUATION_BIT) {
                cursor = start + 1
                return byte0.toULong()
            }

            if (contiguousAvailable() >= U64_MAX_BYTES) {
                var value = (byte0 and PAYLOAD_MASK).toLong()

                val byte1 = buffer[start + 1].toInt() and BYTE_MASK
                value = value or ((byte1 and PAYLOAD_MASK).toLong() shl 7)
                if (byte1 < CONTINUATION_BIT) {
                    cursor = start + 2
                    return value.toULong()
                }

                val byte2 = buffer[start + 2].toInt() and BYTE_MASK
                value = value or ((byte2 and PAYLOAD_MASK).toLong() shl 14)
                if (byte2 < CONTINUATION_BIT) {
                    cursor = start + 3
                    return value.toULong()
                }

                val byte3 = buffer[start + 3].toInt() and BYTE_MASK
                value = value or ((byte3 and PAYLOAD_MASK).toLong() shl 21)
                if (byte3 < CONTINUATION_BIT) {
                    cursor = start + 4
                    return value.toULong()
                }

                val byte4 = buffer[start + 4].toInt() and BYTE_MASK
                value = value or ((byte4 and PAYLOAD_MASK).toLong() shl 28)
                if (byte4 < CONTINUATION_BIT) {
                    cursor = start + 5
                    return value.toULong()
                }

                val byte5 = buffer[start + 5].toInt() and BYTE_MASK
                value = value or ((byte5 and PAYLOAD_MASK).toLong() shl 35)
                if (byte5 < CONTINUATION_BIT) {
                    cursor = start + 6
                    return value.toULong()
                }

                val byte6 = buffer[start + 6].toInt() and BYTE_MASK
                value = value or ((byte6 and PAYLOAD_MASK).toLong() shl 42)
                if (byte6 < CONTINUATION_BIT) {
                    cursor = start + 7
                    return value.toULong()
                }

                val byte7 = buffer[start + 7].toInt() and BYTE_MASK
                value = value or ((byte7 and PAYLOAD_MASK).toLong() shl 49)
                if (byte7 < CONTINUATION_BIT) {
                    cursor = start + 8
                    return value.toULong()
                }

                val byte8 = buffer[start + 8].toInt() and BYTE_MASK
                value = value or ((byte8 and PAYLOAD_MASK).toLong() shl 56)
                if (byte8 < CONTINUATION_BIT) {
                    cursor = start + 9
                    return value.toULong()
                }

                val byte9 = buffer[start + 9].toInt() and BYTE_MASK
                cursor = start + U64_MAX_BYTES
                if (byte9 > 0x01) invalidIntegerEncoding(ULong.SIZE_BITS)
                return (value or (byte9.toLong() shl 63)).toULong()
            }
        }

        val byte0 = byte().toInt() and BYTE_MASK
        if (byte0 < CONTINUATION_BIT) return byte0.toULong()

        var value = (byte0 and PAYLOAD_MASK).toLong()

        val byte1 = byte().toInt() and BYTE_MASK
        value = value or ((byte1 and PAYLOAD_MASK).toLong() shl 7)
        if (byte1 < CONTINUATION_BIT) return value.toULong()

        val byte2 = byte().toInt() and BYTE_MASK
        value = value or ((byte2 and PAYLOAD_MASK).toLong() shl 14)
        if (byte2 < CONTINUATION_BIT) return value.toULong()

        val byte3 = byte().toInt() and BYTE_MASK
        value = value or ((byte3 and PAYLOAD_MASK).toLong() shl 21)
        if (byte3 < CONTINUATION_BIT) return value.toULong()

        val byte4 = byte().toInt() and BYTE_MASK
        value = value or ((byte4 and PAYLOAD_MASK).toLong() shl 28)
        if (byte4 < CONTINUATION_BIT) return value.toULong()

        val byte5 = byte().toInt() and BYTE_MASK
        value = value or ((byte5 and PAYLOAD_MASK).toLong() shl 35)
        if (byte5 < CONTINUATION_BIT) return value.toULong()

        val byte6 = byte().toInt() and BYTE_MASK
        value = value or ((byte6 and PAYLOAD_MASK).toLong() shl 42)
        if (byte6 < CONTINUATION_BIT) return value.toULong()

        val byte7 = byte().toInt() and BYTE_MASK
        value = value or ((byte7 and PAYLOAD_MASK).toLong() shl 49)
        if (byte7 < CONTINUATION_BIT) return value.toULong()

        val byte8 = byte().toInt() and BYTE_MASK
        value = value or ((byte8 and PAYLOAD_MASK).toLong() shl 56)
        if (byte8 < CONTINUATION_BIT) return value.toULong()

        val byte9 = byte().toInt() and BYTE_MASK
        if (byte9 > 0x01) invalidIntegerEncoding(ULong.SIZE_BITS)
        return (value or (byte9.toLong() shl 63)).toULong()
    }
}

@PublishedApi
internal fun invalidIntegerEncoding(bitWidth: Int): Nothing =
    throw WasmDecodeException(ReaderDecodeError.InvalidIntegerEncoding(bitWidth))

@PublishedApi
internal const val BYTE_MASK = 0xFF

@PublishedApi
internal const val PAYLOAD_MASK = 0x7F

@PublishedApi
internal const val CONTINUATION_BIT = 0x80

@PublishedApi
internal const val SIGN_BIT = 0x40

@PublishedApi
internal const val I16_MAX_BYTES = 3

@PublishedApi
internal const val U16_MAX_BYTES = 3

@PublishedApi
internal const val I32_MAX_BYTES = 5

@PublishedApi
internal const val U32_MAX_BYTES = 5

@PublishedApi
internal const val I33_MAX_BYTES = 5

@PublishedApi
internal const val I64_MAX_BYTES = 10

@PublishedApi
internal const val U64_MAX_BYTES = 10

@PublishedApi
internal const val I33_BITS = 33

private const val BYTE_MASK_LONG = 0xFFL

private const val MAX_PRIMITIVE_SIZE = 10

private const val DEFAULT_BUFFER_SIZE = 8 * 1024
