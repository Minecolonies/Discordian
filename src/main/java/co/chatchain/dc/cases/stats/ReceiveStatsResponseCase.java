package co.chatchain.dc.cases.stats;

import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.messages.stats.StatsResponseMessage;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsResponseCase;
import co.chatchain.dc.ChatChainDC;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.inject.Inject;
import java.util.Arrays;

public class ReceiveStatsResponseCase implements IReceiveStatsResponseCase
{

    private final ChatChainDC chatChainDC;

    @Inject
    public ReceiveStatsResponseCase(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    public boolean handle(final StatsResponseMessage message)
    {
        final String channelId = chatChainDC.getStatsRequestsCache().getIfPresent(message.getRequestId());
        if (channelId != null)
        {
            TextChannel channel = chatChainDC.getJda().getTextChannelById(channelId);

            if (channel.canTalk())
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

            chatChainDC.getStatsRequestsCache().invalidate(message.getRequestId());
        }
        return false;
    }
}
