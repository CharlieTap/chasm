package io.github.charlietap.chasm.integration

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

fun integrationTest(
    script: String,
    exclusions: Map<String, Set<String>> = EXCLUSIONS,
    block: suspend TestScope.() -> Unit,
): TestResult {
    return runTest {
        engines
            .filterNot { engine -> script in exclusions[engine.name()].orEmpty() }
            .forEach { engine ->
                block(IntegrationTestScope(engine, script))
            }
    }
}
