package io.github.charlietap.chasm.fixture.runtime.component.info

import io.github.charlietap.chasm.fixture.runtime.component.index.preparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExport
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExportValue

fun preparedComponentExport(
    name: String = "export",
    value: PreparedComponentExportValue = PreparedComponentExportValue.Function(
        preparedComponentFunctionIndex(),
    ),
) = PreparedComponentExport(
    name = name,
    value = value,
)

fun preparedFunctionExport(
    function: PreparedComponentFunctionIndex = preparedComponentFunctionIndex(),
) = PreparedComponentExportValue.Function(function)

fun preparedInstanceExport(
    exports: List<PreparedComponentExport> = emptyList(),
) = PreparedComponentExportValue.Instance(exports)
