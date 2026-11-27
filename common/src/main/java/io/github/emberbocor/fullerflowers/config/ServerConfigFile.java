package io.github.emberbocor.fullerflowers.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import io.github.emberbocor.fullerflowers.FullerFlowers;

final class ServerConfigFile {
    static final String FILE_NAME = FullerFlowers.MODID + "-server.toml";

    private static final String DEFAULT_TEXT = """
            # Fuller Flowers server config.
            # Created on the first server or world start.
            # Restart the server or reopen the singleplayer world to apply changes.

            # Most flowers one block can hold, a whole number from 1 to 8. Applies to every supported flower.
            # 1 means flowers never grow into clusters.
            maxFlowers = 4

            # Per-flower values that replace maxFlowers for the flowers listed, for example
            # ["minecraft:poppy=5", "minecraft:wither_rose=1"]. Each value is a whole number from 1 to 8.
            # Supported flowers: dandelion, poppy, blue_orchid, allium, azure_bluet, red_tulip, orange_tulip,
            # white_tulip, pink_tulip, oxeye_daisy, cornflower, lily_of_the_valley, torchflower, wither_rose,
            # open_eyeblossom, closed_eyeblossom, golden_dandelion.
            # Open and closed eyeblossoms share one limit: an override for either one applies to both.
            flowerOverrides = []

            # Most flowers one flower pot can hold, a whole number from 1 to 4.
            # A pot never holds more than the block limit of that flower.
            maxPotFlowers = 3
            """;

    private ServerConfigFile() {
    }

    static Optional<UnmodifiableConfig> load(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, DEFAULT_TEXT, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                FullerFlowers.LOGGER.info("Created {}", file);
            } catch (IOException e) {
                FullerFlowers.LOGGER.warn("Could not create {}, using the default settings: {}", file, e.getMessage());
            }
            return parse(file, DEFAULT_TEXT);
        }

        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            return parse(file, text.startsWith("﻿") ? text.substring(1) : text);
        } catch (IOException e) {
            FullerFlowers.LOGGER.warn("Could not read {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<UnmodifiableConfig> parse(Path file, String text) {
        try {
            return Optional.of(TomlFormat.instance().createParser().parse(text));
        } catch (RuntimeException e) {
            FullerFlowers.LOGGER.warn("Could not parse {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }
}
