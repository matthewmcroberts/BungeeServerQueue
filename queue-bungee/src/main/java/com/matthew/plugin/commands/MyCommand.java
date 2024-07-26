package com.matthew.plugin.commands;


import com.matthew.plugin.modules.messages.MessageModule;
import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.queue.events.PlayerPriorityQueueJoinEvent;
import com.matthew.plugin.modules.settings.SettingsConstants;
import com.matthew.plugin.modules.settings.SettingsModule;
import com.matthew.plugin.util.ChannelMessaging;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class MyCommand extends Command implements TabExecutor {

    private final Plugin plugin;

    private final MessageModule messages = ModuleManager.getInstance().getRegisteredModule(MessageModule.class);

    private final QueueModule queue = ModuleManager.getInstance().getRegisteredModule(QueueModule.class);

    private final SettingsModule settings = ModuleManager.getInstance().getRegisteredModule(SettingsModule.class);

    private final Map<String, Consumer<ProxiedPlayer>> commandActions = new HashMap<>();


    public MyCommand(Plugin plugin) {
        super("testing");
        this.plugin = plugin;
        registerActions();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        final ProxiedPlayer player = commandSender instanceof ProxiedPlayer ? (ProxiedPlayer) commandSender : null;

        if (player == null) {
            plugin.getLogger().info("Sender must be a player");
            return;
        }

        final Optional<String> usageNodeOpt = settings.getString(SettingsConstants.PERMISSION_COMMAND_USE_NODE);

        if (usageNodeOpt.isEmpty()) {
            plugin.getLogger().severe("Command usage permission node not properly setup in bungeeconfig.yml");
            return;
        }

        ChannelMessaging.getInstance().hasPermission(player, usageNodeOpt.get()).thenAccept(hasPermission -> {
            if (hasPermission) {
                final TextComponent USAGE_MESSAGE = messages.buildMessage("usage");

                if (args.length == 0) {
                    player.sendMessage(USAGE_MESSAGE);
                    return;
                }

                //Value (if usage is correct) is either: help, list, leave, or join
                String actionArg = args[0].toLowerCase();

                Consumer<ProxiedPlayer> action = commandActions.getOrDefault(actionArg, p -> p.sendMessage(USAGE_MESSAGE));

                if (actionArg.equals("join")) {
                    if (args.length == 2) {
                        action.accept(player);
                        PlayerPriorityQueueJoinEvent event = new PlayerPriorityQueueJoinEvent(player, args[1]);
                        plugin.getProxy().getPluginManager().callEvent(event);
                    } else {
                        player.sendMessage(USAGE_MESSAGE);
                    }
                    return;
                }

                if (args.length == 1) {
                    action.accept(player);
                } else {
                    player.sendMessage(USAGE_MESSAGE);
                }
            }
        }).exceptionally(ex -> {
            plugin.getLogger().severe("Error checking permission for player " + player.getName() + ": " + ex.getMessage());
            return null;
        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = commandSender instanceof ProxiedPlayer ? (ProxiedPlayer) commandSender : null;

        if (player == null) {
            return List.of();
        }

        String arg0 = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1].toLowerCase() : "";

        if (args.length == 1) {
            return commandActions.keySet().stream()
                    .filter(command -> command.startsWith(arg0))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && "join".equalsIgnoreCase(arg0)) {

            //TODO: This shouldn't be players, this should be online servers that have a queue to join

            return plugin.getProxy().getPlayers().stream()
                    .map(ProxiedPlayer::getName)
                    .filter(name -> name.toLowerCase().startsWith(arg1))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    private void registerActions() {
        commandActions.put("help", player -> {
            //Logic for command argument help goes here
            player.sendMessage(new TextComponent("help"));
        });

        commandActions.put("list", player -> {
            //Logic for command argument list goes here
            player.sendMessage(new TextComponent("list"));
        });

        commandActions.put("leave", player -> {
            //Logic for command argument leave goes here
            player.sendMessage(new TextComponent("leave"));
        });

        commandActions.put("join", player -> {
            //Logic for command argument join goes here
            player.sendMessage(new TextComponent("join"));
        });
    }
}