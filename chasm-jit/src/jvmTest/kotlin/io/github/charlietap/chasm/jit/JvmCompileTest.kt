@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFailsWith

class JvmCompileTest {

    @Test
    fun aCancelledCompileStops() {
        val store = store()
        val module = runBlocking { module(resourceBytes("modules/loops.wasm"), validate = true) }.expect("module")
        val cancelled = Job().apply { cancel() }

        assertFailsWith<CancellationException> { runBlocking(cancelled) { instance(store, module, emptyList()) } }
    }
}
