package io.github.charlietap.chasm.runtime.type

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RuntimeTypeRegistryTest {

    @Test
    fun `equivalent groups share runtime type ids`() {
        val registry = RuntimeTypeRegistry()

        val first = registry.register(typeChain(4))
        val second = registry.register(typeChain(4))

        repeat(first.size) { index ->
            assertEquals(first[index], second[index])
        }
    }

    @Test
    fun `equivalent recursive groups share ids member by member`() {
        val registry = RuntimeTypeRegistry()

        val first = registry.register(recursivePair(1, 0))
        val second = registry.register(recursivePair(1, 0))
        val different = registry.register(recursivePair(0, 1))

        assertEquals(first[0], second[0])
        assertEquals(first[1], second[1])
        assertFalse(registry.matches(first[0], different[0]))
        assertFalse(registry.matches(first[1], different[1]))
    }

    @Test
    fun `different groups have different runtime type ids`() {
        val registry = RuntimeTypeRegistry()

        val noParams = registry.register(typeChain(1))
        val oneParam = registry.register(typeChain(1, paramCount = 1))

        assertFalse(registry.matches(noParams[0], oneParam[0]))
    }

    @Test
    fun `subtype display distinguishes ancestors from siblings`() {
        val registry = RuntimeTypeRegistry()
        val types = registry.register(branchingTypes())

        assertTrue(registry.matches(types[3], types[0]))
        assertTrue(registry.matches(types[3], types[1]))
        assertTrue(registry.matches(types[3], types[3]))
        assertFalse(registry.matches(types[3], types[2]))
        assertFalse(registry.matches(types[0], types[3]))
    }

    @Test
    fun `standalone host signature canonicalizes references to module types`() {
        val registry = RuntimeTypeRegistry()
        val firstModuleType = typeChain(1).single()
        val secondModuleType = typeChain(1).single()

        val first = registry.register(hostSignature(firstModuleType))
        val second = registry.register(hostSignature(secondModuleType))

        assertEquals(first, second)
    }

    @Test
    fun `canonical keys do not retain mutable source graphs`() {
        val registry = RuntimeTypeRegistry()
        val mutableTypes = typeChain(1)
        val originalId = registry.register(mutableTypes)[0]

        mutableTypes.single().recursiveType.subTypes = recursiveType(parent = null, paramCount = 1).subTypes
        val changedId = registry.register(mutableTypes)[0]
        val equivalentOriginalId = registry.register(typeChain(1))[0]

        assertFalse(registry.matches(changedId, originalId))
        assertEquals(originalId, equivalentOriginalId)
    }

    @Test
    fun `failed group registration does not publish partial ids`() {
        val registry = RuntimeTypeRegistry()
        val invalid = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        subtype(parent = null),
                        subtype(parent = 1),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )

        assertFailsWith<IllegalStateException> { registry.register(invalid) }

        assertEquals(RTT(0), registry.register(typeChain(1))[0])
    }

    @Test
    fun `canonical key equality resolves hash collisions exactly`() {
        val first = StoredRecGroupKey(intArrayOf(0, 31))
        val second = StoredRecGroupKey(intArrayOf(1, 0))

        assertEquals(first.hashCode(), second.hashCode())
        assertNotEquals(first, second)
    }

    private companion object {
        fun typeChain(
            depth: Int,
            paramCount: Int = 0,
        ) = DefinedTypeFactory(
            List(depth) { index ->
                recursiveType(
                    parent = (index - 1).takeIf { it >= 0 },
                    paramCount = paramCount,
                )
            },
        )

        fun branchingTypes() = DefinedTypeFactory(
            listOf(
                recursiveType(parent = null),
                recursiveType(parent = 0),
                recursiveType(parent = 0, paramCount = 1),
                recursiveType(parent = 1),
            ),
        )

        fun hostSignature(parameterType: DefinedType) = FunctionType(
            params = ResultType(
                listOf(ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(parameterType)))),
            ),
            results = ResultType(emptyList()),
        ).definedType()

        fun recursivePair(
            firstTarget: Int,
            secondTarget: Int,
        ) = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        recursiveStruct(firstTarget),
                        recursiveStruct(secondTarget),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )

        fun recursiveStruct(target: Int) = SubType.Final(
            superTypes = emptyList(),
            compositeType = CompositeType.Struct(
                StructType(
                    listOf(
                        FieldType(
                            StorageType.Value(
                                ValueType.Reference(
                                    ReferenceType.RefNull(ConcreteHeapType.TypeIndex(target)),
                                ),
                            ),
                            Mutability.Const,
                        ),
                    ),
                ),
            ),
        )

        fun recursiveType(
            parent: Int?,
            paramCount: Int = 0,
        ) = RecursiveType(
            subTypes = listOf(
                subtype(parent, paramCount),
            ),
            state = RecursiveType.State.SYNTAX,
        )

        fun subtype(
            parent: Int?,
            paramCount: Int = 0,
        ) = SubType.Open(
            superTypes = parent?.let { listOf(ConcreteHeapType.TypeIndex(it)) }.orEmpty(),
            compositeType = CompositeType.Function(
                FunctionType(
                    params = ResultType(List(paramCount) { ValueType.Number(NumberType.I32) }),
                    results = ResultType(emptyList()),
                ),
            ),
        )
    }
}
