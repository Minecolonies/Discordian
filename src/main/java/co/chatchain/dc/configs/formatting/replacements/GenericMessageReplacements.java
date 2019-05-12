package co.chatchain.dc.configs.formatting.replacements;

import co.chatchain.commons.messages.objects.messages.GenericMessage;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum GenericMessageReplacements
{
    MESSAGE("message", GenericMessage::getMessage);

    @Getter
    final String replacement;

    @Getter
    final GenericMessageReplacementAction action;

    GenericMessageReplacements(final String replacement, final GenericMessageReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    public String GetReplacementObject(final GenericMessage genericMessage)
    {
        if (genericMessage == null)
            return null;

        return this.action.invoke(genericMessage);
    }

    @Nullable
    public static String GetReplacementObject(final GenericMessage genericMessage, final String replacementString)
    {
        final GenericMessageReplacements genericMessageReplacement = GetFromReplacement(replacementString);

        return genericMessageReplacement == null ? null : genericMessageReplacement.GetReplacementObject(genericMessage);
    }

    @Nullable
    public static GenericMessageReplacements GetFromReplacement(final String replacementString)
    {
        for (final GenericMessageReplacements genericMessageReplacement : values())
        {
            if (genericMessageReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return genericMessageReplacement;
            }
        }
        return null;
    }

    public interface GenericMessageReplacementAction
    {
        String invoke(final GenericMessage genericMessage);
    }

}
