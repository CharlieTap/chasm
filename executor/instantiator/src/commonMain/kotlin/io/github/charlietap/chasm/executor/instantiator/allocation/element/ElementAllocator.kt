package io.github.charlietap.chasm.executor.instantiator.allocation.element

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias ElementAllocator = (Store, ReferenceType, List<ReferenceValue>) -> Address.Element
