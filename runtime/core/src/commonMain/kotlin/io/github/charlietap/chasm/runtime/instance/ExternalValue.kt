package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import kotlin.jvm.JvmInline

sealed interface ExternalValue {

    @JvmInline
    value class Function(val address: Address.Function) : ExternalValue

    @JvmInline
    value class Table(val address: Address.Table) : ExternalValue

    @JvmInline
    value class Memory(val address: Address.Memory) : ExternalValue

    @JvmInline
    value class Tag(val address: Address.Tag) : ExternalValue

    @JvmInline
    value class Global(val address: Address.Global) : ExternalValue
}
