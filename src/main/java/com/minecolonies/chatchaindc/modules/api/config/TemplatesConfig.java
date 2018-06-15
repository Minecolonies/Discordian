package com.minecolonies.chatchaindc.modules.api.config;

import com.minecolonies.chatchaindc.qsml.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class TemplatesConfig extends BaseConfig
{
    @Setting(value = "generic-connection-placeholder", comment = "\nPlaceholder for when a new client connects.")
    public String genericConnection = "{client-name} of {client-type} connected!";

    @Setting(value = "generic-disconnection-placeholder", comment = "\nPlaceholder for when a new client disconnects.")
    public String genericDisconnection = "{client-name} of {client-type} disconnected!";

    @Setting(value = "generic-message-placeholder", comment = "\nPlaceholder for generic messages sent over connection.")
    public String genericMessage = "[{client-name}] **{user-name}**: {user-message}";

    @Setting(value = "generic-join-placeholder", comment = "\nPlaceholder for a generic user join.")
    public String genericJoin = "[{client-name}] **{user-name}** joined!";

    @Setting(value = "generic-leave-placeholder", comment = "\nPlaceholder for a generic user leave.")
    public String genericLeave = "[{client-name}] **{user-name}** left!";

    @Setting(value = "client-type-template-overrides", comment = "Override for specific ClientType placeholders in here.")
    public Map<String, TypeOverridesConfig> clientTypeOverrides = new HashMap<>();

    @Setting(value = "client-template-overrides", comment = "Overrides for specific Client placeholders in here.")
    public Map<String, ClientOverridesConfig> clientOverrides = new HashMap<>();

    @Setting(value = "channel-template-overrides", comment = "Override for specific channel placeholders in here.")
    public Map<String, ChannelOverridesConfig> channelOverrides = new HashMap<>();

    @Setting(value = "username-template-overrides", comment = "Override for specific username placeholders in here. Not Reliable!")
    public Map<String, UserOverridesConfig> usernameOverrides = new HashMap<>();

    @ConfigSerializable
    public static class TypeOverridesConfig extends BaseConfig
    {
        @Setting(value = "client-template-overrides", comment = "Overrides for specific Client placeholders in here.")
        public Map<String, ClientOverridesConfig> clientOverrides = new HashMap<>();

        @Setting(value = "channel-template-overrides", comment = "Override for specific channel placeholders in here.")
        public Map<String, ChannelOverridesConfig> channelOverrides = new HashMap<>();

        @Setting(value = "username-template-overrides", comment = "Override for specific username placeholders in here. Not Reliable!")
        public Map<String, UserOverridesConfig> usernameOverrides = new HashMap<>();

        @Setting(value = "generic-connection-placeholder", comment = "Placeholder for when a new client connects.")
        public String genericConnection;

        @Setting(value = "generic-disconnection-placeholder", comment = "Placeholder for when a new client disconnects.")
        public String genericDisconnection;

        @Setting(value = "generic-message-placeholder", comment = "Placeholder for generic messages sent over connection.")
        public String genericMessage;

        @Setting(value = "generic-join-placeholder", comment = "Placeholder for a generic user join.")
        public String genericJoin;

        @Setting(value = "generic-leave-placeholder", comment = "Placeholder for a generic user leave.")
        public String genericLeave;
    }

    @ConfigSerializable
    public static class ClientOverridesConfig extends BaseConfig
    {
        @Setting(value = "channel-template-overrides", comment = "Override for specific channel placeholders in here.")
        public Map<String, ChannelOverridesConfig> channelOverrides = new HashMap<>();

        @Setting(value = "username-template-overrides", comment = "Override for specific username placeholders in here. Not Reliable!")
        public Map<String, UserOverridesConfig> usernameOverrides = new HashMap<>();

        @Setting(value = "generic-connection-placeholder", comment = "Placeholder for when a new client connects.")
        public String genericConnection;

        @Setting(value = "generic-disconnection-placeholder", comment = "Placeholder for when a new client disconnects.")
        public String genericDisconnection;

        @Setting(value = "generic-message-placeholder", comment = "Placeholder for generic messages sent over connection.")
        public String genericMessage;

        @Setting(value = "generic-join-placeholder", comment = "Placeholder for a generic user join.")
        public String genericJoin;

        @Setting(value = "generic-leave-placeholder", comment = "Placeholder for a generic user leave.")
        public String genericLeave;
    }

    @ConfigSerializable
    public static class ChannelOverridesConfig extends BaseConfig
    {
        @Setting(value = "username-template-overrides", comment = "Override for specific username placeholders in here. Not Reliable!")
        public Map<String, UserOverridesConfig> usernameOverrides = new HashMap<>();

        @Setting(value = "generic-connection-placeholder", comment = "Placeholder for when a new client connects.")
        public String genericConnection;

        @Setting(value = "generic-disconnection-placeholder", comment = "Placeholder for when a new client disconnects.")
        public String genericDisconnection;

        @Setting(value = "generic-message-placeholder", comment = "Placeholder for generic messages sent over connection.")
        public String genericMessage;

        @Setting(value = "generic-join-placeholder", comment = "Placeholder for a generic user join.")
        public String genericJoin;

        @Setting(value = "generic-leave-placeholder", comment = "Placeholder for a generic user leave.")
        public String genericLeave;
    }

    @ConfigSerializable
    public static class UserOverridesConfig extends BaseConfig
    {
        @Setting(value = "generic-connection-placeholder", comment = "Placeholder for when a new client connects.")
        public String genericConnection;

        @Setting(value = "generic-disconnection-placeholder", comment = "Placeholder for when a new client disconnects.")
        public String genericDisconnection;

        @Setting(value = "generic-message-placeholder", comment = "Placeholder for generic messages sent over connection.")
        public String genericMessage;

        @Setting(value = "generic-join-placeholder", comment = "Placeholder for a generic user join.")
        public String genericJoin;

        @Setting(value = "generic-leave-placeholder", comment = "Placeholder for a generic user leave.")
        public String genericLeave;
    }
}
