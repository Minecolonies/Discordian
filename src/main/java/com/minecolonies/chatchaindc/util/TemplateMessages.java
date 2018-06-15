package com.minecolonies.chatchaindc.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.config.TemplatesConfig;
import com.minecolonies.chatchaindc.modules.api.config.TemplatesConfig.ChannelOverridesConfig;
import com.minecolonies.chatchaindc.modules.api.config.TemplatesConfig.ClientOverridesConfig;
import com.minecolonies.chatchaindc.modules.api.config.TemplatesConfig.TypeOverridesConfig;
import com.minecolonies.chatchaindc.modules.api.config.TemplatesConfig.UserOverridesConfig;

import java.util.Map;

import static com.minecolonies.chatchaindc.modules.api.config.APIConfigReplacements.*;

public final class TemplateMessages
{
    private final ChatChainDC chatChainDC;

    private static final String USERNAME_CONFIG = "username-template-overrides";
    private static final String CHANNEL_CONFIG  = "channel-template-overrides";
    private static final String CLIENT_CONFIG   = "client-template-overrides";
    private static final String TYPE_CONFIG     = "client-type-template-overrides";

    public TemplateMessages(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    public String genericConnection(final String clientType, final String clientName, final String channelName)
    {
        return getMessage("generic-connection-placeholder", clientType, clientName, channelName, "")
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName);
    }

    public String genericDisconnection(final String clientType, final String clientName, final String channelName)
    {
        return getMessage("generic-disconnection-placeholder", clientType, clientName, channelName, "")
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName);
    }

    public String genericMessage(final String clientType, final String clientName, final String channelName, final String user, final String message)
    {
        return getMessage("generic-message-placeholder", clientType, clientName, channelName, user)
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user)
                 .replace(USER_MESSAGE, message);
    }

    public String genericJoin(final String clientType, final String clientName, final String channelName, final String user)
    {
        return getMessage("generic-join-placeholder", clientType, clientName, channelName, user)
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user);
    }

    public String genericLeave(final String clientType, final String clientName, final String channelName, final String user)
    {
        return getMessage("generic-leave-placeholder", clientType, clientName, channelName, user)
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user);
    }

    public String getUserMessage(final JsonObject json, final String configValue, final Map<String, UserOverridesConfig> map, final String userName)
    {
        final JsonObject newJson = json.getAsJsonObject(userName);
        if (map.containsKey(userName)
              && json.has(userName)
              && newJson.has(configValue)
              && !newJson.get(configValue).getAsString().equals(""))
        {
            return newJson.get(configValue).getAsString();
        }
        return "";
    }

    public String getChannelMessage(final JsonObject json, final String configValue, final Map<String, ChannelOverridesConfig> map, final String channelName, final String userName)
    {
        if (map.containsKey(channelName)
              && json.has(channelName))
        {
            final ChannelOverridesConfig config = map.get(channelName);
            final JsonObject newJson = json.getAsJsonObject(channelName);

            if (config.usernameOverrides != null
                  && !getUserMessage(newJson.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName).equals(""))
            {
                return getUserMessage(newJson.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName);
            }

            if (newJson.has(configValue)
                  && !newJson.get(configValue).getAsString().equals(""))
            {
                return newJson.get(configValue).getAsString();
            }
        }
        return "";
    }

    public String getClientMessage(
      final JsonObject json,
      final String configValue,
      final Map<String, ClientOverridesConfig> map,
      final String clientName,
      final String channelName,
      final String userName)
    {
        if (map.containsKey(clientName)
              && json.has(clientName))
        {
            final ClientOverridesConfig config = map.get(clientName);
            final JsonObject newJson = json.getAsJsonObject(clientName);

            if (config.usernameOverrides != null
                  && !getUserMessage(newJson.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName).equals(""))
            {
                return getUserMessage(newJson.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName);
            }

            if (config.channelOverrides != null
                  && !getChannelMessage(newJson.getAsJsonObject(CHANNEL_CONFIG), configValue, config.channelOverrides, channelName, userName).equals(""))
            {
                return getChannelMessage(newJson.getAsJsonObject(CHANNEL_CONFIG), configValue, config.channelOverrides, channelName, userName);
            }

            if (newJson.has(configValue)
                  && !newJson.get(configValue).getAsString().equals(""))
            {
                return newJson.get(configValue).getAsString();
            }
        }
        return "";
    }

    public String getTypeMessage(
      final JsonObject json,
      final String configValue,
      final Map<String, TypeOverridesConfig> map,
      final String clientType,
      final String clientName,
      final String channelName,
      final String userName)
    {
        if (map.containsKey(clientType)
              && json.has(clientType))
        {
            final TypeOverridesConfig config = map.get(clientType);

            final JsonObject newJson = json.getAsJsonObject(clientType);

            if (config.usernameOverrides != null
                  && !getUserMessage(newJson.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName).equals(""))
            {
                return getUserMessage(newJson.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName);
            }

            if (config.channelOverrides != null
                  && !getChannelMessage(newJson.getAsJsonObject(CHANNEL_CONFIG), configValue, config.channelOverrides, channelName, userName).equals(""))
            {
                return getChannelMessage(newJson.getAsJsonObject(CHANNEL_CONFIG), configValue, config.channelOverrides, channelName, userName);
            }

            if (config.clientOverrides != null
                  && !getClientMessage(newJson.getAsJsonObject(CLIENT_CONFIG), configValue, config.clientOverrides, clientName, channelName, userName).equals(""))
            {
                return getClientMessage(newJson.getAsJsonObject(CLIENT_CONFIG), configValue, config.clientOverrides, clientName, channelName, userName);
            }

            if (newJson.has(configValue)
                  && !newJson.get(configValue).getAsString().equals(""))
            {
                return newJson.get(configValue).getAsString();
            }
        }
        return "";
    }

    public String getMessage(final String configValue, final String clientType, final String clientName, final String channelName, final String userName)
    {
        final TemplatesConfig config = chatChainDC.getTemplatesConfig();

        final JsonObject json = new Gson().toJsonTree(config.getNode().getValue()).getAsJsonObject();

        if (config.usernameOverrides.containsKey(userName))
        {
            final String message = getUserMessage(json.getAsJsonObject(USERNAME_CONFIG), configValue, config.usernameOverrides, userName);
            if (!message.equals(""))
            {
                return message;
            }
        }

        if (config.channelOverrides.containsKey(channelName))
        {
            final String message = getChannelMessage(json.getAsJsonObject(CHANNEL_CONFIG), configValue, config.channelOverrides, channelName, userName);
            if (!message.equals(""))
            {
                return message;
            }
        }

        if (config.clientOverrides.containsKey(clientName))
        {
            final String message = getClientMessage(json.getAsJsonObject(CLIENT_CONFIG), configValue, config.clientOverrides, clientName, channelName, userName);
            if (!message.equals(""))
            {
                return message;
            }
        }

        if (config.clientTypeOverrides.containsKey(clientType))
        {
            final String message = getTypeMessage(json.getAsJsonObject(TYPE_CONFIG), configValue, config.clientTypeOverrides, clientType, clientName, channelName, userName);
            if (!message.equals(""))
            {
                return message;
            }
        }

        return json.get(configValue).getAsString();
    }
}
