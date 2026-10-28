package io.github.charlietap.chasm.tools.compilerbaseline

fun main() {
    val resourceLoader = CompilerBaselineResourceLoader(
        classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
    )
    val writer = compilerBaselineWriter(
        importResolver = CoremarkImportResolver(ChasmHostFunctionAllocator()),
    )
    System.out.writer().use { output ->
        writer.write(
            fixture = CompilerBaselineFixture(
                name = "coremark",
                bytes = resourceLoader.read("compiler-baseline/coremark.wasm"),
            ),
            output = output,
        )
    }
}
