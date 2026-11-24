package io.github.charlietap.chasm.aot

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ConsumerRulesTest {

    @Test
    fun `the storage rule ships in the consumer rules`() {

        val rules = File(CONSUMER_RULES).readLines()

        assertTrue(STORAGE_RULE in rules, "consumer rules are missing the storage rule: $rules")
    }

    private companion object {
        const val CONSUMER_RULES = "consumer-rules.pro"
        const val STORAGE_RULE = "-keep,allowobfuscation,allowshrinking class io.github.charlietap.chasm.runtime.compiled.gc.storage.*"
    }
}
