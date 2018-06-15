package com.minecolonies.chatchaindc.modules.api;

import com.google.gson.Gson;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{

    private final ChatChainDC chatChainDC;

    public MessageListener(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
        this.chatChainDC.getLogger().info("MessageListener Started!");
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event)
    {
        IChatChainConnectConnection connection = chatChainDC.getConnection();

        if (connection != null
              && connection.getConnectionState() == ConnectionState.OPEN
              && event.getMember() != null
              && !event.getAuthor().equals(chatChainDC.getJda().getSelfUser()))
        {
            final APIConfig apiConfig = (APIConfig) chatChainDC.getConfigUtils().get(APIModule.ID);

            final User user = new User();
            if (event.getMember().getNickname() != null)
            {
                user.setName(event.getMember().getNickname());
            }
            else
            {
                user.setName(event.getAuthor().getName());
            }

            user.setAvatarURL(event.getAuthor().getEffectiveAvatarUrl());

            connection.send("GenericMessageEvent",
              ChatChainDC.CLIENT_TYPE,
              apiConfig.clientName,
              event.getChannel().getId(),
              new Gson().toJson(user),
              event.getMessage().getContentStripped());
        }
    }
}
