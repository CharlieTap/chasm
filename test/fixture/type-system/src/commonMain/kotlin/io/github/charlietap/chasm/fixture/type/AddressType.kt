package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.AddressType

fun addressType(): AddressType = i32AddressType()

fun i32AddressType() = AddressType.I32

fun i64AddressType() = AddressType.I64
