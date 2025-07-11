package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.decoder.section.code.FunctionBody
import io.github.charlietap.chasm.decoder.section.CodeSection
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.module.functionIndex

internal fun functionBody(
    idx: Index.FunctionIndex = functionIndex(),
    locals: List<Local> = emptyList(),
    body: Expression = expression(),
) = FunctionBody(
    idx = idx,
    locals = locals,
    body = body,
)

internal fun codeSection(
    bodies: List<FunctionBody> = emptyList(),
) = CodeSection(bodies)
