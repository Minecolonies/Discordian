package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;

import javax.inject.Inject;

public class MessageSender implements IMessageSender
{
    private final ChatChainDC chatChainDC;

    @Inject
    public MessageSender(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    private void createGroupInConfig(final Group group)
    {
        if (!chatChainDC.getGroupsConfig().getGroupStorage().containsKey(group.getId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);
            chatChainDC.getGroupsConfig().getGroupStorage().put(group.getId(), config);
            chatChainDC.getGroupsConfig().save();
        }
    }

    @Override
    public boolean sendMessage(final String message, final Group group)
    {
        createGroupInConfig(group);

        if (message == null)
            return false;

        for (final String channelId : chatChainDC.getGroupsConfig().getGroupStorage().get(group.getId()).getChannelMapping())
        {
            if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
            {
                chatChainDC.getJda().getTextChannelById(channelId).sendMessage(message).queue();
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

        if (chatChainDC.getJda().getTextChannelById(responseLocation).canTalk())
        {
            chatChainDC.getJda().getTextChannelById(responseLocation).sendMessage(message).queue();
        }

        return false;
    }
}
