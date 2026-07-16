package io.github.charlietap.chasm.fixture.runtime.component.address

import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.HostResourceHandleId
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress

fun runtimeResourceTypeAddress(address: Int = 0) = RuntimeResourceTypeAddress(address)

fun hostResourceHandleId(id: ULong = 1uL) = HostResourceHandleId(id)

fun componentCallToken(token: ULong = 1uL) = ComponentCallToken(token)
