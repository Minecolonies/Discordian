package co.chatchain.dc.configs.formatting.replacements;

import co.chatchain.commons.messages.objects.User;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum ClientUserReplacements
{
    NAME("client-user-name", User::getName),
    UID("client-user-uid", User::getUniqueId),
    NICKNAME("client-user-nickname", User::getNickName),
    COLOUR("client-user-colour", User::getColour);

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
    public String GetReplacementObject(final User user)
    {
        if (user == null)
            return null;

        return this.action.invoke(user);
    }

    @Nullable
    public static String GetReplacementObject(final User user, final String replacementString)
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
        String invoke(final User user);
    }
}
