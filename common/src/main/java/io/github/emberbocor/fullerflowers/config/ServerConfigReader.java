package io.github.emberbocor.fullerflowers.config;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import io.github.emberbocor.fullerflowers.FullerFlowers;
import io.github.emberbocor.fullerflowers.block.ModBlocks;
import net.minecraft.resources.Identifier;

final class ServerConfigReader {
    private static final Set<String> KEYS = Set.of("maxFlowers", "flowerOverrides", "maxPotFlowers");
    private static final Pattern WHOLE_NUMBER = Pattern.compile("-?[0-9]{1,9}");

    private final Path file;

    ServerConfigReader(Path file) {
        this.file = file;
    }

    ServerConfig.Settings read(UnmodifiableConfig config) {
        ServerConfig.Settings defaults = ServerConfig.DEFAULTS;
        if (config == null) {
            return defaults;
        }
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            if (!KEYS.contains(entry.getKey())) {
                FullerFlowers.LOGGER.warn("{}: unknown option '{}', ignored", file, entry.getKey());
            }
        }
        return new ServerConfig.Settings(
                wholeNumber(config, "maxFlowers", ServerConfig.MIN_FLOWERS, ServerConfig.MAX_FLOWERS, defaults.maxFlowers()),
                flowerOverrides(config, defaults.flowerOverrides()),
                wholeNumber(config, "maxPotFlowers", ServerConfig.MIN_POT_FLOWERS, ServerConfig.MAX_POT_FLOWERS, defaults.maxPotFlowers()));
    }

    private int wholeNumber(UnmodifiableConfig config, String key, int min, int max, int fallback) {
        Object value = config.get(key);
        if (value == null) {
            return fallback;
        }
        if ((value instanceof Integer || value instanceof Long) && ((Number) value).longValue() >= min
                && ((Number) value).longValue() <= max) {
            return ((Number) value).intValue();
        }
        FullerFlowers.LOGGER.warn("{}: {} must be a whole number from {} to {}, got '{}'; using {}", file, key, min, max, value, fallback);
        return fallback;
    }

    private Map<Identifier, Integer> flowerOverrides(UnmodifiableConfig config, Map<Identifier, Integer> fallback) {
        Object value = config.get("flowerOverrides");
        if (value == null) {
            return fallback;
        }
        if (!(value instanceof List<?> list)) {
            FullerFlowers.LOGGER.warn("{}: flowerOverrides must be a list such as [\"minecraft:poppy=5\"], got '{}'; using []", file, value);
            return fallback;
        }
        Map<Identifier, Integer> overrides = new LinkedHashMap<>();
        for (Object element : list) {
            Identifier flower = null;
            Integer max = null;
            if (element instanceof String string) {
                int separator = string.lastIndexOf('=');
                if (separator > 0) {
                    flower = Identifier.tryParse(string.substring(0, separator).trim());
                    max = parseWholeNumber(string.substring(separator + 1).trim());
                }
            }
            if (flower == null || max == null) {
                FullerFlowers.LOGGER.warn("{}: flowerOverrides entry '{}' is not of the form \"minecraft:poppy=5\", skipped", file, element);
            } else if (!ModBlocks.isSupportedFlower(flower)) {
                FullerFlowers.LOGGER.warn("{}: flowerOverrides entry '{}' does not name a supported flower, skipped", file, element);
            } else if (max < ServerConfig.MIN_FLOWERS || max > ServerConfig.MAX_FLOWERS) {
                FullerFlowers.LOGGER.warn("{}: flowerOverrides entry '{}' must use a whole number from {} to {}, skipped",
                        file, element, ServerConfig.MIN_FLOWERS, ServerConfig.MAX_FLOWERS);
            } else {
                overrides.put(ModBlocks.limitId(flower), max);
            }
        }
        return overrides;
    }

    private static Integer parseWholeNumber(String text) {
        return WHOLE_NUMBER.matcher(text).matches() ? Integer.valueOf(text) : null;
    }
}
