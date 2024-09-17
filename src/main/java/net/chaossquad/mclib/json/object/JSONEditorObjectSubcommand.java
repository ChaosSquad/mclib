package net.chaossquad.mclib.json.object;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.json.JSONEditorCommand;
import net.chaossquad.mclib.json.object.subcommands.*;
import org.jetbrains.annotations.NotNull;

public class JSONEditorObjectSubcommand extends SubcommandCommand {
    public static final String NO_EDITABLE_MESSAGE = "§cThere is currently no editable json object loaded";
    @NotNull private final JSONEditorCommand manager;

    public JSONEditorObjectSubcommand(@NotNull JSONEditorCommand manager) {
        super(manager.getPlugin(), "§cUsage: [...] config (info|unload|show|get <key>|set <key> <type> <value>|remove|load-object|load-array|load-empty)");
        this.manager = manager;

        this.addSubcommand("info", SubcommandEntry.of(new JSONEditorObjectInfoSubcommand(this)));
        this.addSubcommand("unload", SubcommandEntry.of(new JSONEditorObjectUnloadSubcommand(this)));
        this.addSubcommand("show", SubcommandEntry.of(new JSONEditorObjectShowSubcommand(this)));
        this.addSubcommand("get", SubcommandEntry.of(new JSONEditorObjectGetSubcommand(this)));
        this.addSubcommand("set", SubcommandEntry.of(new JSONEditorObjectSetSubcommand(this)));
        this.addSubcommand("remove", SubcommandEntry.of(new JSONEditorObjectRemoveSubcommand(this)));
        this.addSubcommand("get-object", SubcommandEntry.of(new JSONEditorObjectGetObjectSubcommand(this)));
        this.addSubcommand("get-array", SubcommandEntry.of(new JSONEditorObjectGetArraySubcommand(this)));
        this.addSubcommand("load-empty", SubcommandEntry.of(new JSONEditorObjectLoadEmptySubcommand(this)));
        this.addSubcommand("set-array", SubcommandEntry.of(new JSONEditorObjectSetArraySubcommand(this)));
        this.addSubcommand("set-object", SubcommandEntry.of(new JSONEditorObjectSetObjectSubcommand(this)));
        this.addSubcommand("clear", SubcommandEntry.of(new JSONEditorObjectClearSubcommand(this)));
    }

    // ----- OTHER -----

    @NotNull
    public JSONEditorCommand getManager() {
        return this.manager;
    }

}
