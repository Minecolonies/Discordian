package co.chatchain.dc.configs;

import co.chatchain.dc.Constants;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class FormattingConfig extends AbstractConfig
{

    @Setting("generic-messages_comment")
    private String genericMessageComment = "Template options:\n" +
            Constants.GROUP_NAME + " - The message's group's name\n" +
            Constants.GROUP_ID + " - The message's group's ID\n" +
            Constants.USER_NAME + " - Name of the user who sent the message\n" +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the message\n" +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the message\n" +
            Constants.MESSAGE + " - The message that was sent";

    @Getter
    @Setting("generic-message-formats")
    private Map<String, String> genericMessageFormats = new HashMap<>();

    @Setting("default-generic-message-format")
    @Getter
    private String defaultGenericMessageFormat = "[{group-name}] [{sending-client-name}] <{user-name}>: {message}";

}
