package co.chatchain.dc.messages.handlers;

import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.messages.objects.GenericMessage;
import co.chatchain.dc.messages.objects.Group;
import co.chatchain.dc.messages.objects.User;
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
            final Group group = chatChainDC.getGroupsConfig().getGroupStorage().get(groupId);

            if (group.getChannelMapping().contains(event.getChannel().getId()))
            {
                final User user = new User(event.getAuthor().getName());

                final GenericMessage message = new GenericMessage(group, user, event.getMessage().getContentStripped());

                chatChainDC.getConnection().send("SendGenericMessage", message);
            }
        }

        //chatChainDC.getConnection().send("ReceiveGenericMessage");
    }
}
