package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.messages.*;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import co.chatchain.dc.configs.GroupsConfig;

public class APIMessages
{

    private final ChatChainDC chatChainDC;

    public APIMessages(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    public void ReceiveGenericMessage(final GenericMessage message)
    {
        if (!chatChainDC.getGroupsConfig().getGroupStorage().containsKey(message.getGroup().getGroupId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(message.getGroup());
            chatChainDC.getGroupsConfig().getGroupStorage().put(message.getGroup().getGroupId(), config);
            chatChainDC.getGroupsConfig().save();
        }

        String messageToSend = chatChainDC.getFormattingConfig().getGenericMessage(chatChainDC, message);

        if (messageToSend == null)
        {
            return;
        }

        System.out.println(messageToSend);

        for (final String channelId : chatChainDC.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId()).getChannelMapping())
        {
            if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
            {
                chatChainDC.getJda().getTextChannelById(channelId).sendMessage(messageToSend).queue();
            }
        }
    }

    public void ReceiveClientEvent(final ClientEventMessage message)
    {
        for (final String groupId : chatChainDC.getGroupsConfig().getClientEventGroups())
        {
            if (chatChainDC.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                final GroupConfig groupConfig = chatChainDC.getGroupsConfig().getGroupStorage().get(groupId);
                final String messageToSend = chatChainDC.getFormattingConfig().getClientEventMessage(chatChainDC, message, groupConfig.getGroup());

                if (messageToSend == null)
                {
                    return;
                }

                for (final String channelId : groupConfig.getChannelMapping())
                {
                    if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
                    {
                        chatChainDC.getJda().getTextChannelById(channelId).sendMessage(messageToSend).queue();
                    }
                }
            }
        }
    }

    public void ReceiveUserEvent(final UserEventMessage message)
    {
        for (final String groupId : chatChainDC.getGroupsConfig().getUserEventGroups())
        {
            if (chatChainDC.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                final GroupConfig groupConfig = chatChainDC.getGroupsConfig().getGroupStorage().get(groupId);
                final String messageToSend = chatChainDC.getFormattingConfig().getUserEventMessage(chatChainDC, message, groupConfig.getGroup());

                if (messageToSend == null)
                {
                    return;
                }

                for (final String channelId : groupConfig.getChannelMapping())
                {
                    if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
                    {
                        chatChainDC.getJda().getTextChannelById(channelId).sendMessage(messageToSend).queue();
                    }
                }
            }
        }
    }

    public void ReceiveGroups(final GetGroupsResponse message)
    {
        final GroupsConfig groupsConfig = chatChainDC.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getGroupId()))
            {
                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                groupsConfig.getGroupStorage().put(group.getGroupId(), config);
            }
        }
        groupsConfig.save();
    }

    public void ReceiveClient(final GetClientResponse message)
    {
        chatChainDC.setClient(message.getClient());
    }


}
