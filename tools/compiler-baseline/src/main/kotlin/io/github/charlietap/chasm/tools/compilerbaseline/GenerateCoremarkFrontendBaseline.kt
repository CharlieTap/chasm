package io.github.charlietap.chasm.tools.compilerbaseline

fun main() {
    val resourceLoader = CompilerBaselineResourceLoader(
        classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
    )
    System.out.writer().use { output ->
        CirBaselineWriter(ChasmModuleDecoder()).write(
            fixture = CompilerBaselineFixture(
                name = "coremark",
                bytes = resourceLoader.read("compiler-baseline/coremark.wasm"),
            ),
            output = output,
        )
    }
}
