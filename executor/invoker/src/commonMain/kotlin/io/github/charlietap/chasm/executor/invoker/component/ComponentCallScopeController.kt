package io.github.charlietap.chasm.executor.invoker.component

import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.function.ComponentEntryPolicy
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceStates
import io.github.charlietap.chasm.runtime.component.store.ComponentCallScope
import io.github.charlietap.chasm.runtime.component.store.ComponentStore

internal fun enterComponentInstance(
    states: ComponentInstanceStates,
    policy: ComponentEntryPolicy,
    scope: ComponentCallScope,
) {
    repeat(policy.enteringInstanceCount) { index ->
        val instance = policy.enteringInstance(index)
        if (!states.mayEnter[instance.index] || states.poisoned[instance.index]) {
            throw ComponentCallScopeException(ComponentInvocationError.CannotEnterComponentInstance)
        }
    }

    repeat(policy.enteringInstanceCount) { index ->
        val instance = policy.enteringInstance(index)
        states.mayEnter[instance.index] = false
        scope.recordEnteredInstance(states, instance)
    }
}

internal fun enterComponentInstance(
    states: ComponentInstanceStates,
    caller: RuntimeComponentInstanceIndex?,
    callee: RuntimeComponentInstanceIndex,
    scope: ComponentCallScope,
) {
    val ancestor = commonAncestor(states, caller?.index ?: ABSENT_INSTANCE, callee.index)
    var candidate = callee.index
    while (candidate != ancestor) {
        if (!states.mayEnter[candidate] || states.poisoned[candidate]) {
            throw ComponentCallScopeException(ComponentInvocationError.CannotEnterComponentInstance)
        }
        candidate = states.parents[candidate]
    }

    candidate = callee.index
    while (candidate != ancestor) {
        states.mayEnter[candidate] = false
        scope.recordEnteredInstance(states, RuntimeComponentInstanceIndex(candidate))
        candidate = states.parents[candidate]
    }
}

internal fun exitComponentCall(
    componentStore: ComponentStore,
    scope: ComponentCallScope,
): ComponentInvocationError? {
    val leakedBorrow = scope.guestBorrowCount != 0

    repeat(scope.guestBorrowJournalCount) { index ->
        scope.guestBorrowTable(index).removeBorrowIfPresent(scope.guestBorrowHandle(index), scope.callToken)
    }
    repeat(scope.hostBorrowCount) { index ->
        componentStore.hostResourceHandles.removeBorrow(scope.hostBorrowHandle(index), scope.callToken)
    }
    for (index in scope.guestLenderCount - 1 downTo 0) {
        scope.guestLenderTable(index).undoLend(scope.guestLenderHandle(index))
    }
    for (index in scope.hostLenderCount - 1 downTo 0) {
        componentStore.hostResourceHandles.undoLend(scope.hostLenderHandle(index))
    }
    for (index in scope.enteredInstanceCount - 1 downTo 0) {
        val instance = scope.enteredInstance(index)
        scope.enteredInstanceStates(index).mayEnter[instance.index] = true
    }

    scope.clearJournals()
    componentStore.exitCall()
    return if (leakedBorrow) {
        ComponentInvocationError.InvalidCanonicalValue("borrow handles remain at the end of the call")
    } else {
        null
    }
}

internal class ComponentCallScopeException(
    val error: ComponentInvocationError,
) : RuntimeException()

private fun commonAncestor(
    states: ComponentInstanceStates,
    first: Int,
    second: Int,
): Int {
    if (first == ABSENT_INSTANCE || second == ABSENT_INSTANCE) return ABSENT_INSTANCE

    var left = first
    var right = second
    var leftDepth = depth(states, left)
    var rightDepth = depth(states, right)
    while (leftDepth > rightDepth) {
        left = states.parents[left]
        leftDepth -= 1
    }
    while (rightDepth > leftDepth) {
        right = states.parents[right]
        rightDepth -= 1
    }
    while (left != right) {
        left = states.parents[left]
        right = states.parents[right]
    }
    return left
}

private fun depth(
    states: ComponentInstanceStates,
    instance: Int,
): Int {
    var depth = 0
    var current = instance
    while (current != ABSENT_INSTANCE) {
        depth += 1
        current = states.parents[current]
    }
    return depth
}

private const val ABSENT_INSTANCE = -1
