package com.minecolonies.chatchaindc.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.minecolonies.chatchainconnect.ChatChainConnectAPI;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.APIModule;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import com.minecolonies.chatchaindc.modules.api.config.ClientConfigs;
import com.minecolonies.chatchaindc.util.SerializeUtils;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.message.IChatChainConnectMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        for (final JsonElement element : message.getArguments())
        {
            chatChainDC.getLogger().info("Message" + element);
        }
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final User user = new Gson().fromJson(message.getArguments()[3], User.class);
        final String sentMessage = message.getArguments()[4].getAsString();

        if (chatChainDC.getClientConfigs().clientConfigs.containsKey(clientName)
              && chatChainDC.getJda() != null
              && chatChainDC.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && chatChainDC.getClientConfigs().clientTypesConfig.get(clientType)
              && clientType.equalsIgnoreCase("ChatChainMC"))
        {
            ClientConfigs.ClientConfig clientConfig = chatChainDC.getClientConfigs().clientConfigs.get(clientName);
            if (clientConfig.display)
                for (final String channel : clientConfig.channels.keySet())
                {
                    if (clientConfig.channels.get(channel).contains(channelName.toLowerCase()))
                    {
                        try
                        {
                            final List<Webhook> webhooks = chatChainDC.getJda().getTextChannelById(channel).getWebhooks().submit().get();
                            for (final Webhook webhook : webhooks)
                            {
                                if (webhook.getName().equals("Test Webhook"))
                                {
                                    final WebhookClientBuilder builder = webhook.newClient();
                                    final WebhookClient webhookClient = builder.build();

                                    final WebhookMessage webhookMessage = new WebhookMessageBuilder()
                                                                            .setUsername(user.getName())
                                                                            //"https://crafatar.com/avatars/2abe5404-4a2f-4e30-b3ff-671696227a90"
                                                                            .setAvatarUrl(user.getAvatarURL())
                                                                            .setContent(sentMessage)
                                                                            .build();

                                    webhookClient.send(webhookMessage);
                                }
                            }
                        }
                        catch (InterruptedException | ExecutionException e)
                        {
                            chatChainDC.getLogger().error("Could not get webHooks for channel ", channel);
                            chatChainDC.getLogger().error("Stack Trace: ", e);
                        }
                    }
                }
        }
        else
        {
            final Message discordMessage = new MessageBuilder((chatChainDC.getReplacementsUtil().genericMessage(clientType,
              clientName,
              channelName,
              user.getName(),
              sentMessage))).build();

            sendMessage(clientType, clientName, channelName, discordMessage);
        }
    }

    public void genericJoinEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final User user = new Gson().fromJson(message.getArguments()[3], User.class);

        final Message discordMessage = new MessageBuilder(chatChainDC.getReplacementsUtil().genericJoin(clientType,
          clientName,
          channelName,
          user.getName())).build();

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericLeaveEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final User user = new Gson().fromJson(message.getArguments()[3], User.class);

        final Message discordMessage = new MessageBuilder(chatChainDC.getReplacementsUtil().genericLeave(clientType,
          clientName,
          channelName,
          user.getName())).build();

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
