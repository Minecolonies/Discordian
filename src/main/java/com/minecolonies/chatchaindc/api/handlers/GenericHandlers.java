package com.minecolonies.chatchaindc.api.handlers;

import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.APIModule;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import com.minecolonies.chatchaindc.modules.api.config.ClientConfigs;
import com.minecolonies.chatchaindc.util.SerializeUtils;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.message.IChatChainConnectMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handlers for all our generic API messages.
 */
public class GenericHandlers
{

    private final ChatChainDC chatChainDC;

    public GenericHandlers(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    public void sendMessage(final String clientType, final String clientName, final String channelName, final Message message)
    {
        final APIConfig apiConfig = (APIConfig) chatChainDC.getConfigUtils().get(APIModule.ID);

        if (clientName.equalsIgnoreCase(apiConfig.clientName))
        {
            return;
        }

        chatChainDC.getClientConfigs().load();
        if (chatChainDC.getClientConfigs().clientConfigs.containsKey(clientName)
              && chatChainDC.getJda() != null
              && chatChainDC.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && chatChainDC.getClientConfigs().clientTypesConfig.get(clientType))
        {
            ClientConfigs.ClientConfig clientConfig = chatChainDC.getClientConfigs().clientConfigs.get(clientName);
            if (clientConfig.display)
            {
                for (final String channel : clientConfig.channels.keySet())
                {
                    if (clientConfig.channels.get(channel).contains(channelName.toLowerCase()))
                    {
                        chatChainDC.getLogger().info("Message: {}", message.getContentRaw());
                        chatChainDC.getJda().getTextChannelById(channel).sendMessage(message).submit();
                    }
                }
            }
        }

        if (!chatChainDC.getClientConfigs().clientConfigs.containsKey(clientName))
        {
            chatChainDC.getLogger().info("Adding New Client: {}", clientName);
            ClientConfigs.ClientConfig clientConfig = new ClientConfigs.ClientConfig();
            clientConfig.channels = new HashMap<>();
            clientConfig.display = false;

            chatChainDC.getClientConfigs().clientConfigs.put(clientName, clientConfig);

            chatChainDC.getClientConfigs().save();
        }

        if (!chatChainDC.getClientConfigs().clientTypesConfig.containsKey(clientType))
        {
            chatChainDC.getLogger().info("Adding New ClientType: {}", clientType);
            chatChainDC.getClientConfigs().clientTypesConfig.put(clientType, true);
            chatChainDC.getClientConfigs().save();
        }
    }

    public void genericConnectionEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final Message discordMessage = new MessageBuilder(chatChainDC.getReplacementsUtil().genericConnection(clientType,
          clientName,
          channelName)).build();

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericDisconnectionEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final Message discordMessage = new MessageBuilder(chatChainDC.getReplacementsUtil().genericDisconnection(clientType,
          clientName,
          channelName)).build();

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericMessageEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final String user = message.getArguments()[3].getAsString();
        final String sentMessage = message.getArguments()[4].getAsString();

        final Message discordMessage = new MessageBuilder((chatChainDC.getReplacementsUtil().genericMessage(clientType,
          clientName,
          channelName,
          user,
          sentMessage))).build();

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericJoinEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final String user = message.getArguments()[3].getAsString();

        final Message discordMessage = new MessageBuilder(chatChainDC.getReplacementsUtil().genericJoin(clientType,
          clientName,
          channelName,
          user)).build();

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericLeaveEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final String user = message.getArguments()[3].getAsString();

        final Message discordMessage = new MessageBuilder(chatChainDC.getReplacementsUtil().genericLeave(clientType,
          clientName,
          channelName,
          user)).build();

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public void requestJoined(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String requestedClient = message.getArguments()[2].getAsString();

        final APIConfig apiConfig = (APIConfig) chatChainDC.getConfigUtils().get(APIModule.ID);

        if (requestedClient.equalsIgnoreCase(apiConfig.clientName)
              && chatChainDC.getClientConfigs().clientConfigs.containsKey(clientName)
              && chatChainDC.getClientConfigs().clientConfigs.get(clientName).display
              && chatChainDC.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && chatChainDC.getClientConfigs().clientTypesConfig.get(clientType))
        {
            final ArrayList<String> players = new ArrayList<>();

            for (final Guild guild : chatChainDC.getJda().getGuilds())
            {
                for (final Member member : guild.getMembers())
                {
                    if (member.getOnlineStatus().equals(OnlineStatus.ONLINE))
                    {
                        players.add(member.getEffectiveName());
                    }
                }
            }

            final String serializedPlayers = SerializeUtils.serialize(players, chatChainDC.getLogger());

            if (chatChainDC.getConnection() != null && chatChainDC.getConnection().getConnectionState().equals(ConnectionState.OPEN))
            {
                chatChainDC.getConnection().send("RespondJoined",
                  ChatChainDC.CLIENT_TYPE,
                  apiConfig.clientName,
                  "discord",
                  clientName,
                  serializedPlayers);
            }
        }
    }

    public void respondJoined(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelID = message.getArguments()[2].getAsString();
        final String destinationClient = message.getArguments()[3].getAsString();
        final String serializedList = message.getArguments()[4].getAsString();

        final APIConfig apiConfig = (APIConfig) chatChainDC.getConfigUtils().get(APIModule.ID);

        if (destinationClient.equalsIgnoreCase(apiConfig.clientName))
        {
            final Object object = SerializeUtils.deserialize(serializedList, chatChainDC.getLogger());

            if (object instanceof ArrayList)
            {
                List<String> players = (ArrayList<String>) object;

                final Message discordMessage = new MessageBuilder(players.toString()).build();

                sendMessage(clientType, clientName, channelID, discordMessage);
            }
        }
    }
}
