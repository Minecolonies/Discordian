package com.minecolonies.chatchaindc.modules.api;

import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
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
              && !event.getAuthor().isBot())
        {
            final APIConfig apiConfig = (APIConfig) chatChainDC.getConfigUtils().get(APIModule.ID);
            connection.send("GenericMessageEvent",
              ChatChainDC.CLIENT_TYPE,
              apiConfig.clientName,
              event.getChannel().getId(),
              event.getAuthor().getName(),
              event.getMessage().getContentStripped());
        }
    }
}
