package co.chatchain.dc.configs;

import co.chatchain.commons.messages.objects.Client;
import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.message.ClientEventMessage;
import co.chatchain.commons.messages.objects.message.GenericMessage;
import co.chatchain.commons.messages.objects.message.UserEventMessage;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.Constants;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class FormattingConfig extends AbstractConfig
{

    private String getDefaultOrOverride(final String groupId, final String defaultString, final Map<String, String> overrideStrings)
    {
        if (defaultString.contains(groupId))
        {
            return overrideStrings.get(groupId);
        }
        return defaultString;
    }

    private String getReplacements(final ChatChainDC chatChainDC, final Group group, Client client, final String messageToReplace)
    {
        System.out.println("Client: " + client);
        System.out.println("Message: " + messageToReplace);
        if (client == null)
        {
            client = chatChainDC.getClient();
        }

        return messageToReplace
                .replaceAll("(\\{group-name})", group.getGroupName())
                .replaceAll("(\\{group-id})", group.getGroupId())
                .replaceAll("(\\{sending-client-name})", client.getClientName())
                .replaceAll("(\\{sending-client-guid})", client.getClientGuid());
    }

    @Setting("generic-messages_comment")
    private String genericMessageComment = "Template options:\n" +
            Constants.GROUP_NAME + " - The message's group's name\n" +
            Constants.GROUP_ID + " - The message's group's ID\n" +
            Constants.USER_NAME + " - Name of the user who sent the message\n" +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the message\n" +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the message\n" +
            Constants.MESSAGE + " - The message that was sent";

    @Setting("generic-message-formats")
    private Map<String, String> genericMessageFormats = new HashMap<>();

    @Setting("default-generic-message-format")
    private String defaultGenericMessageFormat = "[{group-name}] [{sending-client-name}] <{user-name}>: {message}";

    public String getGenericMessage(final ChatChainDC chatChainDC, final GenericMessage message)
    {
        if (chatChainDC.getGroupsConfig().getGroupStorage().containsKey(message.getGroup().getGroupId()))
        {
            final Group group = chatChainDC.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId()).getGroup();

            final String defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultGenericMessageFormat, genericMessageFormats);

            return getReplacements(chatChainDC, message.getGroup(), message.getSendingClient(), defaultOrOverride).replaceAll("(\\{user-name})", message.getUser().getName())
                    .replaceAll("(\\{message})", message.getMessage());
        }
        return null;
    }

    @Setting("client-event-formats_comment")
    private String clientEventComment = "Template options: " +
            Constants.GROUP_NAME + " - The message's group's name " +
            Constants.GROUP_ID + " - The message's group's ID " +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the message " +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the message ";

    @Setting("client-start-event-formats")
    private Map<String, String> clientStartEventFormats = new HashMap<>();

    @Setting("default-client-start-event-format")
    private String defaultClientStartEventFormats = "[{group-name}] {sending-client-name} has **connected**";

    @Setting("client-stop-event-formats")
    private Map<String, String> clientStopEventFormats = new HashMap<>();

    @Setting("default-client-stop-event-format")
    private String defaultClientStopEventFormats = "[{group-name}] {sending-client-name} has **disconnected**";

    public String getClientEventMessage(final ChatChainDC chatChainDC, final ClientEventMessage message, final Group group)
    {
        System.out.println("here3!");
        final String defaultOrOverride;
        if (message.getEvent().equalsIgnoreCase("START"))
        {
            defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultClientStartEventFormats, clientStartEventFormats);
        }
        else if (message.getEvent().equalsIgnoreCase("STOP"))
        {
            defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultClientStopEventFormats, clientStopEventFormats);
        }
        else
        {
            return null;
        }

        return getReplacements(chatChainDC, group, message.getClient(), defaultOrOverride);
    }

    @Setting("user-event-formats_comment")
    private String userEventComment = "Template options: " +
            Constants.GROUP_NAME + " - The message's group's name " +
            Constants.GROUP_ID + " - The message's group's ID " +
            Constants.USER_NAME + " - Name of the user who sent the message " +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the message " +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the message ";

    @Getter
    @Setting("user-login-event-formats")
    private Map<String, String> userLoginEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-login-event-format")
    private String defaultUserLoginEventFormats = "[{group-name}] [{sending-client-name}] {user-name} has **logged in**";

    @Getter
    @Setting("user-logout-event-formats")
    private Map<String, String> userLogoutEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-logout-event-format")
    private String defaultUserLogoutEventFormats = "[{group-name}] [{sending-client-name}] {user-name} has **logged out**";

    @Getter
    @Setting("user-death-event-formats")
    private Map<String, String> userDeathEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-death-event-format")
    private String defaultUserDeathEventFormats = "[{group-name}] [{sending-client-name}] {user-name} has **died**";

    public String getUserEventMessage(final ChatChainDC chatChainDC, final UserEventMessage message, final Group group)
    {
        final String defaultOrOverride;
        if (message.getEvent().equalsIgnoreCase("LOGIN"))
        {
            defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultUserLoginEventFormats, userLoginEventFormats);
        }
        else if (message.getEvent().equalsIgnoreCase("LOGOUT"))
        {
            defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultUserLogoutEventFormats, userLogoutEventFormats);
        }
        else if (message.getEvent().equalsIgnoreCase("DEATH"))
        {
            defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultUserDeathEventFormats, userDeathEventFormats);
        }
        else
        {
            return null;
        }

        String stringMessage = getReplacements(chatChainDC, group, message.getClient(), defaultOrOverride);

        return stringMessage.replaceAll("(\\{user-name})", message.getUser().getName());
    }

}
