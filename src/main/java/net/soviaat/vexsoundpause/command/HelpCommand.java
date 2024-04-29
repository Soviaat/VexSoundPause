package net.soviaat.vexsoundpause.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

import static net.soviaat.vexsoundpause.VexSoundPauseMod.clearChat;

public class HelpCommand {
    private static final Map<String, String> commandUsages = new HashMap<>();

    static {
        commandUsages.put("VexSetCooldown", "§oSets the cooldown duration(10s - 600s) §r\nUsage: §6/VexSetCooldown §8§o<duration>");
        commandUsages.put("VexShowCooldown", "§oShows the current cooldown duration §r\nUsage: §6/VexShowCooldown");
        commandUsages.put("VexToggleDetection", "§oToggles the detection of Vexes §r\nUsage: §6/VexToggleDetection");
        commandUsages.put("VexIsOnCooldown", "§oChecks if the detection is on cooldown §r\nUsage: §6/VexIsOnCooldown");
    }

    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
           dispatcher.register(CommandManager.literal("VexHelp")
               .then(CommandManager.argument("command", StringArgumentType.word())
                   .suggests((context, builder) -> CommandSource.suggestMatching(commandUsages.keySet(), builder))
                   .executes(context -> {

                       String command = StringArgumentType.getString(context, "command");
                       String usage = commandUsages.get(command);

                       if (usage != null) {
                           clearChat();
                           context.getSource().sendMessage(Text.of("                       §nVexSoundDetectionPause Help"));
                           context.getSource().sendMessage(Text.of("\n" + usage));
                       } else {
                           context.getSource().sendMessage(Text.of("§cUnknown command: " + command));
                       }
                       return 1;
                   }))
                   .executes(context -> {
                       clearChat();
                       context.getSource().sendMessage(Text.of("                       §nVexSoundDetectionPause Help"));
                       commandUsages.keySet().forEach(command -> context.getSource().sendMessage(Text.of("\n§d§n" + command + ":§r " + commandUsages.get(command))));
                       return 1;
                   }
               )
           );
        });
    }
}
