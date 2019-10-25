package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.messages.objects.ClientRank;
import co.chatchain.commons.messages.objects.User;
import co.chatchain.commons.messages.objects.messages.GenericMessage;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

            if (!groupConfig.isSendChat())
                continue;

            final List<ClientRank> ranks = new ArrayList<>();

            int priority = 0;

            if (event.getMember() != null && event.getMember().getRoles() != null)
            {
                for (final Role role : event.getMember().getRoles())
                {
                    String colourHex = null;
                    if (role.getColor() != null)
                    {
                        colourHex = String.format("#%02x%02x%02x", role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue());
                    }
                    final String displayName = role.getName().substring(0, 1).toUpperCase() + role.getName().substring(1);
                    ranks.add(new ClientRank(role.getName(), role.getId(), priority, displayName, colourHex));

                    priority++;
                }
            }

            if (groupConfig.getChannelMapping().contains(event.getChannel().getId()))
            {
                String userColour = null;
                final Color userColourObject = event.getMember().getColor();

                if (userColourObject != null)
                {
                    userColour = String.format("#%02x%02x%02x", userColourObject.getRed(), userColourObject.getGreen(), userColourObject.getBlue());
                }
                if (event.getGuild().getId().equals("453039954386223145"))
                    System.out.println("User Colour: " + userColour);

                final User user = new User(event.getAuthor().getName(), event.getAuthor().getId(), event.getMember().getNickname(), userColour, ranks);

                final GenericMessage message = new GenericMessage(groupConfig.getGroup(), user, event.getMessage().getContentStripped());

                chatChainDC.getConnection().sendGenericMessage(message);
            }
        }
    }
}
