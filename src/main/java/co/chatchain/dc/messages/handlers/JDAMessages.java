package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.requests.GenericMessageRequest;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import co.chatchain.dc.utils.UserUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;

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
        if (event.getMessage().getContentRaw().startsWith("!stats"))
            return;

        if (event.getMember() == null)
            return;

        if (event.getAuthor().getId().equalsIgnoreCase(chatChainDC.getJda().getSelfUser().getId()))
        {
            return;
        }

        final ClientUser user = UserUtils.getClientUserFromMember(event.getMember());

        for (final Map.Entry<String, GroupConfig> groupEntry : chatChainDC.getGroupsConfig().getGroupStorage().entrySet())
        {
            if (!groupEntry.getValue().isSendChat())
                continue;

            if (groupEntry.getValue().getChannelMapping().contains(event.getChannel().getId()))
            {
                final GenericMessageRequest request = new GenericMessageRequest(groupEntry.getKey(), event.getMessage().getContentStripped(), user);

                chatChainDC.getConnection().sendGenericMessage(request);
            }
        }
    }
}
