package io.github.charlietap.chasm.integration

fun integrationTest(
    script: String,
    block: TestScope.() -> Unit,
) {
    engines.forEach { engine ->
        block(IntegrationTestScope(engine, script))
    }
}
