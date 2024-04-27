package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.sweet.lib.command.ActionCommand
import io.github.charlietap.sweet.lib.command.AssertExhaustionCommand
import io.github.charlietap.sweet.lib.command.AssertInvalidCommand
import io.github.charlietap.sweet.lib.command.AssertMalformedCommand
import io.github.charlietap.sweet.lib.command.AssertReturnCommand
import io.github.charlietap.sweet.lib.command.AssertTrapCommand
import io.github.charlietap.sweet.lib.command.AssertUninstantiableCommand
import io.github.charlietap.sweet.lib.command.AssertUnlinkableCommand
import io.github.charlietap.sweet.lib.command.Command
import io.github.charlietap.sweet.lib.command.ModuleCommand
import io.github.charlietap.sweet.lib.command.RegisterCommand

typealias CommandRunner = (ScriptContext, Command) -> CommandResult

fun CommandRunner(
    context: ScriptContext,
    command: Command,
): CommandResult = CommandRunner(
    context = context,
    command = command,
    actionCommandRunner = ::ActionCommandRunner,
    assertExhaustionCommandRunner = ::AssertExhaustionCommandRunner,
    assertInvalidCommandRunner = ::AssertInvalidCommandRunner,
    assertMalformedCommandRunner = ::AssertMalformedCommandRunner,
    assertReturnCommandRunner = ::AssertReturnCommandRunner,
    assertTrapCommandRunner = ::AssertTrapCommandRunner,
    assertUninstantiableCommandRunner = ::AssertUninstantiableCommandRunner,
    assertUnlinkableCommandRunner = ::AssertUnlinkableCommandRunner,
    moduleCommandRunner = ::ModuleCommandRunner,
    registerCommandRunner = ::RegisterCommandRunner,
)

private fun CommandRunner(
    context: ScriptContext,
    command: Command,
    actionCommandRunner: ActionCommandRunner,
    assertExhaustionCommandRunner: AssertExhaustionCommandRunner,
    assertInvalidCommandRunner: AssertInvalidCommandRunner,
    assertMalformedCommandRunner: AssertMalformedCommandRunner,
    assertReturnCommandRunner: AssertReturnCommandRunner,
    assertTrapCommandRunner: AssertTrapCommandRunner,
    assertUninstantiableCommandRunner: AssertUninstantiableCommandRunner,
    assertUnlinkableCommandRunner: AssertUnlinkableCommandRunner,
    moduleCommandRunner: ModuleCommandRunner,
    registerCommandRunner: RegisterCommandRunner,
): CommandResult = when (command) {
    is ActionCommand -> actionCommandRunner(context, command)
    is AssertExhaustionCommand -> assertExhaustionCommandRunner(command)
    is AssertInvalidCommand -> assertInvalidCommandRunner(command)
    is AssertMalformedCommand -> assertMalformedCommandRunner(command)
    is AssertReturnCommand -> assertReturnCommandRunner(context, command)
    is AssertTrapCommand -> assertTrapCommandRunner(context, command)
    is AssertUninstantiableCommand -> assertUninstantiableCommandRunner(context, command)
    is AssertUnlinkableCommand -> assertUnlinkableCommandRunner(command)
    is ModuleCommand -> moduleCommandRunner(context, command)
    is RegisterCommand -> registerCommandRunner(context, command)
}
