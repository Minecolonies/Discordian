package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.cases.ReceiveGroupsCase;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.inject.Inject;

public class MessageSender implements IMessageSender
{
    private final ChatChainDC chatChainDC;

    @Inject
    public MessageSender(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    public boolean sendMessage(final String message, final Group group)
    {
        ReceiveGroupsCase.AddGroupToConfig(chatChainDC, group);

        if (message == null)
            return false;

        for (final String channelId : chatChainDC.getGroupsConfig().getGroupStorage().get(group.getId()).getChannelMapping())
        {
            TextChannel channel = chatChainDC.getJda().getTextChannelById(channelId);
            if (channel != null && channel.canTalk())
            {
                channel.sendMessage(message).queue();
            }
        }

        System.out.println("New Message: " + message);
        return true;
    }

    @Override
    public boolean sendStatsMessage(String message, String responseLocation)
    {
        if (message == null)
            return false;

        TextChannel channel = chatChainDC.getJda().getTextChannelById(responseLocation);
        if (channel != null && channel.canTalk())
        {
            channel.sendMessage(message).queue();
        }

        return false;
    }
}
