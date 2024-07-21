package com.matthew.plugin.commands;


import com.matthew.plugin.modules.chat.MessageModule;
import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.queue.events.PlayerPriorityQueueJoinEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;


public class MyCommand extends Command {

    private final Plugin plugin;

    private final MessageModule chatModule = ModuleManager.getInstance().getRegisteredModule(MessageModule.class);

    private final QueueModule queueModule = ModuleManager.getInstance().getRegisteredModule(QueueModule.class);

    public MyCommand(Plugin plugin) {
        super("testing");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        String message = chatModule.buildMessage("test", player.getName());

        queueModule.getQueue(message).addPlayer(player);

        //Message should actually be the server name but that's fine for now
        PlayerPriorityQueueJoinEvent event = new PlayerPriorityQueueJoinEvent(player, message);
        plugin.getProxy().getPluginManager().callEvent(event);

        player.sendMessage(new TextComponent(message));
    }
}
