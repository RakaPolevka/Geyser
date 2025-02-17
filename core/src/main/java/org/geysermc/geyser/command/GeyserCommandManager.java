/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.command;

import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.common.PlatformType;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.api.command.Command;
import org.geysermc.geyser.api.command.CommandExecutor;
import org.geysermc.geyser.api.command.CommandManager;
import org.geysermc.geyser.api.command.CommandSource;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCommandsEvent;
import org.geysermc.geyser.command.defaults.*;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.text.GeyserLocale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RequiredArgsConstructor
public abstract class GeyserCommandManager extends CommandManager {

    @Getter
    private final Map<String, Command> commands = new HashMap<>();

    private final GeyserImpl geyser;

    public void init() {
        register(new HelpCommand(geyser, "help", "geyser.commands.help.desc", "geyser.command.help"));
        register(new ListCommand(geyser, "list", "geyser.commands.list.desc", "geyser.command.list"));
        register(new ReloadCommand(geyser, "reload", "geyser.commands.reload.desc", "geyser.command.reload"));
        register(new OffhandCommand(geyser, "offhand", "geyser.commands.offhand.desc", "geyser.command.offhand"));
        register(new DumpCommand(geyser, "dump", "geyser.commands.dump.desc", "geyser.command.dump"));
        register(new VersionCommand(geyser, "version", "geyser.commands.version.desc", "geyser.command.version"));
        register(new SettingsCommand(geyser, "settings", "geyser.commands.settings.desc", "geyser.command.settings"));
        register(new StatisticsCommand(geyser, "statistics", "geyser.commands.statistics.desc", "geyser.command.statistics"));
        register(new AdvancementsCommand("advancements", "geyser.commands.advancements.desc", "geyser.command.advancements"));
        register(new AdvancedTooltipsCommand("tooltips", "geyser.commands.advancedtooltips.desc", "geyser.command.tooltips"));
        register(new ExtensionsCommand(geyser, "extensions", "geyser.commands.extensions.desc", "geyser.command.extensions"));
        if (GeyserImpl.getInstance().getPlatformType() == PlatformType.STANDALONE) {
            register(new StopCommand(geyser, "stop", "geyser.commands.stop.desc", "geyser.command.stop"));
        }

        this.geyser.eventBus().fire(new GeyserDefineCommandsEvent(this, this.commands));
    }

    @Override
    public void register(@NonNull Command command) {
        this.commands.put(command.name(), command);
        this.geyser.getLogger().debug(GeyserLocale.getLocaleStringLog("geyser.commands.registered", command.name()));

        if (command.aliases().isEmpty()) {
            return;
        }

        for (String alias : command.aliases()) {
            this.commands.put(alias, command);
        }
    }

    @Override
    public void unregister(@NonNull Command command) {
        this.commands.remove(command.name(), command);

        if (command.aliases().isEmpty()) {
            return;
        }

        for (String alias : command.aliases()) {
            this.commands.remove(alias, command);
        }
    }

    @NotNull
    @Override
    public Map<String, Command> commands() {
        return Collections.unmodifiableMap(this.commands);
    }

    public void runCommand(GeyserCommandSource sender, String command) {
        if (!command.startsWith("geyser "))
            return;

        command = command.trim().replace("geyser ", "");
        String label;
        String[] args;

        if (!command.contains(" ")) {
            label = command.toLowerCase();
            args = new String[0];
        } else {
            label = command.substring(0, command.indexOf(" ")).toLowerCase();
            String argLine = command.substring(command.indexOf(" ") + 1);
            args = argLine.contains(" ") ? argLine.split(" ") : new String[] { argLine };
        }

        Command cmd = commands.get(label);
        if (cmd == null) {
            geyser.getLogger().error(GeyserLocale.getLocaleStringLog("geyser.commands.invalid"));
            return;
        }

        if (cmd instanceof GeyserCommand) {
            if (sender instanceof GeyserSession) {
                ((GeyserCommand) cmd).execute((GeyserSession) sender, sender, args);
            } else {
                if (!cmd.isBedrockOnly()) {
                    ((GeyserCommand) cmd).execute(null, sender, args);
                } else {
                    geyser.getLogger().error(GeyserLocale.getLocaleStringLog("geyser.bootstrap.command.bedrock_only"));
                }
            }
        }
    }

    /**
     * Returns the description of the given command
     *
     * @param command Command to get the description for
     * @return Command description
     */
    public abstract String description(String command);

    @Override
    protected <T extends CommandSource> Command.Builder<T> provideBuilder(Class<T> sourceType) {
        return new CommandBuilder<>(sourceType);
    }

    @RequiredArgsConstructor
    public static class CommandBuilder<T extends CommandSource> implements Command.Builder<T> {
        private final Class<T> sourceType;
        private String name;
        private String description = "";
        private String permission = "";
        private List<String> aliases;
        private boolean executableOnConsole = true;
        private List<String> subCommands;
        private boolean bedrockOnly;
        private CommandExecutor<T> executor;

        public CommandBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        public CommandBuilder<T> description(String description) {
            this.description = description;
            return this;
        }

        public CommandBuilder<T> permission(String permission) {
            this.permission = permission;
            return this;
        }

        public CommandBuilder<T> aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public CommandBuilder<T> executableOnConsole(boolean executableOnConsole) {
            this.executableOnConsole = executableOnConsole;
            return this;
        }

        public CommandBuilder<T> subCommands(List<String> subCommands) {
            this.subCommands = subCommands;
            return this;
        }

        public CommandBuilder<T> bedrockOnly(boolean bedrockOnly) {
            this.bedrockOnly = bedrockOnly;
            return this;
        }

        public CommandBuilder<T> executor(CommandExecutor<T> executor) {
            this.executor = executor;
            return this;
        }

        public GeyserCommand build() {
            if (this.name == null || this.name.isBlank()) {
                throw new IllegalArgumentException("Command cannot be null or blank!");
            }

            return new GeyserCommand(this.name, this.description, this.permission) {

                @SuppressWarnings("unchecked")
                @Override
                public void execute(@Nullable GeyserSession session, GeyserCommandSource sender, String[] args) {
                    Class<T> sourceType = CommandBuilder.this.sourceType;
                    CommandExecutor<T> executor = CommandBuilder.this.executor;
                    if (sourceType.isInstance(session)) {
                        executor.execute((T) session, this, args);
                        return;
                    }

                    if (sourceType.isInstance(sender)) {
                        executor.execute((T) sender, this, args);
                        return;
                    }

                    GeyserImpl.getInstance().getLogger().debug("Ignoring command " + this.name + " due to no suitable sender.");
                }

                @NonNull
                @Override
                public List<String> aliases() {
                    return CommandBuilder.this.aliases == null ? Collections.emptyList() : CommandBuilder.this.aliases;
                }

                @NonNull
                @Override
                public List<String> subCommands() {
                    return CommandBuilder.this.subCommands == null ? Collections.emptyList() : CommandBuilder.this.subCommands;
                }

                @Override
                public boolean isBedrockOnly() {
                    return CommandBuilder.this.bedrockOnly;
                }

                @Override
                public boolean isExecutableOnConsole() {
                    return CommandBuilder.this.executableOnConsole;
                }
            };
        }
    }
}
