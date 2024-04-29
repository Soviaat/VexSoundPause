package net.soviaat.vexsoundpause;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.soviaat.vexsoundpause.command.CooldownCommand;
import net.soviaat.vexsoundpause.command.HelpCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VexSoundPauseMod implements ModInitializer {
	public static final String MOD_ID = "vexsoundpause";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String MOD_VERSION = "v1.1";


	@Override
	public void onInitialize() {
		LOGGER.info("The VexSoundPause Mod has been initialized. Made by Soviaat.");
		CooldownCommand.register();
		HelpCommand.register();
	}

	public static void clearChat() {
		clearChat(false);
	}

	public static void clearChat(boolean clearHistory) {
			MinecraftClient.getInstance().inGameHud.getChatHud().clear(clearHistory);
	}
}