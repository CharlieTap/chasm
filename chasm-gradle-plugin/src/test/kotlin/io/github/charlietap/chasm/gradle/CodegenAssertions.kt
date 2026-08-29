package io.github.charlietap.chasm.gradle

import com.goncalossilva.resources.Resource
import kotlin.test.assertEquals

internal fun assertGenerates(
    category: String,
    wasmInterface: WasmInterface,
    interfaceVisibility: TypeVisibility = TypeVisibility.PUBLIC,
    implementationVisibility: TypeVisibility = TypeVisibility.INTERNAL,
    config: CodegenConfig = CodegenConfig(),
) {
    val generated = WasmInterfaceGenerator()(
        interfaceVisibility = interfaceVisibility,
        implementationVisibility = implementationVisibility,
        wasmInterface = wasmInterface,
        config = config,
    )
    val expectedFileNames = listOf(
        wasmInterface.interfaceName,
        wasmInterface.interfaceName + "Impl",
    )
    assertEquals(expectedFileNames, generated.map { file -> file.name })

    generated.forEach { file ->
        val resourcePath = "codegen/$category/${file.name}.kt.txt"
        assertEquals(
            Resource(resourcePath).readText(),
            file.toString(),
            "Generated source did not match $resourcePath",
        )
    }
}
