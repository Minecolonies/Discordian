package co.chatchain.dc.configs.formatting.replacements;

import co.chatchain.commons.objects.Group;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum GroupReplacements
{
    NAME("group-name", Group::getName),
    ID("group-id", Group::getId),
    OWNER_ID("group-owner-id", Group::getOwnerId);

    @Getter
    final String replacement;

    @Getter
    final GroupReplacementAction action;

    GroupReplacements(final String replacement, final GroupReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    public String GetReplacementObject(final Group group)
    {
        if (group == null)
            return null;

        return this.action.invoke(group);
    }

    @Nullable
    public static String GetReplacementObject(final Group group, final String replacementString)
    {
        final GroupReplacements groupReplacement = GetFromReplacement(replacementString);

        return groupReplacement == null ? null : groupReplacement.GetReplacementObject(group);
    }

    @Nullable
    public static GroupReplacements GetFromReplacement(final String replacementString)
    {
        for (final GroupReplacements groupReplacement : values())
        {
            if (groupReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return groupReplacement;
            }
        }
        return null;
    }

    public interface GroupReplacementAction
    {
        String invoke(final Group group);
    }
}
