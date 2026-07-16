package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.embedding.componentResourceType
import io.github.charlietap.chasm.embedding.dropResource
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.resource
import io.github.charlietap.chasm.embedding.resourceValue
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.ComponentImport
import io.github.charlietap.chasm.embedding.shapes.ComponentImportable
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceType
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

internal fun ComponentSpectestImports(store: Store): List<ComponentImport> {
    val resources = ComponentSpectestResourceState()
    val resource1 = componentResourceType(store) { value ->
        resources.drops += 1u
        resources.lastDrop = value as UInt
    }
    val resource2 = componentResourceType(store) {}

    return listOf(
        ComponentImport(
            "host-echo-u32",
            ComponentImportable.Function { arguments ->
                listOf(arguments.u32(0))
            },
        ),
        ComponentImport(
            "host-return-two",
            ComponentImportable.Function { listOf(ComponentValue.U32(2u)) },
        ),
        ComponentImport(
            "host",
            ComponentImportable.Instance(
                listOf(
                    ComponentImport(
                        "return-three",
                        ComponentImportable.Function { listOf(ComponentValue.U32(3u)) },
                    ),
                    ComponentImport(
                        "nested",
                        ComponentImportable.Instance(
                            listOf(
                                ComponentImport(
                                    "return-four",
                                    ComponentImportable.Function { listOf(ComponentValue.U32(4u)) },
                                ),
                            ),
                        ),
                    ),
                    ComponentImport("simple-module", spectestModule()),
                    ComponentImport("resource1", ComponentImportable.ResourceType(resource1)),
                    ComponentImport("resource2", ComponentImportable.ResourceType(resource2)),
                    ComponentImport("resource1-again", ComponentImportable.ResourceType(resource1)),
                    ComponentImport(
                        "[constructor]resource1",
                        ComponentImportable.Function { arguments ->
                            listOf(resource(resource1, arguments.u32(0).value))
                        },
                    ),
                    ComponentImport(
                        "[static]resource1.assert",
                        ComponentImportable.Function { arguments ->
                            val own = arguments.own(0)
                            requireResourceValue(resource1, own, arguments.u32(1).value)
                            drop(own)
                            emptyList()
                        },
                    ),
                    ComponentImport(
                        "[static]resource1.last-drop",
                        ComponentImportable.Function { listOf(ComponentValue.U32(resources.lastDrop)) },
                    ),
                    ComponentImport(
                        "[static]resource1.drops",
                        ComponentImportable.Function { listOf(ComponentValue.U32(resources.drops)) },
                    ),
                    ComponentImport(
                        "[method]resource1.simple",
                        ComponentImportable.Function { arguments ->
                            requireResourceValue(resource1, arguments.resource(0), arguments.u32(1).value)
                            emptyList()
                        },
                    ),
                    ComponentImport(
                        "[method]resource1.take-borrow",
                        ComponentImportable.Function { arguments ->
                            arguments.borrow(0)
                            arguments.borrow(1)
                            emptyList()
                        },
                    ),
                    ComponentImport(
                        "[method]resource1.take-own",
                        ComponentImportable.Function { arguments ->
                            arguments.borrow(0)
                            val own = arguments.own(1)
                            drop(own)
                            emptyList()
                        },
                    ),
                    ComponentImport(
                        "return-hi",
                        ComponentImportable.Function { listOf(ComponentValue.StringValue("hi")) },
                    ),
                ),
            ),
        ),
    )
}

private fun spectestModule() = module(SPECTEST_MODULE).expect("failed to decode the component spectest module")

private fun List<ComponentValue>.u32(index: Int): ComponentValue.U32 =
    getOrNull(index) as? ComponentValue.U32 ?: invalidArgument(index, "u32")

private fun List<ComponentValue>.resource(index: Int): ComponentValue.Resource =
    getOrNull(index) as? ComponentValue.Resource ?: invalidArgument(index, "resource")

private fun List<ComponentValue>.own(index: Int): ComponentValue.Resource.Own =
    getOrNull(index) as? ComponentValue.Resource.Own ?: invalidArgument(index, "own resource")

private fun List<ComponentValue>.borrow(index: Int): ComponentValue.Resource.Borrow =
    getOrNull(index) as? ComponentValue.Resource.Borrow ?: invalidArgument(index, "borrowed resource")

private fun ComponentHostFunctionContext.requireResourceValue(
    type: ComponentResourceType,
    resource: ComponentValue.Resource,
    expected: UInt,
) {
    val actual = resourceValue(type, resource) as? UInt
    if (actual != expected) throw HostFunctionException("expected resource representation $expected, found $actual")
}

private fun ComponentHostFunctionContext.drop(resource: ComponentValue.Resource.Own) {
    dropResource(store, resource).expect("failed to drop a component spectest resource")
}

private fun invalidArgument(index: Int, type: String): Nothing =
    throw HostFunctionException("component spectest argument $index is not a $type")

private class ComponentSpectestResourceState(
    var drops: UInt = 0u,
    var lastDrop: UInt = 0u,
)

private val SPECTEST_MODULE = byteArrayOf(
    0x00,
    0x61,
    0x73,
    0x6d,
    0x01,
    0x00,
    0x00,
    0x00,
    0x01,
    0x05,
    0x01,
    0x60,
    0x00,
    0x01,
    0x7f,
    0x03,
    0x02,
    0x01,
    0x00,
    0x06,
    0x07,
    0x01,
    0x7f,
    0x00,
    0x41,
    0xe4.toByte(),
    0x00,
    0x0b,
    0x07,
    0x09,
    0x02,
    0x01,
    0x67,
    0x03,
    0x00,
    0x01,
    0x66,
    0x00,
    0x00,
    0x0a,
    0x07,
    0x01,
    0x05,
    0x00,
    0x41,
    0xe5.toByte(),
    0x00,
    0x0b,
)
