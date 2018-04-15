package com.minecolonies.discordianbot.modules.api;

import com.minecolonies.discordianbot.DiscordianBot;
import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianconnect.api.connection.IDiscordianConnectConnection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{

    private final DiscordianBot discordianBot;

    public MessageListener(final DiscordianBot discordianBot)
    {
        this.discordianBot = discordianBot;
        this.discordianBot.getLogger().info("MessageListener Started!");
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event)
    {

        IDiscordianConnectConnection connection = discordianBot.getConnection();

        if (connection != null
              && event.getChannel().getId().equalsIgnoreCase("377078011394260994")
              && connection.getConnectionState() == ConnectionState.OPEN)
        {
            discordianBot.getLogger().info("Listener: {}", event.getMessage().getContentStripped());
            connection.send("DiscordChatMessage", event.getAuthor().getName(), event.getChannel().getId(), event.getMessage().getContentStripped()); //, event.getChannel().getId()
        }

    }

}
