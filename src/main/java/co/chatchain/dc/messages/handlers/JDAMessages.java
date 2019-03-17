package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.messages.objects.User;
import co.chatchain.commons.messages.objects.message.GenericMessage;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class JDAMessages extends ListenerAdapter
{

    private final ChatChainDC chatChainDC;

    public JDAMessages(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().getId().equalsIgnoreCase(chatChainDC.getJda().getSelfUser().getId()))
        {
            return;
        }

        for (final String groupId : chatChainDC.getGroupsConfig().getGroupStorage().keySet())
        {
            final GroupConfig groupConfig = chatChainDC.getGroupsConfig().getGroupStorage().get(groupId);

            if (groupConfig.getChannelMapping().contains(event.getChannel().getId()))
            {
                final User user = new User(event.getAuthor().getName());

                final GenericMessage message = new GenericMessage(groupConfig.getGroup(), user, event.getMessage().getContentStripped());

                chatChainDC.getConnection().sendGenericMessage(message);
            }
        }

        //chatChainDC.getConnection().send("ReceiveGenericMessage");
    }
}
