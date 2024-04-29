package net.soviaat.vexsoundpause;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

import static net.soviaat.vexsoundpause.VexSoundPauseMod.MOD_VERSION;


public class VexSoundPauseModClient implements ClientModInitializer {
    public static long COOLDOWN_SECONDS = 210;
    private static long ADVANCEMENTS_INTERVAL = COOLDOWN_SECONDS * 20;
    private static long lastPopup = 0;
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean isListening = false;

    private static final HashMap<String, Boolean> hasJoined = new HashMap<>();
    private static final Gson GSON = new Gson();
    private static final Path SAVE_FILE = FabricLoader.getInstance().getGameDir().resolve("hasJoined.json");

    public static long getLastPopup() {
        return lastPopup;
    }

    @Override
    public void onInitializeClient() {

        if(Files.exists(SAVE_FILE)) {
            try (Reader reader = Files.newBufferedReader(SAVE_FILE)) {
                hasJoined.putAll(GSON.fromJson(reader, new TypeToken<HashMap<String, Boolean>>() {}.getType()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ClientPlayConnectionEvents.JOIN.register((server, world, client) -> {
            if (client.player != null) {
                String worldName = Objects.requireNonNull(client.getServer()).getSavePath(WorldSavePath.ROOT).toString();

                if (!hasJoined.containsKey(worldName)) {
                    hasJoined.put(worldName, true);
                    welcomeText();
                }

                try (Writer writer = Files.newBufferedWriter(SAVE_FILE)) {
                    GSON.toJson(hasJoined, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                if (isCooldownActive()) {
                    long currentTime = client.world.getTime();
                    if (currentTime >= lastPopup) {
                        setCooldownActive(false);
                    }
                }
            }
        });
    }
    public static void triggerPopup() {
        if (mc != null && mc.player != null && mc.world != null) {
            mc.execute(() -> mc.setScreen(new AdvancementsScreen(mc.player.networkHandler.getAdvancementHandler())));
            setCooldownActive(true);

        }
    }

    public static boolean isCooldownActive() {
        if (mc.world == null) return false;
        assert MinecraftClient.getInstance().world != null;
        return lastPopup > MinecraftClient.getInstance().world.getTime();
    }

    public static void setCooldownActive(boolean active) {
        if (mc != null && mc.player != null && mc.world != null) {
            if(active) {
                lastPopup = mc.world.getTime() + ADVANCEMENTS_INTERVAL;
                mc.player.sendMessage(Text.of(String.format("\n" + "§aCooldown has started (%ss)", COOLDOWN_SECONDS)));
            } else {
                lastPopup = 0;
                mc.player.sendMessage(Text.of("§c\nCooldown has ended."));
            }
            // mc.player.sendMessage(Text.of(active ? "Cooldown has started ("+ COOLDOWN_SECONDS +"s)" : "Cooldown has ended"), false);
        }
    }

    public static void setCooldown(long seconds) {
        COOLDOWN_SECONDS = seconds;
        ADVANCEMENTS_INTERVAL = COOLDOWN_SECONDS * 20;
    }

    public static long showCooldown() {
        return COOLDOWN_SECONDS;
    }

    public static boolean isOnCooldown() {
        return isCooldownActive();
    }

    public static String getPlayerName() {
        if (mc.player != null && mc.world != null) {
            return mc.player.getEntityName();
        }
        return "Player";
    }

    public static void welcomeText() {
        if (mc.player != null) {
            ClientPlayerEntity player = mc.player;
            player.sendMessage(Text.literal(String.format("Welcome §9§l%s§r.", getPlayerName())));
            player.sendMessage(Text.of(String.format("§6VexSoundPauseMod by Soviaat (version: %s)\n\n", MOD_VERSION)), false);
            player.sendMessage(Text.of("Sound detection is currently turned " + (isListening ? "§aon" : "§coff") + ".§f\nUse §d§l/VexToggleDetection§r to turn it " + (isListening ? "§coff." : "§aon." + "§r\nTo get help, use §d§l§o/VexHelp§r.")), false);
        }
    }
}
