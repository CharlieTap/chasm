package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.internal.RuntimeLoadedCode
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsumerRulesTest {

    @Test
    fun `the storage rule ships in the consumer rules`() {

        val rules = File(CONSUMER_RULES).readLines()

        assertTrue(STORAGE_RULE in rules, "consumer rules are missing the storage rule: $rules")
    }

    @Test
    fun `every rule for runtime-loaded code applies only when runtime-loaded code is used`() {

        val rules = File(CONSUMER_RULES).readLines().filter { line -> line.isNotBlank() }
        val condition = "-if class ${RuntimeLoadedCode::class.java.name}"
        val loadedCodeIndex = rules.indexOf("-keep,allowshrinking class ${RuntimeLoadedCode::class.java.name}")
        val loadedMembersIndex = rules.indexOf("-keepclassmembers class ${RuntimeLoadedCode::class.java.name} { *; }")

        assertTrue(loadedCodeIndex >= 0, "consumer rules do not keep ${RuntimeLoadedCode::class.java.name}: $rules")
        assertEquals(loadedCodeIndex + 1, loadedMembersIndex, "consumer rules do not keep the members of ${RuntimeLoadedCode::class.java.name}")

        val unconditional = rules.take(loadedCodeIndex)
        val conditional = rules.drop(loadedMembersIndex + 1)

        assertEquals(listOf(STORAGE_RULE, MANAGED_ENTRY_RULE), unconditional, "unexpected unconditional rules: $unconditional")
        assertTrue(conditional.isNotEmpty(), "consumer rules have no rules for runtime-loaded code: $rules")
        conditional.chunked(2).forEach { pair ->
            assertEquals(condition, pair.first(), "rule without its condition: ${pair.last()}")
            assertTrue(pair.size == 2 && pair.last().startsWith("-keep"), "condition without a rule: $pair")
        }
    }

    private companion object {
        const val CONSUMER_RULES = "consumer-rules.pro"
        const val STORAGE_RULE = "-keep,allowobfuscation,allowshrinking class io.github.charlietap.chasm.runtime.compiled.gc.storage.*"
        const val MANAGED_ENTRY_RULE = "-keep,allowobfuscation,allowshrinking class io.github.charlietap.chasm.runtime.compiled.gc.ManagedEntry { *** enter(...); }"
    }
}
