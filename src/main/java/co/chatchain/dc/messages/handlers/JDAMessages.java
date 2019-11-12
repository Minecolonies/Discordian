package co.chatchain.dc.messages.handlers;

import co.chatchain.commons.core.entities.ClientRank;
import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.requests.GenericMessageRequest;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
        if (event.getAuthor().getId().equalsIgnoreCase(chatChainDC.getJda().getSelfUser().getId()))
        {
            return;
        }

        final List<ClientRank> ranks = new ArrayList<>();

        int priority = 0;

        if (event.getMember() != null)
        {
            final List<Role> roles = event.getMember().getRoles();
            if (roles != null)
            {
                for (final Role role : roles)
                {
                    final int roleColor = role.getColorRaw();

                    final int red = (roleColor >> 16) & 0xFF;
                    final int green = (roleColor >> 8) & 0xFF;
                    final int blue = roleColor & 0xFF;

                    String colourHex = String.format("#%02x%02x%02x", red, green, blue);

                    final String displayName = role.getName().substring(0, 1).toUpperCase() + role.getName().substring(1);
                    ranks.add(new ClientRank(role.getName(), role.getId(), priority, displayName, colourHex));

                    priority++;
                }
            }
        }

        for (final Map.Entry<String, GroupConfig> groupEntry : chatChainDC.getGroupsConfig().getGroupStorage().entrySet())
        {
            if (!groupEntry.getValue().isSendChat())
                continue;

            if (groupEntry.getValue().getChannelMapping().contains(event.getChannel().getId()))
            {
                String userColour = null;
                final Color userColourObject = event.getMember().getColor();

                if (userColourObject != null)
                {
                    userColour = String.format("#%02x%02x%02x", userColourObject.getRed(), userColourObject.getGreen(), userColourObject.getBlue());
                }
                if (event.getGuild().getId().equals("453039954386223145"))
                    System.out.println("User Colour: " + userColour);

                final ClientUser user = new ClientUser(event.getAuthor().getName(), event.getAuthor().getId(), event.getMember().getNickname(), userColour, ranks);

                final GenericMessageRequest request = new GenericMessageRequest(groupEntry.getValue().getGroup().getId(), event.getMessage().getContentStripped(), user);

                chatChainDC.getConnection().sendGenericMessage(request);
            }
        }
    }
}
