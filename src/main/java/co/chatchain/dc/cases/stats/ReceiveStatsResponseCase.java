package co.chatchain.dc.cases.stats;

import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.messages.stats.StatsResponseMessage;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.commons.core.interfaces.formatters.stats.IStatsResponseFormatter;
import co.chatchain.commons.interfaces.IChatChainHubConnection;
import co.chatchain.dc.ChatChainDC;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.inject.Inject;
import java.util.Arrays;

public class ReceiveStatsResponseCase extends co.chatchain.commons.core.cases.stats.ReceiveStatsResponseCase
{
    private final ChatChainDC             chatChainDC;

    @Inject
    public ReceiveStatsResponseCase(final IChatChainHubConnection chatChainHubConnection, final IMessageSender messageSender, final IStatsResponseFormatter statsResponseFormatter, final ChatChainDC chatChainDC)
    {
        super(chatChainHubConnection, messageSender, statsResponseFormatter);
        this.chatChainDC = chatChainDC;
    }

    @Override
    public boolean handle(final StatsResponseMessage message)
    {
        if (!chatChainDC.getMainConfig().getStatsEmbed())
        {
            return super.handle(message);
        }

        final String responseLocation = chatChainHubConnection.getStatsRequest(message.getRequestId());
        if (responseLocation != null)
        {
            TextChannel channel = chatChainDC.getJda().getTextChannelById(responseLocation);

            if (chatChainDC.getJda().getTextChannelById(responseLocation).canTalk())
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                MessageBuilder messageBuilder = new MessageBuilder();

                embedBuilder.setAuthor(message.getSendingClient().getName());
                embedBuilder.setDescription(message.getSendingClient().getDescription());
                embedBuilder.setColor(123456);

                if (message.getStatsObject().getOnlineUsers() != null)
                {
                    embedBuilder.addField("Online Users", Arrays.toString(message.getStatsObject().getOnlineUsers().stream().map(ClientUser::getName).toArray()), false);
                    messageBuilder.append("online: ").append(Arrays.toString(message.getStatsObject().getOnlineUsers().stream().map(ClientUser::getName).toArray()));
                }

                if (message.getStatsObject().getOnlineUsers() != null && message.getStatsObject().getPerformance() != null)
                {
                    messageBuilder.append("\n");
                }

                if (message.getStatsObject().getPerformance() != null)
                {
                    embedBuilder.addField("Performance", message.getStatsObject().getPerformance(), true);
                    embedBuilder.addField("Performance Name", message.getStatsObject().getPerformanceName(), true);
                    messageBuilder.append("performance: ").append(message.getStatsObject().getPerformance()).append(" ").append(message.getStatsObject().getPerformanceName());
                }

                channel.sendMessage(embedBuilder.build()).queue();
            }
        }
        return false;
    }
}
