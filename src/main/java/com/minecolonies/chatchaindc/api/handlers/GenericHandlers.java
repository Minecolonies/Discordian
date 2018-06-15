package com.minecolonies.chatchaindc.api.handlers;

import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.message.IChatChainConnectMessage;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.APIModule;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import com.minecolonies.chatchaindc.modules.api.config.ClientConfigs;
import com.minecolonies.chatchaindc.modules.core.CoreModule;
import com.minecolonies.chatchaindc.modules.core.config.CoreConfig;
import com.minecolonies.chatchaindc.util.SerializeUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

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

    public void sendMessage(final String channel, final Message message)
    {
        chatChainDC.getLogger().info("Message: {}", message.getContentRaw());
        chatChainDC.getJda().getTextChannelById(channel).sendMessage(message).submit();
    }

    public void sendMessage(final String channel, final User user, final String text)
    {
        try
        {
            final CoreConfig coreConfig = (CoreConfig) chatChainDC.getConfigUtils().get(CoreModule.ID);
            final List<Webhook> webhooks = chatChainDC.getJda().getTextChannelById(channel).getWebhooks().submit().get();
            for (final Webhook webhook : webhooks)
            {
                if (webhook.getName().equals(coreConfig.webhookName))
                {
                    final WebhookClientBuilder builder = webhook.newClient();
                    final WebhookClient webhookClient = builder.build();

                    final WebhookMessage webhookMessage = new WebhookMessageBuilder()
                                                            .setUsername(user.getName())
                                                            .setAvatarUrl(user.getAvatarURL())
                                                            .setContent(text)
                                                            .build();

                    webhookClient.send(webhookMessage);
                    webhookClient.close();
                }
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            chatChainDC.getLogger().error("Could not get webHooks for channel ", channel);
            chatChainDC.getLogger().error("Stack Trace: ", e);
        }
    }

    public void checkMessage(
      final String clientType,
      final String clientName,
      final String channelName,
      final Message message)
    {
        checkMessage(clientType, clientName, channelName, message, null, null);
    }

    public void checkMessage(
      final String clientType,
      final String clientName,
      final String channelName,
      final Message message,
      final User user,
      final String text)
    {
        if (text != null && text.equals(""))
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
            if (clientConfig.display
                  && clientConfig.channels.containsKey(channelName))
            {
                for (final String channel : clientConfig.channels.get(channelName))
                {
                    if (clientConfig.useWebhook
                          && user != null
                          && text != null)
                    {
                        sendMessage(channel, user, text);
                    }
                    else
                    {
                        sendMessage(channel, message);
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

        final Message discordMessage = new MessageBuilder(chatChainDC.getTemplateMessages().genericConnection(clientType,
          clientName,
          channelName)).build();

        checkMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericDisconnectionEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final Message discordMessage = new MessageBuilder(chatChainDC.getTemplateMessages().genericDisconnection(clientType,
          clientName,
          channelName)).build();

        checkMessage(clientType, clientName, channelName, discordMessage);
    }

    public void genericMessageEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final User user = User.fromJson(message.getArguments()[3]);
        final String sentMessage = message.getArguments()[4].getAsString();

        final Message discordMessage = new MessageBuilder((chatChainDC.getTemplateMessages().genericMessage(clientType,
          clientName,
          channelName,
          user.getName(),
          sentMessage))).build();

        checkMessage(clientType, clientName, channelName, discordMessage, user, sentMessage);
    }

    public void genericJoinEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final User user = User.fromJson(message.getArguments()[3]);

        final Message discordMessage = new MessageBuilder(chatChainDC.getTemplateMessages().genericJoin(clientType,
          clientName,
          channelName,
          user.getName())).build();

        checkMessage(clientType, clientName, channelName, discordMessage, user, "Joined the Server: " + clientName);
    }

    public void genericLeaveEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final User user = User.fromJson(message.getArguments()[3]);

        final Message discordMessage = new MessageBuilder(chatChainDC.getTemplateMessages().genericLeave(clientType,
          clientName,
          channelName,
          user.getName())).build();

        checkMessage(clientType, clientName, channelName, discordMessage, user, "Left the Server: " + clientName);
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

                checkMessage(clientType, clientName, channelID, discordMessage);
            }
        }
    }
}
