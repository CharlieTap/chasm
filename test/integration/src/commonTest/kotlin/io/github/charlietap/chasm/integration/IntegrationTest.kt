package io.github.charlietap.chasm.integration

fun integrationTest(
    script: String,
    exclusions: Map<String, Set<String>> = EXCLUSIONS,
    block: TestScope.() -> Unit,
) {
    engines
        .filterNot { engine -> script in exclusions[engine.name()].orEmpty() }
        .forEach { engine ->
            block(IntegrationTestScope(engine, script))
        }
}
