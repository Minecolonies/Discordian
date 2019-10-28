package co.chatchain.dc.configs.formatting.replacements;

import co.chatchain.commons.objects.ClientRank;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum ClientRankReplacements
{
    NAME("client-rank-name", ClientRank::getName),
    UID("client-rank-uid", ClientRank::getUniqueId),
    DISPLAY("client-rank-display", ClientRank::getDisplay),
    COLOUR("client-rank-colour", ClientRank::getColour);

    @Getter
    final String replacement;

    @Getter
    final ClientRankReplacementAction action;

    ClientRankReplacements(final String replacement, final ClientRankReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    public String GetReplacementObject(final ClientRank rank)
    {
        if (rank == null)
            return null;

        return this.action.invoke(rank);
    }

    @Nullable
    public static String GetReplacementObject(final ClientRank rank, final String replacementString)
    {
        final ClientRankReplacements rankReplacement = GetFromReplacement(replacementString);

        return rankReplacement == null ? null : rankReplacement.GetReplacementObject(rank);
    }

    @Nullable
    public static ClientRankReplacements GetFromReplacement(final String replacementString)
    {
        for (final ClientRankReplacements rankReplacement : values())
        {
            if (rankReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return rankReplacement;
            }
        }
        return null;
    }

    public interface ClientRankReplacementAction
    {
        String invoke(final ClientRank rank);
    }
}
