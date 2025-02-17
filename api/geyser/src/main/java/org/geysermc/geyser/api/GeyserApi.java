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

package org.geysermc.geyser.api;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.api.Geyser;
import org.geysermc.api.GeyserApiBase;
import org.geysermc.geyser.api.command.CommandManager;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.custom.CustomManager;
import org.geysermc.geyser.api.custom.items.CustomItemManager;
import org.geysermc.geyser.api.event.EventBus;
import org.geysermc.geyser.api.extension.ExtensionManager;
import org.geysermc.geyser.api.network.BedrockListener;
import org.geysermc.geyser.api.network.RemoteServer;

import java.util.List;
import java.util.UUID;

/**
 * Represents the API used in Geyser.
 */
public interface GeyserApi extends GeyserApiBase {
    /**
     * Shuts down the current Geyser instance.
     */
    void shutdown();

    /**
     * Reloads the current Geyser instance.
     */
    void reload();

    /**
     * Gets if this Geyser instance is running in an IDE. This only needs to be used in cases where files
     * expected to be in a jarfile are not present.
     *
     * @return if we are in a production environment
     */
    boolean isProductionEnvironment();

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable GeyserConnection connectionByUuid(@NonNull UUID uuid);

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable GeyserConnection connectionByXuid(@NonNull String xuid);

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable GeyserConnection connectionByName(@NonNull String name);

    /**
     * {@inheritDoc}
     */
    @NonNull
    List<? extends GeyserConnection> onlineConnections();

    /**
     * Gets the {@link ExtensionManager}.
     *
     * @return the extension manager
     */
    ExtensionManager extensionManager();

    /**
     * Gets the {@link CommandManager}.
     *
     * @return the command manager
     */
    CommandManager commandManager();

    /**
     * Gets the {@link EventBus} for handling
     * Geyser events.
     *
     * @return the event bus
     */
    EventBus eventBus();

    /**
     * Gets the {@link CustomItemManager}.
     *
     * @return the custom model data manager
     */
    CustomManager customManager();

    /**
     * Get's the default {@link RemoteServer} configured
     * within the config file that is used by default.
     *
     * @return the default remote server used within Geyser
     */
    RemoteServer defaultRemoteServer();

    /**
     * Gets the {@link BedrockListener} used for listening
     * for Minecraft: Bedrock Edition client connections.
     *
     * @return the listener used for Bedrock client connectins
     */
    BedrockListener bedrockListener();

    /**
     * Gets the maximum number of players that
     * can join this Geyser instance.
     *
     * @return the maximum number of players that
     *         can join this Geyser instance
     */
    int maxPlayers();

    /**
     * Gets the current {@link GeyserApiBase} instance.
     *
     * @return the current geyser api instance
     */
    static GeyserApi api() {
        return Geyser.api(GeyserApi.class);
    }
}
