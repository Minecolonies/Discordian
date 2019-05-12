package co.chatchain.dc.configs.formatting.replacements;

import co.chatchain.commons.messages.objects.Client;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum ClientReplacements
{
    NAME("client-name", Client::getClientName),
    ID("client-id", Client::getClientId),
    GUID("client-guid", Client::getClientGuid),
    OWNER_ID("client-owner-id", Client::getOwnerId);

    @Getter
    final String replacement;

    @Getter
    final ClientReplacementAction action;

    ClientReplacements(final String replacement, final ClientReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    public String GetReplacementObject(final Client client)
    {
        if (client == null)
            return null;

        return this.action.invoke(client);
    }

    @Nullable
    public static String GetReplacementObject(final Client client, final String replacementString)
    {
        final ClientReplacements clientReplacement = GetFromReplacement(replacementString);

        return clientReplacement == null ? null : clientReplacement.GetReplacementObject(client);
    }

    @Nullable
    public static ClientReplacements GetFromReplacement(final String replacementString)
    {
        for (final ClientReplacements clientReplacement : values())
        {
            if (clientReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return clientReplacement;
            }
        }
        return null;
    }

    public interface ClientReplacementAction
    {
        String invoke(final Client client);
    }
}
