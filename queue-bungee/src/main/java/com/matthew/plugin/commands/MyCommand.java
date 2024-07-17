package com.matthew.plugin.commands;


import com.matthew.plugin.modules.chat.MessageModule;
import com.matthew.plugin.modules.ModuleManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class MyCommand extends Command {

    private final MessageModule chat = ModuleManager.getInstance().getRegisteredModule(MessageModule.class);

    public MyCommand() {
        super("testing");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        String message = chat.buildMessage("test", player.getName());

        player.sendMessage(new TextComponent(message));
    }
}
