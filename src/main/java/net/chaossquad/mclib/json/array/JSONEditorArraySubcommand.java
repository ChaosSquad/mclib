package net.chaossquad.mclib.json.array;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.json.JSONEditorCommand;
import net.chaossquad.mclib.json.array.subcommands.*;
import org.jetbrains.annotations.NotNull;

public class JSONEditorArraySubcommand extends SubcommandCommand {
    public static final String NO_EDITABLE_MESSAGE = "§cThere is currently no editable json array loaded";

    @NotNull JSONEditorCommand manager;

    public JSONEditorArraySubcommand(@NotNull JSONEditorCommand manager) {
        super(manager.getPlugin(), "§cUsage: [...] config (info|unload|show|get <key>|set <key> <type> <value>|remove|load-object|load-array|load-empty)");
        this.manager = manager;

        this.addSubcommand("info", SubcommandEntry.of(new JSONEditorArrayInfoSubcommand(this)));
        this.addSubcommand("unload", SubcommandEntry.of(new JSONEditorArrayUnloadSubcommand(this)));
        this.addSubcommand("show", SubcommandEntry.of(new JSONEditorArrayShowSubcommand(this)));
        this.addSubcommand("size", SubcommandEntry.of(new JSONEditorArraySizeSubcommand(this)));
        this.addSubcommand("get", SubcommandEntry.of(new JSONEditorArrayGetSubcommand(this)));
        this.addSubcommand("set", SubcommandEntry.of(new JSONEditorArraySetSubcommand(this)));
        this.addSubcommand("remove", SubcommandEntry.of(new JSONEditorArrayRemoveSubcommand(this)));
        this.addSubcommand("get-object", SubcommandEntry.of(new JSONEditorArrayGetObjectSubcommand(this)));
        this.addSubcommand("get-array", SubcommandEntry.of(new JSONEditorArrayGetArraySubcommand(this)));
        this.addSubcommand("load-empty", SubcommandEntry.of(new JSONEditorArrayLoadEmptySubcommand(this)));
        this.addSubcommand("set-object", SubcommandEntry.of(new JSONEditorArraySetObjectSubcommand(this)));
        this.addSubcommand("set-array", SubcommandEntry.of(new JSONEditorArraySetArraySubcommand(this)));
        this.addSubcommand("clear", SubcommandEntry.of(new JSONEditorArrayClearSubcommand(this)));
    }

    public @NotNull JSONEditorCommand getManager() {
        return manager;
    }
}
