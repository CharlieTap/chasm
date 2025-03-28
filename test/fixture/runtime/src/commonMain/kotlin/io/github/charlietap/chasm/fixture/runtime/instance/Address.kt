package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address

fun functionAddress(
    address: Int = 0,
): Address.Function = Address.Function(address)

fun tableAddress(
    address: Int = 0,
): Address.Table = Address.Table(address)

fun memoryAddress(
    address: Int = 0,
): Address.Memory = Address.Memory(address)

fun tagAddress(
    address: Int = 0,
): Address.Tag = Address.Tag(address)

fun globalAddress(
    address: Int = 0,
): Address.Global = Address.Global(address)

fun elementAddress(
    address: Int = 0,
): Address.Element = Address.Element(address)

fun dataAddress(
    address: Int = 0,
): Address.Data = Address.Data(address)

fun hostAddress(
    address: Int = 0,
): Address.Host = Address.Host(address)

fun structAddress(
    address: Int = 0,
): Address.Struct = Address.Struct(address)

fun arrayAddress(
    address: Int = 0,
): Address.Array = Address.Array(address)
