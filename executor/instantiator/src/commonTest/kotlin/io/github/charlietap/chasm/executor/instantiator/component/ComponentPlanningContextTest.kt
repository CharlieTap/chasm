package io.github.charlietap.chasm.executor.instantiator.component

import io.github.charlietap.chasm.fixture.config.runtimeConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentPlanningContextTest {

    @Test
    fun `host entry policies contain the prepared callee to root path`() {
        val context = ComponentPlanningContext(runtimeConfig())
        val root = context.nextComponentInstance()
        val child = context.nextComponentInstance(root)
        val grandchild = context.nextComponentInstance(child)

        val policy = context.hostEntryPolicy(grandchild)
        val actual = List(policy.enteringInstanceCount) { index -> policy.enteringInstance(index).index }

        assertEquals(listOf(grandchild.index, child.index, root.index), actual)
    }

    @Test
    fun `guest entry policies contain only the callee path below the common ancestor`() {
        val context = ComponentPlanningContext(runtimeConfig())
        val root = context.nextComponentInstance()
        val left = context.nextComponentInstance(root)
        val leftChild = context.nextComponentInstance(left)
        val right = context.nextComponentInstance(root)
        val rightChild = context.nextComponentInstance(right)

        val cases = listOf(
            context.entryPolicy(left, left) to emptyList(),
            context.entryPolicy(left, leftChild) to listOf(leftChild.index),
            context.entryPolicy(leftChild, left) to emptyList(),
            context.entryPolicy(leftChild, rightChild) to listOf(rightChild.index, right.index),
            context.entryPolicy(root, rightChild) to listOf(rightChild.index, right.index),
        )
        val actual = cases.map { (policy, _) ->
            List(policy.enteringInstanceCount) { index -> policy.enteringInstance(index).index }
        }
        val expected = cases.map { (_, expected) -> expected }

        assertEquals(expected, actual)
    }
}
