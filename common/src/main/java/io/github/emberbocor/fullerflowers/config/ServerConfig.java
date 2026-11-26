package io.github.emberbocor.fullerflowers.config;

import java.nio.file.Path;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.fullerflowers.platform.Services;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public final class ServerConfig {
    static final int MIN_FLOWERS = 1;
    static final int MAX_FLOWERS = 8;
    static final int MIN_POT_FLOWERS = 1;
    static final int MAX_POT_FLOWERS = 4;
    static final Settings DEFAULTS = new Settings(4, Map.of(), 3);

    private static volatile Settings settings = DEFAULTS;
    private static volatile boolean serverRunning;

    private ServerConfig() {
    }

    /** Called when a server (dedicated or the singleplayer one) starts. */
    public static void load() {
        settings = DEFAULTS;
        Path file = Services.PLATFORM.getConfigDirectory().resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file).read(ServerConfigFile.load(file).orElse(null));
        serverRunning = true;
    }

    /** Called when that server has stopped. */
    public static void unload() {
        serverRunning = false;
        settings = DEFAULTS;
    }

    /**
     * The settings that apply to this level. On the server side always the loaded config. On the client side the
     * config is only known while the server runs in this game (singleplayer or LAN host); a client connected to
     * another server gets null, because the config is never sent to clients.
     */
    @Nullable
    public static Settings settingsFor(Level level) {
        return !level.isClientSide() || serverRunning ? settings : null;
    }

    public record Settings(int maxFlowers, Map<Identifier, Integer> flowerOverrides, int maxPotFlowers) {
        public Settings {
            flowerOverrides = Map.copyOf(flowerOverrides);
        }

        /** Most flowers one block of this flower may hold, 1 to 8. */
        public int maxFlowers(Identifier flower) {
            return flowerOverrides.getOrDefault(flower, maxFlowers);
        }

        /** Most flowers one flower pot of this flower may hold, 1 to 4. */
        public int maxPotFlowers(Identifier flower) {
            return Math.min(maxPotFlowers, maxFlowers(flower));
        }
    }
}
