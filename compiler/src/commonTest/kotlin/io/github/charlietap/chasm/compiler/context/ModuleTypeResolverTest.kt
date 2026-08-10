package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.module.global
import io.github.charlietap.chasm.fixture.ast.module.globalImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.import
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tag
import io.github.charlietap.chasm.fixture.ast.module.tagImportDescriptor
import io.github.charlietap.chasm.fixture.type.concreteDefinedTypeHeapType
import io.github.charlietap.chasm.fixture.type.concreteTypeIndexHeapType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModuleTypeResolverTest {

    @Test
    fun resolvesDefinedTypesByModuleIndex() {
        val definedType = definedType()
        val resolver = ModuleTypeResolver(
            module = module(definedTypes = listOf(definedType)),
        )

        assertEquals(definedType, resolver.definedType(Index.TypeIndex(0u)))
    }

    @Test
    fun substitutesTypeIndicesInReferenceTypes() {
        val definedType = definedType()
        val resolver = ModuleTypeResolver(
            module = module(definedTypes = listOf(definedType)),
        )

        val resolved = resolver.resolve(
            refNullReferenceType(
                heapType = concreteTypeIndexHeapType(0),
            ),
        )

        assertEquals(
            refNullReferenceType(
                heapType = concreteDefinedTypeHeapType(definedType),
            ),
            resolved,
        )
    }

    @Test
    fun rejectsIndicesOutsideTheRuntimeRange() {
        assertFailsWith<IllegalStateException> {
            Index.TypeIndex(UInt.MAX_VALUE).toInt()
        }
    }

    @Test
    fun substitutesTypeIndicesAcrossModuleTypes() {
        val definedType = definedType()
        val indexedReference = refNullReferenceType(
            heapType = concreteTypeIndexHeapType(0),
        )
        val tableType = tableType(referenceType = indexedReference)
        val globalType = globalType(valueType = referenceValueType(indexedReference))
        val tagType = tagType(
            functionType = functionType(
                params = resultType(listOf(referenceValueType(indexedReference))),
            ),
        )
        val module = module(
            definedTypes = listOf(definedType),
            imports = listOf(
                import(descriptor = tableImportDescriptor(tableType)),
                import(descriptor = globalImportDescriptor(globalType)),
                import(descriptor = tagImportDescriptor(tagType)),
            ),
            tables = listOf(table(type = tableType)),
            tags = listOf(tag(type = tagType)),
            globals = listOf(global(type = globalType)),
            elementSegments = listOf(elementSegment(type = indexedReference)),
        )
        val resolver = ModuleTypeResolver(module)
        val resolvedReference = refNullReferenceType(
            heapType = concreteDefinedTypeHeapType(definedType),
        )

        assertEquals(tableType(referenceType = resolvedReference), resolver.resolve(tableType))
        assertEquals(
            globalType(valueType = referenceValueType(resolvedReference)),
            resolver.resolve(globalType),
        )
        assertEquals(
            tagType(
                functionType = functionType(
                    params = resultType(listOf(referenceValueType(resolvedReference))),
                ),
            ),
            resolver.resolve(tagType),
        )
        assertEquals(resolvedReference, resolver.resolve(indexedReference))
    }
}
