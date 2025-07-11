package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.decoder.section.StartSection
import io.github.charlietap.chasm.fixture.ast.module.startFunction

internal fun startSection(
    startFunction: StartFunction = startFunction(),
) = StartSection(startFunction)
