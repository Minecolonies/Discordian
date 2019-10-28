package co.chatchain.dc.configs.formatting;

import co.chatchain.commons.objects.Client;
import co.chatchain.commons.objects.ClientRank;
import co.chatchain.commons.objects.ClientUser;
import co.chatchain.commons.objects.Group;
import co.chatchain.commons.objects.messages.ClientEventMessage;
import co.chatchain.commons.objects.messages.GenericMessageMessage;
import co.chatchain.commons.objects.messages.UserEventMessage;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.formatting.formats.MessageFormats;
import co.chatchain.dc.configs.formatting.replacements.ClientRankReplacements;
import co.chatchain.dc.configs.formatting.replacements.ClientReplacements;
import co.chatchain.dc.configs.formatting.replacements.ClientUserReplacements;
import co.chatchain.dc.configs.formatting.replacements.GroupReplacements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementUtils
{

    private static final Pattern MOTHER_REPLACEMENT = Pattern.compile("\\{.*?}");
    private static final Pattern OR_REPLACEMENT = Pattern.compile("[^\\|\\|]*");

    private final ChatChainDC chatChainDC;

    public ReplacementUtils(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    public String getFormat(final GenericMessageMessage message)
    {
        return getFormat(message.getGroup(), message.getSendingClient(), message.getClientUser(), MessageFormats::getGenericMessage)
                .replace("{message}", message.getMessage());
    }

    public String getFormat(final GenericMessageMessage message, final Client client)
    {
        return getFormat(message.getGroup(), client, message.getClientUser(), MessageFormats::getGenericMessage)
                .replace("{message}", message.getMessage());
    }

    public String getFormat(final ClientEventMessage message)
    {
        return getFormat(message.getGroup(), message.getSendingClient(), null, formats -> formats.getClientEventMessages().get(message.getEvent().toUpperCase()));
    }

    public String getFormat(final UserEventMessage message)
    {
        return getFormat(message.getGroup(), message.getSendingClient(), message.getClientUser(), formats -> formats.getUserEventMessages().get(message.getEvent().toUpperCase()));
    }

    public String getFormat(final Group group, final Client client, final ClientUser user, final FormatAction action)
    {
        final List<String> format = new ArrayList<>();

        if (chatChainDC.getMainConfig().getAdvancedFormatting())
        {
            format.addAll(chatChainDC.getAdvancedFormattingConfig().getOverride(group, client, user, action));
        }
        else
        {
            format.addAll(chatChainDC.getFormattingConfig().getOverride(action));
        }

        final StringBuilder outputStringBuilder = new StringBuilder();

        for (final String formatSection : format)
        {
            final String[] splitString = formatSection.split(MOTHER_REPLACEMENT.pattern());

            final Matcher motherMatcher = MOTHER_REPLACEMENT.matcher(formatSection);

            final StringBuilder motherStringBuilder = new StringBuilder();

            if (splitString.length > 0)
            {
                motherStringBuilder.append(splitString[0]);
            }

            int index = 0;

            boolean skip = false;

            while (motherMatcher.find())
            {
                index++;
                final String motherString = motherMatcher.group().replace("{", "").replace("}", "");

                final String replacementString = getReplacementForSection(group, client, user, motherString);
                if (replacementString == null)
                {
                    skip = true;
                    break;
                }

                motherStringBuilder.append(replacementString);
                if (splitString.length > index)
                    motherStringBuilder.append(splitString[index]);
            }

            if (!skip)
                outputStringBuilder.append(motherStringBuilder.toString());
        }

        return outputStringBuilder.toString();
    }

    private static String getReplacementForSection(final Group group, final Client client, final ClientUser user, final String formatsString)
    {

        final Matcher childMatcher = OR_REPLACEMENT.matcher(formatsString);

        while (childMatcher.find())
        {
            String matchString = childMatcher.group();

            if (!matchString.equals(""))
            {
                if (GroupReplacements.GetFromReplacement(matchString) == null &&
                        ClientReplacements.GetFromReplacement(matchString) == null &&
                        ClientRankReplacements.GetFromReplacement(matchString) == null &&
                        ClientUserReplacements.GetFromReplacement(matchString) == null)
                {
                    if (matchString.equalsIgnoreCase("message"))
                        return "{message}";
                    return matchString;
                }

                String returnString = GroupReplacements.GetReplacementObject(group, matchString);
                if (returnString != null)
                    return returnString;

                returnString = ClientReplacements.GetReplacementObject(client, matchString);
                if (returnString != null)
                    return returnString;

                final List<ClientRank> clientRanks = user.getClientRanks();

                clientRanks.sort(Comparator.comparingInt(ClientRank::getPriority));

                final ClientRank rank = clientRanks.stream().findFirst().orElse(null);

                returnString = ClientRankReplacements.GetReplacementObject(rank, matchString);
                if (returnString != null)
                    return returnString;

                returnString = ClientUserReplacements.GetReplacementObject(user, matchString);
                if (returnString != null)
                    return returnString;
            }
        }

        return null;
    }
}
