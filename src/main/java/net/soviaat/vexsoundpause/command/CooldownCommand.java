package net.soviaat.vexsoundpause.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.soviaat.vexsoundpause.VexSoundPauseModClient;

import static net.soviaat.vexsoundpause.VexSoundPauseMod.clearChat;
import static net.soviaat.vexsoundpause.VexSoundPauseModClient.*;

public class CooldownCommand {
    public static void register() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register(CommandManager.literal("VexSetCooldown")
                .then(CommandManager.argument("seconds", IntegerArgumentType.integer(10, 600))
                    .executes(context -> {
                        int seconds = IntegerArgumentType.getInteger(context, "seconds");
                        VexSoundPauseModClient.setCooldown(seconds);
                        context.getSource().sendMessage(Text.literal("\nCooldown set to §d" + seconds + "§f seconds."));
                        return 1;
                    })
                )
                .executes(context -> {
                    if (COOLDOWN_SECONDS != 210) {
                        VexSoundPauseModClient.setCooldown(210);
                        context.getSource().sendMessage(Text.of("\nThe cooldown has reset to §d210 §fseconds."));
                    } else {
                        clearChat(false);
                        context.getSource().sendMessage(Text.of("§c\nYou have to pass in a value since the cooldown hasn't been changed."));
                    }
                        return 1;
                })
            );

            dispatcher.register(CommandManager.literal("VexShowCooldown")
                .executes(context -> {
                    long seconds = VexSoundPauseModClient.showCooldown();
                    long cooldownEnd = VexSoundPauseModClient.getLastPopup();
                    long currentTime = context.getSource().getWorld().getTime();
                    long remainingTicks = Math.max(0, cooldownEnd - currentTime);
                    long remainingSeconds = remainingTicks / 20;

                    if (isOnCooldown()) {
                        context.getSource().sendMessage(Text.literal("\nThe current cooldown is §d" + seconds + "§f seconds.\nThere " + (remainingSeconds > 2 ? "are §d" : "is §d") + remainingSeconds + (remainingSeconds > 2 ? "§r seconds" : "§r second") + " left."));
                    } else {
                        context.getSource().sendMessage(Text.literal("\nThe current cooldown is §d" + seconds + "§f seconds."));
                    }
                    return 1;
                })
            );

            dispatcher.register(CommandManager.literal("VexToggleDetection")
                .executes(context -> {
                    boolean active = isCooldownActive();
                    VexSoundPauseModClient.isListening = !VexSoundPauseModClient.isListening;
                        context.getSource().sendMessage(Text.literal("\nListening for vex sounds is now " +
                        (VexSoundPauseModClient.isListening ? "§aenabled" : "§cdisabled") + "§f."));
                        if (active) {
                            setCooldownActive(false);
                        }

                    return 1;
                })
            );

            dispatcher.register(CommandManager.literal("VexIsOnCooldown")
                .executes(context -> {
                    boolean onCooldown = isOnCooldown();
                context.getSource().sendMessage(Text.literal("\nListening " +
                    (onCooldown ? "is §acurrently on §fcooldown." : "is §cnot §fon cooldown.")));
                    return 1;
            }));

            /*
            dispatcher.register(CommandManager.literal("VexEndCooldown")
                .executes(context -> {
                    boolean active = isCooldownActive();
                    if (!VexSoundPauseModClient.isListening) {
                        if (active) {
                            setCooldownActive(false);
                        } else {
                            context.getSource().sendMessage(Text.literal("\nCooldown is already stopped."));
                        }
                    } else {
                        context.getSource().sendMessage(Text.literal("\nYou have to stop the detection first (§d§o/VexToggleDetection§f)"));
                    }
                        return 1;
                })
            ); */

            dispatcher.register(CommandManager.literal("clearChat")
                .executes(context -> {
                    clearChat();
                        context.getSource().sendMessage(Text.literal("§7§oChat cleared"));
                    try {
                        Thread.sleep(2000);
                        clearChat();
                    } catch (InterruptedException e) {
                        return 1;
                    }
                    return 1;
                })
            );
        });
    }
}
