package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.objects.Group;
import co.chatchain.commons.objects.messages.*;
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

    public void createGroupInConfig(final Group group)
    {
        if (!chatChainDC.getGroupsConfig().getGroupStorage().containsKey(group.getId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);
            chatChainDC.getGroupsConfig().getGroupStorage().put(group.getId(), config);
            chatChainDC.getGroupsConfig().save();
        }
    }

    public void ReceiveGenericMessage(final GenericMessageMessage message)
    {
        createGroupInConfig(message.getGroup());

        String messageToSend = chatChainDC.getReplacementUtils().getFormat(message);

        if (messageToSend == null)
        {
            return;
        }

        for (final String channelId : chatChainDC.getGroupsConfig().getGroupStorage().get(message.getGroup().getId()).getChannelMapping())
        {
            if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
            {
                chatChainDC.getJda().getTextChannelById(channelId).sendMessage(messageToSend).queue();
            }
        }
      
        System.out.println("New Generic Message: " + messageToSend);
    }

    public void ReceiveClientEvent(final ClientEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        String messageToSend = chatChainDC.getReplacementUtils().getFormat(message);

        if (messageToSend == null)
        {
            return;
        }

        for (final String channelId : chatChainDC.getGroupsConfig().getGroupStorage().get(message.getGroup().getId()).getChannelMapping())
        {
            if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
            {
                chatChainDC.getJda().getTextChannelById(channelId).sendMessage(messageToSend).queue();
            }
        }

        System.out.println("New Client Event: " + messageToSend);
    }

    public void ReceiveUserEvent(final UserEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        String messageToSend = chatChainDC.getReplacementUtils().getFormat(message);

        if (messageToSend == null)
        {
            return;
        }

        for (final String channelId : chatChainDC.getGroupsConfig().getGroupStorage().get(message.getGroup().getId()).getChannelMapping())
        {
            if (chatChainDC.getJda().getTextChannelById(channelId).canTalk())
            {
                chatChainDC.getJda().getTextChannelById(channelId).sendMessage(messageToSend).queue();
            }
        }

        System.out.println("New User Event: " + messageToSend);
    }

    public void ReceiveGroups(final GetGroupsMessage message)
    {
        final GroupsConfig groupsConfig = chatChainDC.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getId()))
            {
                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                groupsConfig.getGroupStorage().put(group.getId(), config);
            }
        }
        groupsConfig.save();
    }

    public void ReceiveClient(final GetClientMessage message)
    {
        chatChainDC.setClient(message.getClient());
    }


}
