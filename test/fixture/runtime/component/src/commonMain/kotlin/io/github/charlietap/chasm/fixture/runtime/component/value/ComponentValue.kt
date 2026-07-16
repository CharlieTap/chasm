package io.github.charlietap.chasm.fixture.runtime.component.value

import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.HostResourceHandleId
import io.github.charlietap.chasm.runtime.address.StoreIdentity
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

fun flagsComponentValue(
    bits: UInt = 0u,
): ComponentValue.Flags = ComponentValue.Flags(bits)

fun u32ComponentValue(
    value: UInt = 0u,
): ComponentValue.U32 = ComponentValue.U32(value)

fun byteListComponentValue(
    bytes: ByteArray = byteArrayOf(),
): ComponentValue.ByteList = ComponentValue.ByteList(bytes)

fun ownComponentResourceValue(
    store: StoreIdentity,
    handle: HostResourceHandleId = HostResourceHandleId(1uL),
): ComponentValue.Resource.Own = ComponentValue.Resource.Own(
    store = store,
    handle = handle,
)

fun borrowComponentResourceValue(
    store: StoreIdentity,
    handle: HostResourceHandleId = HostResourceHandleId(1uL),
    callToken: ComponentCallToken = ComponentCallToken(1uL),
): ComponentValue.Resource.Borrow = ComponentValue.Resource.Borrow(
    store = store,
    handle = handle,
    callToken = callToken,
)
