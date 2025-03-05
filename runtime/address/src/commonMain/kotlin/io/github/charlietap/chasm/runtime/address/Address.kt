package io.github.charlietap.chasm.runtime.address

import kotlin.jvm.JvmInline

sealed interface Address {
    val address: Int

    @JvmInline
    value class Function(override val address: Int) : Address

    @JvmInline
    value class Table(override val address: Int) : Address

    @JvmInline
    value class Memory(override val address: Int) : Address

    @JvmInline
    value class Tag(override val address: Int) : Address

    @JvmInline
    value class Global(override val address: Int) : Address

    @JvmInline
    value class Element(override val address: Int) : Address

    @JvmInline
    value class Data(override val address: Int) : Address

    @JvmInline
    value class Exception(override val address: Int) : Address

    @JvmInline
    value class Struct(override val address: Int) : Address

    @JvmInline
    value class Array(override val address: Int) : Address

    @JvmInline
    value class Host(override val address: Int) : Address
}
