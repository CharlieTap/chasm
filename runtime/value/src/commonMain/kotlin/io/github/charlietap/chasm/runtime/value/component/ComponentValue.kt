package io.github.charlietap.chasm.runtime.value.component

import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.HostResourceHandleId
import io.github.charlietap.chasm.runtime.address.StoreIdentity
import kotlin.jvm.JvmInline

sealed interface ComponentValue {

    @JvmInline
    value class Bool(val value: Boolean) : ComponentValue

    @JvmInline
    value class S8(val value: Byte) : ComponentValue

    @JvmInline
    value class U8(val value: UByte) : ComponentValue

    @JvmInline
    value class S16(val value: Short) : ComponentValue

    @JvmInline
    value class U16(val value: UShort) : ComponentValue

    @JvmInline
    value class S32(val value: Int) : ComponentValue

    @JvmInline
    value class U32(val value: UInt) : ComponentValue

    @JvmInline
    value class S64(val value: Long) : ComponentValue

    @JvmInline
    value class U64(val value: ULong) : ComponentValue

    @JvmInline
    value class F32(val value: Float) : ComponentValue

    @JvmInline
    value class F64(val value: Double) : ComponentValue

    @JvmInline
    value class Char(val codePoint: UInt) : ComponentValue {
        init {
            require(codePoint <= MAX_UNICODE_CODE_POINT && codePoint !in SURROGATE_RANGE)
        }
    }

    @JvmInline
    value class StringValue(val value: String) : ComponentValue

    data class Record(val fields: List<ComponentValue>) : ComponentValue

    data class Variant(
        val caseIndex: Int,
        val value: ComponentValue? = null,
    ) : ComponentValue

    data class ListValue(val elements: List<ComponentValue>) : ComponentValue

    class ByteList(val bytes: ByteArray) : ComponentValue {

        override fun equals(other: Any?): Boolean =
            other is ByteList && bytes.contentEquals(other.bytes)

        override fun hashCode(): Int = bytes.contentHashCode()

        override fun toString(): String = "ByteList(bytes=${bytes.contentToString()})"
    }

    data class Tuple(val elements: List<ComponentValue>) : ComponentValue

    @JvmInline
    value class Flags(val bits: UInt) : ComponentValue

    @JvmInline
    value class Enum(val caseIndex: Int) : ComponentValue

    sealed interface Resource : ComponentValue {
        val store: StoreIdentity
        val handle: HostResourceHandleId

        data class Own(
            override val store: StoreIdentity,
            override val handle: HostResourceHandleId,
        ) : Resource

        data class Borrow(
            override val store: StoreIdentity,
            override val handle: HostResourceHandleId,
            val callToken: ComponentCallToken,
        ) : Resource
    }

    sealed interface Option : ComponentValue {
        data object None : Option

        data class Some(val value: ComponentValue) : Option
    }

    sealed interface Result : ComponentValue {
        data class Ok(val value: ComponentValue? = null) : Result

        data class Error(val value: ComponentValue? = null) : Result
    }
}

private const val MAX_UNICODE_CODE_POINT = 0x10ffffu
private val SURROGATE_RANGE = 0xd800u..0xdfffu
