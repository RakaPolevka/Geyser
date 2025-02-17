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

package org.geysermc.geyser.custom.mappings;

import com.fasterxml.jackson.databind.JsonNode;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.custom.GeyserCustomManager;
import org.geysermc.geyser.custom.mappings.versions.MappingsReader;
import org.geysermc.geyser.custom.mappings.versions.MappingsReader_v1_0_0;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MappingsConfigReader {
    private static final Map<String, MappingsReader> MAPPING_READERS = new HashMap<>();

    public static void init(GeyserCustomManager customManager) {
        MAPPING_READERS.put("1.0.0", new MappingsReader_v1_0_0(customManager));
    }

    public static Path getCustomMappingsDirectory() {
        return GeyserImpl.getInstance().getBootstrap().getConfigFolder().resolve("custom_mappings");
    }

    public static File[] getCustomMappingsFiles() {
        File[] files = getCustomMappingsDirectory().toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return new File[0];
        }
        return files;
    }

    public static void readMappingsFromJson(File file) {
        JsonNode mappingsRoot;
        try {
            mappingsRoot = GeyserImpl.JSON_MAPPER.readTree(file);
        } catch (IOException e) {
            GeyserImpl.getInstance().getLogger().error("Failed to read custom mapping file: " + file.getName(), e);
            return;
        }

        if (!mappingsRoot.has("format_version")) {
            GeyserImpl.getInstance().getLogger().error("Mappings file " + file.getName() + " is missing the format version field!");
            return;
        }

        String formatVersion = mappingsRoot.get("format_version").asText();
        if (!MAPPING_READERS.containsKey(formatVersion)) {
            GeyserImpl.getInstance().getLogger().error("Mappings file " + file.getName() + " has an unknown format version: " + formatVersion);
            return;
        }

        MAPPING_READERS.get(formatVersion).readMappings(file, mappingsRoot);
    }
}
