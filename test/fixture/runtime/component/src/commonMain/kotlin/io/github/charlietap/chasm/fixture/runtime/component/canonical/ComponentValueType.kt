package io.github.charlietap.chasm.fixture.runtime.component.canonical

import io.github.charlietap.chasm.fixture.type.component.componentResourceTypeId
import io.github.charlietap.chasm.fixture.type.component.componentTypeId
import io.github.charlietap.chasm.fixture.type.component.componentVariantCase
import io.github.charlietap.chasm.fixture.type.component.definedComponentValueType
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.type.component.ComponentDefinedValueType
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.chasm.type.component.ComponentVariantCase
import io.github.charlietap.chasm.type.component.LabeledComponentValueType

fun recordComponentValueType(
    id: UInt = 0u,
    fields: List<LabeledComponentValueType> = emptyList(),
) = definedComponentValueType(
    type = ComponentDefinedValueType.Record(fields),
    id = componentTypeId(id),
)

fun tupleComponentValueType(
    id: UInt = 0u,
    elements: List<ComponentValueType> = emptyList(),
) = definedComponentValueType(
    type = ComponentDefinedValueType.Tuple(elements),
    id = componentTypeId(id),
)

fun variantComponentValueType(
    id: UInt = 0u,
    cases: List<ComponentVariantCase> = emptyList(),
) = definedComponentValueType(
    type = ComponentDefinedValueType.Variant(cases),
    id = componentTypeId(id),
)

fun nullaryVariantComponentValueType(
    id: UInt = 0u,
    cases: Int = 1,
) = variantComponentValueType(
    id = id,
    cases = List(cases) { index -> componentVariantCase("case-$index") },
)

fun optionComponentValueType(
    id: UInt = 0u,
    value: ComponentValueType = primitiveComponentValueType(),
) = definedComponentValueType(
    type = ComponentDefinedValueType.Option(value),
    id = componentTypeId(id),
)

fun resultComponentValueType(
    id: UInt = 0u,
    ok: ComponentValueType? = null,
    error: ComponentValueType? = null,
) = definedComponentValueType(
    type = ComponentDefinedValueType.Result(ok, error),
    id = componentTypeId(id),
)

fun flagsComponentValueType(
    id: UInt = 0u,
    labels: List<String> = listOf("flag"),
) = definedComponentValueType(
    type = ComponentDefinedValueType.Flags(labels),
    id = componentTypeId(id),
)

fun enumComponentValueType(
    id: UInt = 0u,
    labels: List<String> = listOf("case"),
) = definedComponentValueType(
    type = ComponentDefinedValueType.Enum(labels),
    id = componentTypeId(id),
)

fun ownComponentValueType(
    id: UInt = 0u,
    typeId: UInt = 0u,
    resourceId: UInt = 0u,
) = definedComponentValueType(
    type = ComponentDefinedValueType.Own(
        id = componentTypeId(typeId),
        resource = componentResourceTypeId(resourceId),
    ),
    id = componentTypeId(id),
)

fun borrowComponentValueType(
    id: UInt = 0u,
    typeId: UInt = 0u,
    resourceId: UInt = 0u,
) = definedComponentValueType(
    type = ComponentDefinedValueType.Borrow(
        id = componentTypeId(typeId),
        resource = componentResourceTypeId(resourceId),
    ),
    id = componentTypeId(id),
)

fun listComponentValueType(
    id: UInt = 0u,
    element: ComponentValueType = primitiveComponentValueType(),
) = definedComponentValueType(
    type = ComponentDefinedValueType.ListValue(element),
    id = componentTypeId(id),
)
