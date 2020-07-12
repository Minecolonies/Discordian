package co.chatchain.dc.utils;

import co.chatchain.commons.core.entities.ClientRank;
import co.chatchain.commons.core.entities.ClientUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserUtils
{

    public static ClientUser getClientUserFromMember(@NotNull final Member member)
    {
        final List<ClientRank> ranks = new ArrayList<>();

        int priority = 0;
        final List<Role> roles = member.getRoles();
        for (final Role role : roles)
        {
            String colourHex = ColourUtils.convertRawToString(role.getColorRaw());

            final String displayName = role.getName().substring(0, 1).toUpperCase() + role.getName().substring(1);
            ranks.add(new ClientRank(role.getName(), role.getId(), priority, displayName, colourHex));

            priority++;
        }
        final String userColourString = ColourUtils.convertRawToString(member.getColorRaw());

        return new ClientUser(member.getUser().getName(), member.getUser().getId(), member.getNickname(), userColourString, ranks);
    }
}
