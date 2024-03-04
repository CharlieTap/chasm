package io.github.charlietap.chasm.executor.instantiator.allocation.table

import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias TableAllocator = (Store, TableType, ReferenceValue) -> Address.Table
