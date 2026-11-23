package io.github.charlietap.chasm.tools.compilerbaseline

fun main() {
    val resourceLoader = CompilerBaselineResourceLoader(
        classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
    )
    val writer = compilerBaselineWriter(
        importResolver = EmptyCompilerBaselineImportResolver(),
        stores = listOf(CompilerBaselineStore.CHECKPOINTS),
    )
    System.out.writer().use { output ->
        writer.write(
            fixture = CompilerBaselineFixture(
                name = "contrived",
                bytes = resourceLoader.read("compiler-baseline/contrived.wasm"),
            ),
            output = output,
        )
    }
}
