package io.github.charlietap.chasm.type.expansion

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType

typealias BlockTypeExpander = (List<DefinedType>, ControlInstruction.BlockType) -> FunctionType?
