package io.github.charlietap.chasm.bindgen

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.bindgen.fixture.NeverLogger
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlin.test.assertEquals

internal fun assertGenerates(
    category: String,
    wasmInterface: WasmInterface,
    interfaceVisibility: InterfaceVisibility = InterfaceVisibility.PUBLIC,
    factoryVisibility: FactoryVisibility = FactoryVisibility.PUBLIC,
    implementationVisibility: ImplementationVisibility = ImplementationVisibility.PRIVATE,
    config: CodegenConfig = CodegenConfig(),
    runtimes: Iterable<CodegenRuntime> = CodegenRuntime.entries,
) {
    val expectedFileNames = listOf(
        wasmInterface.interfaceName,
        wasmInterface.interfaceName + "Impl",
    )
    val generatedByRuntime = runtimes.associateWith { runtime ->
        WasmInterfaceGenerator()(
            interfaceVisibility = interfaceVisibility,
            factoryVisibility = factoryVisibility,
            implementationVisibility = implementationVisibility,
            wasmInterface = wasmInterface,
            config = config.copy(runtime = runtime),
        ).also { generated ->
            assertEquals(expectedFileNames, generated.map { file -> file.name })
        }.associateBy { file -> file.name }
    }

    val interfacesByRuntime = generatedByRuntime.mapValues { (_, files) ->
        files.getValue(wasmInterface.interfaceName).toString()
    }
    assertEquals(
        1,
        interfacesByRuntime.values.toSet().size,
        "Generated interface differs by runtime: ${interfacesByRuntime.keys}",
    )
    assertGolden(
        path = "codegen/interface/$category/${wasmInterface.interfaceName}.kt.txt",
        actual = interfacesByRuntime.values.first(),
    )

    generatedByRuntime.forEach { (runtime, files) ->
        assertGolden(
            path = "codegen/implementation/${runtime.goldenDirectory}/$category/${wasmInterface.interfaceName}Impl.kt.txt",
            actual = files.getValue(wasmInterface.interfaceName + "Impl").toString(),
        )
    }
}

internal fun assertModuleGenerates(
    category: String,
    module: String,
    options: BindingOptions,
    logger: BindgenLogger = NeverLogger,
) {

    val info = moduleInfo(moduleBytes(module)).expect("$module should decode")
    val files = BindingGenerator(logger).generate(info, options).associateBy { file -> file.name }

    assertGolden(
        path = "codegen/interface/$category/${options.interfaceName}.kt.txt",
        actual = files.getValue(options.interfaceName).toString(),
    )
    assertGolden(
        path = "codegen/implementation/${options.config.runtime.goldenDirectory}/$category/${options.interfaceName}Impl.kt.txt",
        actual = files.getValue(options.interfaceName + "Impl").toString(),
    )
}

internal fun moduleBytes(module: String): ByteArray {
    return checkNotNull(BindingGenerator::class.java.getResourceAsStream("/modules/$module.wasm")) { "missing module $module" }
        .use { stream -> stream.readBytes() }
}

private val CodegenRuntime.goldenDirectory: String
    get() = when (this) {
        CodegenRuntime.PORTABLE_VM -> "vm"
        CodegenRuntime.CHASM -> "chasm"
    }

private fun assertGolden(path: String, actual: String) {
    assertEquals(
        Resource(path).readText(),
        actual,
        "Generated source did not match $path",
    )
}
