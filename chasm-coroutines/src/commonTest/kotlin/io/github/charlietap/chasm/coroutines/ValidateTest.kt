package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ValidateTest {

    @Test
    fun `validates a module through the coroutine API`() = runTest {
        val module = module(EMPTY_MODULE).expect("expected module to decode")

        validate(module).expect("expected module to validate")
    }

    private companion object {
        val EMPTY_MODULE = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
        )
    }
}
