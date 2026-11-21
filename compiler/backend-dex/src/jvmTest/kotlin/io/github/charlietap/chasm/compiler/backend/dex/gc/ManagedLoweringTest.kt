package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.decodeModule
import io.github.charlietap.chasm.compiler.backend.dex.fixture
import io.github.charlietap.chasm.compiler.backend.dex.generate
import kotlin.test.Test
import kotlin.test.assertNotNull

class ManagedLoweringTest {

    @Test
    fun `a gc module lowers with managed references`() {

        val dex = generate(decodeModule(fixture("gc")), DexOptions(stacktraces = true))

        assertNotNull(dex.shapeDex)
    }
}
