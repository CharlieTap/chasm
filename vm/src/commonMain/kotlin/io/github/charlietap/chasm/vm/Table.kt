package io.github.charlietap.chasm.vm

internal expect class TableReference

class Table internal constructor(internal val reference: TableReference)
