package co.chatchain.dc.messages.handlers;

import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.messages.objects.GenericMessage;

import static co.chatchain.dc.Constants.*;

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
            chatChainDC.getGroupsConfig().getGroupStorage().put(message.getGroup().getGroupId(), message.getGroup());
            chatChainDC.getGroupsConfig().save();
        }

        String messageToSend;

        if (chatChainDC.getFormattingConfig().getGenericMessageFormats().containsKey(message.getGroup().getGroupId()))
        {
            messageToSend = chatChainDC.getFormattingConfig().getGenericMessageFormats().get(message.getGroup().getGroupId())
                    .replace(GROUP_NAME, message.getGroup().getGroupName())
                    .replace(GROUP_ID, message.getGroup().getGroupId())
                    .replace(USER_NAME, message.getUser().getName())
                    .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                    .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid())
                    .replace(MESSAGE, message.getMessage());
        } else
        {
            messageToSend = chatChainDC.getFormattingConfig().getDefaultGenericMessageFormat()
                    .replace(GROUP_NAME, message.getGroup().getGroupName())
                    .replace(GROUP_ID, message.getGroup().getGroupId())
                    .replace(USER_NAME, message.getUser().getName())
                    .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                    .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid())
                    .replace(MESSAGE, message.getMessage());
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


}
