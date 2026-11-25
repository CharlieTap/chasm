package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.fixture.Fixture

val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)

fun fixture(): Fixture {
    return Fixture(
        name = "empty",
        path = "empty.wasm",
        version = "1.0",
    )
}
