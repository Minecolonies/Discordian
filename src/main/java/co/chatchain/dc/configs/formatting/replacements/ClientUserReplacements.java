package co.chatchain.dc.configs.formatting.replacements;

import co.chatchain.commons.objects.ClientUser;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum ClientUserReplacements
{
    NAME("client-user-name", ClientUser::getName),
    UID("client-user-uid", ClientUser::getUniqueId),
    NICKNAME("client-user-nickname", ClientUser::getNickName),
    COLOUR("client-user-colour", ClientUser::getColour);

    @Getter
    final String replacement;

    @Getter
    final UserReplacementAction action;

    ClientUserReplacements(final String replacement, final UserReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    public String GetReplacementObject(final ClientUser user)
    {
        if (user == null)
            return null;

        return this.action.invoke(user);
    }

    @Nullable
    public static String GetReplacementObject(final ClientUser user, final String replacementString)
    {
        final ClientUserReplacements userReplacement = GetFromReplacement(replacementString);

        return userReplacement == null ? null : userReplacement.GetReplacementObject(user);
    }

    @Nullable
    public static ClientUserReplacements GetFromReplacement(final String replacementString)
    {
        for (final ClientUserReplacements userReplacement : values())
        {
            if (userReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return userReplacement;
            }
        }
        return null;
    }

    public interface UserReplacementAction
    {
        String invoke(final ClientUser user);
    }
}
