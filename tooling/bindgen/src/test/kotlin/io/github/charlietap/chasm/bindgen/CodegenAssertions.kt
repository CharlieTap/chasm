package io.github.charlietap.chasm.bindgen

import com.goncalossilva.resources.Resource
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
    val implementationFileName = wasmInterface.interfaceName + "Impl"
    val compiledFileName = wasmInterface.interfaceName + "Compiled"
    val generatedByRuntime = runtimes.associateWith { runtime ->
        val compiled = runtime == CodegenRuntime.CHASM && config.compiledCode != null
        val expectedFileNames = listOfNotNull(
            wasmInterface.interfaceName,
            implementationFileName.takeIf { !compiled },
            compiledFileName.takeIf { compiled },
        )
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
        listOf(implementationFileName, compiledFileName).forEach { name ->
            files[name]?.let { file ->
                assertGolden(
                    path = "codegen/implementation/${runtime.goldenDirectory}/$category/$name.kt.txt",
                    actual = file.toString(),
                )
            }
        }
    }
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
